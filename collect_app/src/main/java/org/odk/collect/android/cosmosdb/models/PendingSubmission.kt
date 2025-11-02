package org.odk.collect.android.cosmosdb.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a submission pending upload to Cosmos DB
 * Used for offline queue and retry logic
 */
data class PendingSubmission(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("submissionId")
    val submissionId: String,

    @SerializedName("instancePath")
    val instancePath: String,

    @SerializedName("formId")
    val formId: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("status")
    val status: SyncStatus,

    @SerializedName("retryCount")
    val retryCount: Int = 0,

    @SerializedName("lastErrorCode")
    val lastErrorCode: String? = null,

    @SerializedName("lastErrorMessage")
    val lastErrorMessage: String? = null,

    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @SerializedName("lastAttemptAt")
    val lastAttemptAt: Long? = null,

    @SerializedName("syncedAt")
    val syncedAt: Long? = null
) {
    companion object {
        const val MAX_RETRY_COUNT = 5
        const val RETRY_DELAY_MS = 60000L // 1 minute
    }

    /**
     * Check if submission should be retried
     */
    fun shouldRetry(): Boolean {
        return status == SyncStatus.FAILED && retryCount < MAX_RETRY_COUNT
    }

    /**
     * Get next retry delay based on exponential backoff
     */
    fun getNextRetryDelay(): Long {
        return RETRY_DELAY_MS * Math.pow(2.0, retryCount.toDouble()).toLong()
    }
}

/**
 * Status of submission sync
 */
enum class SyncStatus {
    PENDING,    // Waiting to be synced
    SYNCING,    // Currently being synced
    SYNCED,     // Successfully synced
    FAILED,     // Failed to sync (will retry)
    SKIPPED     // Skipped (sync disabled or other reason)
}
