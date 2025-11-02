package org.odk.collect.android.cosmosdb

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.odk.collect.android.cosmosdb.models.ODKSubmission
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Singleton client for Azure Cosmos DB operations via HTTP API
 * Handles connection, submission upload, and error handling with retry logic
 *
 * This implementation uses an Azure Function as an API layer instead of the Cosmos DB SDK
 * to avoid dependency conflicts and simplify mobile app integration.
 */
class CosmosDbClient private constructor() {

    private var apiEndpoint: String? = null
    private var apiKey: String? = null
    private val gson = Gson()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        @Volatile
        private var instance: CosmosDbClient? = null

        fun getInstance(): CosmosDbClient {
            return instance ?: synchronized(this) {
                instance ?: CosmosDbClient().also { instance = it }
            }
        }

        // Error codes
        const val ERROR_NETWORK = "NETWORK_ERROR"
        const val ERROR_AUTH = "AUTH_ERROR"
        const val ERROR_RATE_LIMIT = "RATE_LIMIT_ERROR"
        const val ERROR_CONFLICT = "CONFLICT_ERROR"
        const val ERROR_VALIDATION = "VALIDATION_ERROR"
        const val ERROR_UNKNOWN = "UNKNOWN_ERROR"

        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    /**
     * Initialize the Cosmos DB client
     * Must be called before any operations
     */
    fun initialize(context: Context) {
        try {
            if (apiEndpoint != null) {
                Timber.d("CosmosDbClient already initialized")
                return
            }

            // Initialize configuration
            CosmosDbConfig.initialize(context)

            apiEndpoint = CosmosDbConfig.getApiEndpoint()
            apiKey = CosmosDbConfig.getApiFunctionKey()

            if (apiEndpoint.isNullOrEmpty()) {
                throw IllegalStateException("Cosmos DB API endpoint is not configured")
            }

            Timber.i("CosmosDbClient initialized successfully")
            Timber.d("API Endpoint: $apiEndpoint")

        } catch (e: Exception) {
            Timber.e(e, "Error initializing CosmosDbClient")
            throw e
        }
    }

    /**
     * Check if client is initialized
     */
    fun isInitialized(): Boolean {
        return !apiEndpoint.isNullOrEmpty()
    }

    /**
     * Upload ODK submission to Cosmos DB via Azure Function API
     * Uses retry logic with exponential backoff
     *
     * @param submission The ODK submission to upload
     * @return Result indicating success or failure
     */
    suspend fun uploadSubmission(submission: ODKSubmission): UploadResult = withContext(Dispatchers.IO) {
        if (!isInitialized()) {
            return@withContext UploadResult.Error(
                ERROR_UNKNOWN,
                "CosmosDbClient not initialized"
            )
        }

        try {
            Timber.d("Uploading submission: ${submission.id}")

            // Execute with retry logic
            executeWithRetry {
                uploadSubmissionInternal(submission)
            }

            Timber.i("Submission uploaded successfully: ${submission.id}")
            UploadResult.Success(submission.id)

        } catch (e: Exception) {
            val errorResult = handleUploadError(e, submission.id)
            Timber.e(e, "Failed to upload submission: ${submission.id}, error: ${errorResult.errorCode}")
            errorResult
        }
    }

    /**
     * Internal method to upload submission (without retry logic)
     */
    private suspend fun uploadSubmissionInternal(submission: ODKSubmission) {
        // Convert submission to JSON
        val jsonBody = gson.toJson(submission)

        // Build URL with function key if provided
        val url = if (!apiKey.isNullOrEmpty()) {
            "$apiEndpoint?code=$apiKey"
        } else {
            apiEndpoint!!
        }

        // Create HTTP request
        val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        // Execute request
        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "No error message"
            throw HttpException(response.code, errorBody)
        }

        response.close()
    }

    /**
     * Execute operation with retry logic and exponential backoff
     */
    private suspend fun <T> executeWithRetry(
        maxRetries: Int = CosmosDbConfig.getMaxRetries(),
        baseDelay: Long = CosmosDbConfig.getRetryDelayMs(),
        operation: suspend () -> T
    ): T {
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                lastException = e

                // Check if error is retryable
                if (!isRetryableError(e)) {
                    throw e
                }

                // Don't delay on last attempt
                if (attempt < maxRetries - 1) {
                    val delay = calculateBackoff(attempt, baseDelay)
                    Timber.d("Retry attempt ${attempt + 1}/$maxRetries after ${delay}ms")
                    kotlinx.coroutines.delay(delay)
                }
            }
        }

        throw lastException ?: IOException("Operation failed after $maxRetries retries")
    }

    /**
     * Check if error is retryable (network, rate limit, transient errors)
     */
    private fun isRetryableError(error: Exception): Boolean {
        val message = error.message?.lowercase() ?: ""

        return when {
            error is HttpException && error.code == 429 -> true  // Too Many Requests (rate limit)
            error is HttpException && error.code == 503 -> true  // Service Unavailable
            error is HttpException && error.code in 500..599 -> true  // Server errors
            error is IOException -> true  // Network errors
            message.contains("timeout") -> true
            message.contains("connection") -> true
            message.contains("network") -> true
            message.contains("econnreset") -> true
            message.contains("etimedout") -> true
            else -> false
        }
    }

    /**
     * Calculate exponential backoff delay
     */
    private fun calculateBackoff(attempt: Int, baseDelay: Long): Long {
        return baseDelay * Math.pow(2.0, attempt.toDouble()).toLong()
    }

    /**
     * Handle upload errors and return appropriate error result
     */
    private fun handleUploadError(error: Exception, submissionId: String): UploadResult.Error {
        val message = error.message ?: "Unknown error"

        return when {
            error is HttpException -> {
                when (error.code) {
                    401, 403 -> UploadResult.Error(ERROR_AUTH, "Authentication failed: ${error.body}")
                    409 -> UploadResult.Error(ERROR_CONFLICT, "Submission already exists: $submissionId")
                    429 -> UploadResult.Error(ERROR_RATE_LIMIT, "Rate limit exceeded, please retry")
                    400 -> UploadResult.Error(ERROR_VALIDATION, "Invalid submission data: ${error.body}")
                    else -> UploadResult.Error(ERROR_UNKNOWN, "API error (${error.code}): ${error.body}")
                }
            }
            error is IOException || message.contains("network", ignoreCase = true) ||
            message.contains("connection", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) -> {
                UploadResult.Error(ERROR_NETWORK, "Network error: $message")
            }
            else -> {
                UploadResult.Error(ERROR_UNKNOWN, "Upload failed: $message")
            }
        }
    }

    /**
     * Test connection to the API endpoint
     * Verifies that the Azure Function is accessible
     */
    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized()) {
                Timber.w("Cannot test connection: client not initialized")
                return@withContext false
            }

            // Try a simple GET request to check if endpoint is accessible
            val request = Request.Builder()
                .url(apiEndpoint!!)
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val success = response.code in 200..405  // Even 405 (Method Not Allowed) means endpoint exists
            response.close()

            Timber.d("Connection test: ${if (success) "SUCCESS" else "FAILED"} (HTTP ${response.code})")
            success

        } catch (e: Exception) {
            Timber.e(e, "Connection test failed")
            false
        }
    }

    /**
     * Check if a submission already exists
     * Note: This requires implementing a GET endpoint in your Azure Function
     */
    suspend fun submissionExists(submissionId: String, state: String): Boolean {
        // For now, we'll rely on the 409 Conflict response from the upload attempt
        // If you want to check before uploading, implement a GET endpoint in your Azure Function
        return false
    }

    /**
     * Get connection statistics
     */
    fun getConnectionStats(): Map<String, Any> {
        return mapOf(
            "initialized" to isInitialized(),
            "apiEndpoint" to (apiEndpoint ?: "Not configured"),
            "hasApiKey" to (!apiKey.isNullOrEmpty()),
            "syncEnabled" to CosmosDbConfig.isSyncEnabled(),
            "autoSync" to CosmosDbConfig.isAutoSyncEnabled(),
            "wifiOnly" to CosmosDbConfig.isWifiOnlyEnabled()
        )
    }

    /**
     * Close the HTTP client
     * Should be called when app is closing or during cleanup
     */
    fun close() {
        try {
            // OkHttp clients are designed to be shared and don't typically need closing
            // But we can clear references
            apiEndpoint = null
            apiKey = null
            Timber.d("CosmosDbClient closed")
        } catch (e: Exception) {
            Timber.e(e, "Error closing CosmosDbClient")
        }
    }
}

/**
 * Result of upload operation
 */
sealed class UploadResult {
    data class Success(val submissionId: String) : UploadResult()
    data class Error(val errorCode: String, val message: String) : UploadResult()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
}

/**
 * Custom exception for HTTP errors
 */
class HttpException(val code: Int, val body: String) : Exception("HTTP $code: $body")
