package org.odk.collect.android.cosmosdb

import android.content.Context
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.odk.collect.android.javarosawrapper.FormController
import timber.log.Timber

/**
 * Service to orchestrate the submission sync workflow
 * Coordinates extraction, upload, and status tracking
 */
class SubmissionSyncService private constructor(private val context: Context) {

    private val extractor = SubmissionExtractor(context)
    private val cosmosClient = CosmosDbClient.getInstance()
    private val pendingStore = PendingSubmissionStore.getInstance(context)
    private val syncScheduler = CosmosDbSyncScheduler.getInstance(context)

    companion object {
        @Volatile
        private var instance: SubmissionSyncService? = null

        @JvmStatic
        fun getInstance(context: Context): SubmissionSyncService {
            return instance ?: synchronized(this) {
                instance ?: SubmissionSyncService(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Sync a form submission to Cosmos DB
     *
     * @param formController The form controller containing submission data
     * @param location GPS location (if available)
     * @param callback Callback for sync status updates
     */
    suspend fun syncSubmission(
        formController: FormController,
        location: Location? = null,
        callback: SyncCallback? = null
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            // Check if sync is enabled
            if (!CosmosDbConfig.isSyncEnabled()) {
                Timber.d("Cosmos DB sync is disabled, skipping upload")
                return@withContext SyncResult.Skipped("Sync disabled in settings")
            }

            // Check WiFi requirement
            if (CosmosDbConfig.isWifiOnlyEnabled() && !isWifiConnected()) {
                Timber.d("WiFi-only mode enabled but not connected to WiFi")

                // Add to offline queue and schedule for later
                val submission = extractor.extractSubmission(formController, location)
                addToOfflineQueue(submission.submissionId, formController, submission.state)
                syncScheduler.scheduleImmediateSync(submission.submissionId)

                return@withContext SyncResult.Pending("Waiting for WiFi connection")
            }

            // Initialize clients if needed
            if (!cosmosClient.isInitialized()) {
                Timber.d("Initializing Cosmos DB client")
                cosmosClient.initialize(context)
            }

            // Notify extraction started
            callback?.onExtractionStarted()

            // Extract submission data
            Timber.d("Extracting submission from form: ${formController.getFormTitle()}")
            val submission = extractor.extractSubmission(formController, location)

            // Notify extraction completed
            callback?.onExtractionCompleted(submission.submissionId)

            // Check if submission already exists
            if (cosmosClient.submissionExists(submission.submissionId, submission.state)) {
                Timber.w("Submission already exists: ${submission.submissionId}")
                return@withContext SyncResult.Duplicate(submission.submissionId)
            }

            // Notify upload started
            callback?.onUploadStarted(submission.submissionId)

            // Upload to Cosmos DB
            Timber.d("Uploading submission: ${submission.submissionId}")
            val uploadResult = cosmosClient.uploadSubmission(submission)

            when (uploadResult) {
                is UploadResult.Success -> {
                    Timber.i("Submission synced successfully: ${uploadResult.submissionId}")
                    callback?.onUploadSuccess(uploadResult.submissionId)

                    // Remove from pending queue if exists
                    pendingStore.deleteSubmission(submission.submissionId)

                    SyncResult.Success(uploadResult.submissionId)
                }
                is UploadResult.Error -> {
                    Timber.e("Upload failed: ${uploadResult.errorCode} - ${uploadResult.message}")
                    callback?.onUploadError(uploadResult.errorCode, uploadResult.message)

                    // Determine if error is retryable
                    val retryable = isRetryableError(uploadResult.errorCode)

                    // Add to offline queue if retryable
                    if (retryable) {
                        addToOfflineQueue(submission.submissionId, formController, submission.state)

                        // Schedule background retry
                        syncScheduler.scheduleImmediateSync(submission.submissionId)
                    }

                    SyncResult.Failed(uploadResult.errorCode, uploadResult.message, retryable)
                }
            }

        } catch (e: Exception) {
            Timber.e(e, "Error during submission sync")
            callback?.onUploadError("SYNC_ERROR", e.message ?: "Unknown error")

            // Add to offline queue for retry
            val submission = try {
                extractor.extractSubmission(formController, location)
            } catch (extractError: Exception) {
                Timber.e(extractError, "Failed to extract submission for offline queue")
                return@withContext SyncResult.Failed("SYNC_ERROR", e.message ?: "Unknown error", retryable = true)
            }

            addToOfflineQueue(submission.submissionId, formController, submission.state)
            syncScheduler.scheduleImmediateSync(submission.submissionId)

            SyncResult.Failed("SYNC_ERROR", e.message ?: "Unknown error", retryable = true)
        }
    }

    /**
     * Check if WiFi is connected
     */
    private fun isWifiConnected(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as android.net.ConnectivityManager

            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)
        } catch (e: Exception) {
            Timber.w(e, "Error checking WiFi connection")
            false
        }
    }

    /**
     * Check if error code indicates a retryable error
     */
    private fun isRetryableError(errorCode: String): Boolean {
        return when (errorCode) {
            CosmosDbClient.ERROR_NETWORK -> true
            CosmosDbClient.ERROR_RATE_LIMIT -> true
            CosmosDbClient.ERROR_UNKNOWN -> true
            CosmosDbClient.ERROR_AUTH -> false
            CosmosDbClient.ERROR_CONFLICT -> false
            CosmosDbClient.ERROR_VALIDATION -> false
            else -> false
        }
    }

    /**
     * Test connection to Cosmos DB
     * Useful for verifying configuration before attempting sync
     */
    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!cosmosClient.isInitialized()) {
                cosmosClient.initialize(context)
            }
            cosmosClient.testConnection()
        } catch (e: Exception) {
            Timber.e(e, "Connection test failed")
            false
        }
    }

    /**
     * Get connection statistics
     */
    fun getConnectionStats(): Map<String, Any> {
        return cosmosClient.getConnectionStats()
    }

    /**
     * Initialize the sync service and all dependent clients
     * Should be called during app startup or before first sync
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Initializing SubmissionSyncService")

            // Initialize CosmosDbConfig
            CosmosDbConfig.initialize(context)

            // Initialize CosmosDbClient
            if (!cosmosClient.isInitialized()) {
                cosmosClient.initialize(context)
            }

            // Initialize SubmissionExtractor (GeofenceIdResolver)
            SubmissionExtractor.initializeResolver(context)

            // Test connection
            val connected = cosmosClient.testConnection()

            if (connected) {
                Timber.i("SubmissionSyncService initialized successfully")
            } else {
                Timber.w("SubmissionSyncService initialized but connection test failed")
            }

            connected
        } catch (e: Exception) {
            Timber.e(e, "Error initializing SubmissionSyncService")
            false
        }
    }

    /**
     * Check if service is ready to sync
     */
    fun isReady(): Boolean {
        return cosmosClient.isInitialized() &&
               SubmissionExtractor.isResolverInitialized()
    }

    /**
     * Add submission to offline queue for retry
     */
    private fun addToOfflineQueue(
        submissionId: String,
        formController: FormController,
        state: String
    ) {
        try {
            val instancePath = formController.getAbsoluteInstancePath() ?: ""
            val formId = formController.getFormDef()?.mainInstance?.name ?: "unknown"

            pendingStore.addPending(
                submissionId = submissionId,
                instancePath = instancePath,
                formId = formId,
                state = state
            )

            Timber.d("Added submission to offline queue: $submissionId")
        } catch (e: Exception) {
            Timber.e(e, "Error adding submission to offline queue: $submissionId")
        }
    }

    /**
     * Get pending submissions count
     */
    fun getPendingCount(): Int {
        return pendingStore.getPendingCount()
    }

    /**
     * Schedule periodic background sync
     */
    fun schedulePeriodicSync() {
        syncScheduler.schedulePeriodicSync()
    }

    /**
     * Cancel periodic background sync
     */
    fun cancelPeriodicSync() {
        syncScheduler.cancelPeriodicSync()
    }
}

/**
 * Result of submission sync operation
 */
sealed class SyncResult {
    data class Success(val submissionId: String) : SyncResult()
    data class Failed(val errorCode: String, val message: String, val retryable: Boolean) : SyncResult()
    data class Duplicate(val submissionId: String) : SyncResult()
    data class Pending(val reason: String) : SyncResult()
    data class Skipped(val reason: String) : SyncResult()

    fun isSuccess(): Boolean = this is Success
    fun isFailed(): Boolean = this is Failed
    fun isDuplicate(): Boolean = this is Duplicate
    fun isPending(): Boolean = this is Pending
    fun isSkipped(): Boolean = this is Skipped
}

/**
 * Callback interface for sync status updates
 * Allows UI to track progress and show feedback to user
 */
interface SyncCallback {
    /**
     * Called when extraction from FormController starts
     */
    fun onExtractionStarted()

    /**
     * Called when extraction completes successfully
     * @param submissionId The ID of the extracted submission
     */
    fun onExtractionCompleted(submissionId: String)

    /**
     * Called when upload to Cosmos DB starts
     * @param submissionId The ID of the submission being uploaded
     */
    fun onUploadStarted(submissionId: String)

    /**
     * Called when upload completes successfully
     * @param submissionId The ID of the uploaded submission
     */
    fun onUploadSuccess(submissionId: String)

    /**
     * Called when upload fails
     * @param errorCode Error code (e.g., NETWORK_ERROR, AUTH_ERROR)
     * @param message Error message
     */
    fun onUploadError(errorCode: String, message: String)
}
