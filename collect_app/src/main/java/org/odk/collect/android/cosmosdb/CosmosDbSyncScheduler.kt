package org.odk.collect.android.cosmosdb

import android.content.Context
import androidx.work.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Scheduler for Cosmos DB background sync using WorkManager
 * Handles periodic sync and one-time retry scheduling
 */
class CosmosDbSyncScheduler private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: CosmosDbSyncScheduler? = null

        // Work tags
        private const val WORK_TAG_PERIODIC = "cosmos_db_periodic_sync"
        private const val WORK_TAG_ONE_TIME = "cosmos_db_one_time_sync"

        // Sync intervals
        private const val PERIODIC_SYNC_INTERVAL_MINUTES = 60L // 1 hour
        private const val RETRY_INITIAL_BACKOFF_MINUTES = 15L

        @JvmStatic
        fun getInstance(context: Context): CosmosDbSyncScheduler {
            return instance ?: synchronized(this) {
                instance ?: CosmosDbSyncScheduler(context.applicationContext).also { instance = it }
            }
        }
    }

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule periodic background sync
     * Runs every hour to sync pending submissions
     */
    fun schedulePeriodicSync() {
        if (!CosmosDbConfig.isAutoSyncEnabled()) {
            Timber.d("Auto-sync is disabled, not scheduling periodic sync")
            cancelPeriodicSync()
            return
        }

        // Build constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (CosmosDbConfig.isWifiOnlyEnabled()) {
                    NetworkType.UNMETERED // WiFi or unlimited data
                } else {
                    NetworkType.CONNECTED // Any network
                }
            )
            .setRequiresBatteryNotLow(true) // Don't drain battery
            .build()

        // Build periodic work request
        val periodicWork = PeriodicWorkRequestBuilder<CosmosDbSyncWorker>(
            PERIODIC_SYNC_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                RETRY_INITIAL_BACKOFF_MINUTES,
                TimeUnit.MINUTES
            )
            .addTag(WORK_TAG_PERIODIC)
            .build()

        // Enqueue unique periodic work (replaces existing)
        workManager.enqueueUniquePeriodicWork(
            CosmosDbSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing if already scheduled
            periodicWork
        )

        Timber.d("Scheduled periodic Cosmos DB sync (interval: ${PERIODIC_SYNC_INTERVAL_MINUTES}m)")
    }

    /**
     * Cancel periodic background sync
     */
    fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(CosmosDbSyncWorker.WORK_NAME)
        Timber.d("Cancelled periodic Cosmos DB sync")
    }

    /**
     * Schedule one-time sync immediately
     * Used when a form is finalized and immediate sync fails
     */
    fun scheduleImmediateSync(submissionId: String? = null) {
        // Build constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (CosmosDbConfig.isWifiOnlyEnabled()) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                }
            )
            .build()

        // Build input data if specific submission
        val inputData = if (submissionId != null) {
            Data.Builder()
                .putString(CosmosDbSyncWorker.INPUT_SUBMISSION_ID, submissionId)
                .build()
        } else {
            Data.EMPTY
        }

        // Build one-time work request
        val oneTimeWork = OneTimeWorkRequestBuilder<CosmosDbSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                RETRY_INITIAL_BACKOFF_MINUTES,
                TimeUnit.MINUTES
            )
            .addTag(WORK_TAG_ONE_TIME)
            .build()

        // Enqueue work
        workManager.enqueue(oneTimeWork)

        Timber.d("Scheduled immediate sync" + if (submissionId != null) " for: $submissionId" else "")
    }

    /**
     * Schedule delayed retry for a specific submission
     * Used when a submission fails and should be retried later
     */
    fun scheduleRetry(submissionId: String, delayMinutes: Long) {
        // Build constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (CosmosDbConfig.isWifiOnlyEnabled()) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                }
            )
            .build()

        // Build input data
        val inputData = Data.Builder()
            .putString(CosmosDbSyncWorker.INPUT_SUBMISSION_ID, submissionId)
            .build()

        // Build delayed work request
        val delayedWork = OneTimeWorkRequestBuilder<CosmosDbSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                RETRY_INITIAL_BACKOFF_MINUTES,
                TimeUnit.MINUTES
            )
            .addTag(WORK_TAG_ONE_TIME)
            .addTag("retry_$submissionId")
            .build()

        // Enqueue work
        workManager.enqueue(delayedWork)

        Timber.d("Scheduled retry for $submissionId in ${delayMinutes}m")
    }

    /**
     * Cancel all pending sync work
     */
    fun cancelAllSync() {
        workManager.cancelAllWorkByTag(WORK_TAG_PERIODIC)
        workManager.cancelAllWorkByTag(WORK_TAG_ONE_TIME)
        Timber.d("Cancelled all Cosmos DB sync work")
    }

    /**
     * Get pending work info for monitoring
     */
    fun getPendingWorkInfo(): List<WorkInfo> {
        return try {
            workManager.getWorkInfosByTag(WORK_TAG_PERIODIC).get() +
                    workManager.getWorkInfosByTag(WORK_TAG_ONE_TIME).get()
        } catch (e: Exception) {
            Timber.e(e, "Error getting pending work info")
            emptyList()
        }
    }

    /**
     * Check if periodic sync is scheduled
     */
    fun isPeriodicSyncScheduled(): Boolean {
        return try {
            val workInfos = workManager.getWorkInfosForUniqueWork(CosmosDbSyncWorker.WORK_NAME).get()
            workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        } catch (e: Exception) {
            Timber.e(e, "Error checking periodic sync status")
            false
        }
    }
}
