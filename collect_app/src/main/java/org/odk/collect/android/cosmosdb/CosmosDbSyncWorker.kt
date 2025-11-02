package org.odk.collect.android.cosmosdb

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.odk.collect.android.javarosawrapper.FormController
import org.odk.collect.android.cosmosdb.models.SyncStatus
import timber.log.Timber
import java.io.File

/**
 * WorkManager worker for background Cosmos DB sync
 * Processes pending submissions in the offline queue
 */
class CosmosDbSyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "cosmos_db_sync"
        const val INPUT_SUBMISSION_ID = "submission_id"
        const val OUTPUT_SUCCESS_COUNT = "success_count"
        const val OUTPUT_FAILED_COUNT = "failed_count"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Timber.d("Starting Cosmos DB background sync")

            // Check if sync is enabled
            if (!CosmosDbConfig.isSyncEnabled()) {
                Timber.d("Cosmos DB sync is disabled, skipping")
                return@withContext Result.success()
            }

            // Check WiFi requirement
            if (CosmosDbConfig.isWifiOnlyEnabled() && !isWifiConnected()) {
                Timber.d("WiFi-only mode enabled but not connected, retrying later")
                return@withContext Result.retry()
            }

            // Get pending submissions
            val pendingStore = PendingSubmissionStore.getInstance(context)
            val syncService = SubmissionSyncService.getInstance(context)

            // Initialize sync service if needed
            if (!syncService.isReady()) {
                val initialized = syncService.initialize()
                if (!initialized) {
                    Timber.e("Failed to initialize sync service")
                    return@withContext Result.retry()
                }
            }

            // Process specific submission if provided
            val specificSubmissionId = inputData.getString(INPUT_SUBMISSION_ID)
            val pendingSubmissions = if (specificSubmissionId != null) {
                val submission = pendingStore.getSubmission(specificSubmissionId)
                if (submission != null) listOf(submission) else emptyList()
            } else {
                pendingStore.getPendingSubmissions()
            }

            if (pendingSubmissions.isEmpty()) {
                Timber.d("No pending submissions to sync")
                return@withContext Result.success()
            }

            Timber.d("Processing ${pendingSubmissions.size} pending submissions")

            var successCount = 0
            var failedCount = 0

            // Process each pending submission
            for (pending in pendingSubmissions) {
                try {
                    // Mark as syncing
                    pendingStore.markAsSyncing(pending.submissionId)

                    // Load form controller from instance path
                    val formController = loadFormController(pending.instancePath)

                    if (formController == null) {
                        Timber.e("Failed to load form controller for: ${pending.instancePath}")
                        pendingStore.markAsFailed(
                            pending.submissionId,
                            "LOAD_ERROR",
                            "Failed to load form instance"
                        )
                        failedCount++
                        continue
                    }

                    // Sync the submission
                    val syncResult = syncService.syncSubmission(formController, null, null)

                    when {
                        syncResult.isSuccess() -> {
                            pendingStore.markAsSynced(pending.submissionId)
                            successCount++
                            Timber.i("Successfully synced: ${pending.submissionId}")
                        }
                        syncResult.isFailed() -> {
                            val failed = syncResult as SyncResult.Failed
                            pendingStore.markAsFailed(
                                pending.submissionId,
                                failed.errorCode,
                                failed.message
                            )
                            failedCount++
                            Timber.w("Failed to sync: ${pending.submissionId} - ${failed.errorCode}")
                        }
                        syncResult.isPending() -> {
                            // Keep as pending, will retry later
                            Timber.d("Sync pending: ${pending.submissionId}")
                        }
                        syncResult.isDuplicate() -> {
                            // Already synced, mark as synced
                            pendingStore.markAsSynced(pending.submissionId)
                            successCount++
                            Timber.d("Submission already synced: ${pending.submissionId}")
                        }
                        syncResult.isSkipped() -> {
                            // Skip this submission
                            Timber.d("Sync skipped: ${pending.submissionId}")
                        }
                    }

                } catch (e: Exception) {
                    Timber.e(e, "Error syncing submission: ${pending.submissionId}")
                    pendingStore.markAsFailed(
                        pending.submissionId,
                        "SYNC_ERROR",
                        e.message ?: "Unknown error"
                    )
                    failedCount++
                }
            }

            // Clean up old synced submissions
            pendingStore.clearOldSynced()

            Timber.i("Sync complete: $successCount succeeded, $failedCount failed")

            // Return results
            val outputData = Data.Builder()
                .putInt(OUTPUT_SUCCESS_COUNT, successCount)
                .putInt(OUTPUT_FAILED_COUNT, failedCount)
                .build()

            // If all failed, retry
            if (successCount == 0 && failedCount > 0) {
                Result.retry()
            } else {
                Result.success(outputData)
            }

        } catch (e: Exception) {
            Timber.e(e, "Error during background sync")
            Result.retry()
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
     * Load FormController from instance path
     * Note: This is a simplified version - in production you'd need to properly
     * load the form using ODK Collect's form session management
     */
    private fun loadFormController(instancePath: String): FormController? {
        return try {
            // TODO: Implement proper FormController loading
            // For now, return null as we need to implement this properly
            // This would require accessing the form session and instance management
            Timber.w("FormController loading not yet implemented for offline sync")
            null
        } catch (e: Exception) {
            Timber.e(e, "Error loading FormController from: $instancePath")
            null
        }
    }
}
