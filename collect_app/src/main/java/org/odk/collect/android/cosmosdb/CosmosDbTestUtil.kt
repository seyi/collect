package org.odk.collect.android.cosmosdb

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Utility for testing Cosmos DB integration
 * Provides methods to verify connection, test upload, and check configuration
 */
class CosmosDbTestUtil(private val context: Context) {

    private val syncService = SubmissionSyncService.getInstance(context)
    private val cosmosClient = CosmosDbClient.getInstance()
    private val pendingStore = PendingSubmissionStore.getInstance(context)

    /**
     * Run comprehensive Cosmos DB integration test
     * Tests configuration, connection, and basic operations
     */
    suspend fun runIntegrationTest(): TestResult = withContext(Dispatchers.IO) {
        val results = mutableListOf<String>()
        var passed = 0
        var failed = 0

        try {
            // Test 1: Check configuration
            results.add("=== Test 1: Configuration ===")
            try {
                CosmosDbConfig.initialize(context)
                val apiEndpoint = CosmosDbConfig.getApiEndpoint()
                val syncEnabled = CosmosDbConfig.isSyncEnabled()

                results.add("✓ API Endpoint: $apiEndpoint")
                results.add("✓ Sync enabled: $syncEnabled")

                if (apiEndpoint.isEmpty()) {
                    results.add("✗ Configuration incomplete")
                    failed++
                } else {
                    results.add("✓ Configuration complete")
                    passed++
                }
            } catch (e: Exception) {
                results.add("✗ Configuration failed: ${e.message}")
                failed++
            }

            // Test 2: Initialize services
            results.add("\n=== Test 2: Service Initialization ===")
            try {
                val initialized = syncService.initialize()
                if (initialized) {
                    results.add("✓ SubmissionSyncService initialized")
                    passed++
                } else {
                    results.add("✗ SubmissionSyncService initialization failed")
                    failed++
                }
            } catch (e: Exception) {
                results.add("✗ Service initialization error: ${e.message}")
                failed++
            }

            // Test 3: Test connection
            results.add("\n=== Test 3: Connection Test ===")
            try {
                val connected = cosmosClient.testConnection()
                if (connected) {
                    results.add("✓ Successfully connected to Cosmos DB")
                    passed++
                } else {
                    results.add("✗ Connection test failed")
                    failed++
                }
            } catch (e: Exception) {
                results.add("✗ Connection error: ${e.message}")
                failed++
            }

            // Test 4: Check client initialization
            results.add("\n=== Test 4: Client Status ===")
            try {
                val clientReady = cosmosClient.isInitialized()
                val resolverReady = GeofenceIdResolver.getInstance().isInitialized()

                results.add("  CosmosDbClient: ${if (clientReady) "✓ Ready" else "✗ Not ready"}")
                results.add("  GeofenceIdResolver: ${if (resolverReady) "✓ Ready" else "✗ Not ready"}")

                if (clientReady && resolverReady) {
                    results.add("✓ All clients ready")
                    passed++
                } else {
                    results.add("✗ Some clients not ready")
                    failed++
                }
            } catch (e: Exception) {
                results.add("✗ Client status check error: ${e.message}")
                failed++
            }

            // Test 5: Pending queue
            results.add("\n=== Test 5: Offline Queue ===")
            try {
                val pendingCount = pendingStore.getPendingCount()
                results.add("✓ Pending submissions: $pendingCount")
                passed++
            } catch (e: Exception) {
                results.add("✗ Queue error: ${e.message}")
                failed++
            }

            // Test 6: GeofenceIdResolver test
            results.add("\n=== Test 6: GeofenceIdResolver ===")
            try {
                val resolver = GeofenceIdResolver.getInstance()
                val testResult = resolver.testConnection("Adamawa")
                if (testResult) {
                    results.add("✓ GeofenceIdResolver can access containers")
                    passed++
                } else {
                    results.add("✗ GeofenceIdResolver cannot access containers")
                    results.add("  (Containers may not exist: Adamawa_strategic_catchments, Adamawa_micro_catchments)")
                    failed++
                }
            } catch (e: Exception) {
                results.add("✗ GeofenceIdResolver error: ${e.message}")
                failed++
            }

            // Summary
            results.add("\n=== Summary ===")
            results.add("Tests passed: $passed")
            results.add("Tests failed: $failed")
            results.add("Overall: ${if (failed == 0) "✓ ALL TESTS PASSED" else "✗ SOME TESTS FAILED"}")

            TestResult(
                success = failed == 0,
                passed = passed,
                failed = failed,
                details = results.joinToString("\n")
            )

        } catch (e: Exception) {
            Timber.e(e, "Integration test error")
            TestResult(
                success = false,
                passed = passed,
                failed = failed + 1,
                details = results.joinToString("\n") + "\n\nFATAL ERROR: ${e.message}\n${e.stackTraceToString()}"
            )
        }
    }

    /**
     * Get connection statistics
     */
    fun getConnectionStats(): String {
        return try {
            val stats = cosmosClient.getConnectionStats()
            buildString {
                appendLine("=== Connection Statistics ===")
                stats.forEach { (key, value) ->
                    appendLine("$key: $value")
                }
            }
        } catch (e: Exception) {
            "Error getting stats: ${e.message}"
        }
    }

    /**
     * Get pending queue status
     */
    fun getPendingQueueStatus(): String {
        return try {
            val count = pendingStore.getPendingCount()
            val pending = pendingStore.getPendingSubmissions()

            buildString {
                appendLine("=== Pending Queue Status ===")
                appendLine("Total pending: $count")
                appendLine("\nPending submissions:")
                pending.forEach { submission ->
                    appendLine("  - ID: ${submission.submissionId}")
                    appendLine("    Form: ${submission.formId}")
                    appendLine("    State: ${submission.state}")
                    appendLine("    Status: ${submission.status}")
                    appendLine("    Retry count: ${submission.retryCount}")
                    if (submission.lastErrorMessage != null) {
                        appendLine("    Last error: ${submission.lastErrorMessage}")
                    }
                    appendLine()
                }
            }
        } catch (e: Exception) {
            "Error getting queue status: ${e.message}"
        }
    }

    /**
     * Clear pending queue (for testing)
     */
    fun clearPendingQueue(): String {
        return try {
            val pending = pendingStore.getPendingSubmissions()
            pending.forEach { submission ->
                pendingStore.deleteSubmission(submission.submissionId)
            }
            "Cleared ${pending.size} pending submissions"
        } catch (e: Exception) {
            "Error clearing queue: ${e.message}"
        }
    }

    data class TestResult(
        val success: Boolean,
        val passed: Int,
        val failed: Int,
        val details: String
    )
}
