package org.odk.collect.android.cosmosdb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.odk.collect.android.cosmosdb.models.GeofenceReference
import timber.log.Timber

/**
 * Service to resolve geofence names to their IDs from hierarchical containers
 *
 * NOTE: This is a simplified version that works without the Azure Cosmos DB SDK.
 * Since we're using the Azure Function approach, we don't need to query Cosmos DB directly.
 * The geofence data is submitted with names, and the server-side can resolve IDs if needed.
 */
class GeofenceIdResolver private constructor() {

    private var initialized: Boolean = false

    companion object {
        @Volatile
        private var instance: GeofenceIdResolver? = null

        fun getInstance(): GeofenceIdResolver {
            return instance ?: synchronized(this) {
                instance ?: GeofenceIdResolver().also { instance = it }
            }
        }
    }

    /**
     * Initialize the resolver
     * This is a no-op since we don't need direct Cosmos DB access anymore
     */
    fun initialize(connectionString: String, databaseName: String) {
        try {
            initialized = true
            Timber.d("GeofenceIdResolver initialized (simplified mode - no SDK)")
        } catch (e: Exception) {
            Timber.e(e, "Error initializing GeofenceIdResolver")
            throw e
        }
    }

    /**
     * Check if resolver is initialized
     */
    fun isInitialized(): Boolean {
        return initialized
    }

    /**
     * Resolve geofence references from names
     *
     * NOTE: This simplified version returns GeofenceReference with names only.
     * IDs will be null since we're not querying Cosmos DB directly.
     * The Azure Function can handle name-based submissions.
     *
     * @param state State name (e.g., "Adamawa")
     * @param strategicCatchmentName Strategic catchment name (e.g., "Yedseram (East-Chad)")
     * @param microCatchmentName Micro catchment name (e.g., "Madagali")
     * @param lga LGA name (optional)
     * @return GeofenceReference with names (IDs will be null)
     */
    suspend fun resolveGeofenceIds(
        state: String,
        strategicCatchmentName: String?,
        microCatchmentName: String?,
        lga: String? = null
    ): GeofenceReference = withContext(Dispatchers.IO) {
        try {
            Timber.d("Resolving geofence: state=$state, strategic=$strategicCatchmentName, micro=$microCatchmentName")

            // Return GeofenceReference with names only
            // IDs are null since we're using the Azure Function approach
            GeofenceReference(
                state = state,
                lga = lga,
                strategicCatchmentId = null,  // Will be resolved server-side if needed
                strategicCatchmentName = strategicCatchmentName,
                microCatchmentId = null,  // Will be resolved server-side if needed
                microCatchmentName = microCatchmentName
            )
        } catch (e: Exception) {
            Timber.e(e, "Error resolving geofence IDs for state=$state, strategic=$strategicCatchmentName, micro=$microCatchmentName")

            // Return reference with names only (IDs will be null)
            GeofenceReference(
                state = state,
                lga = lga,
                strategicCatchmentId = null,
                strategicCatchmentName = strategicCatchmentName,
                microCatchmentId = null,
                microCatchmentName = microCatchmentName
            )
        }
    }

    /**
     * Test if resolver can connect
     * Always returns true in simplified mode
     */
    suspend fun testConnection(state: String = "Adamawa"): Boolean = withContext(Dispatchers.IO) {
        try {
            Timber.d("Connection test (simplified mode): always returns true")
            true
        } catch (e: Exception) {
            Timber.e(e, "Connection test failed")
            false
        }
    }

    /**
     * Close the resolver
     * This is a no-op in simplified mode
     */
    fun close() {
        try {
            initialized = false
            Timber.d("GeofenceIdResolver closed (simplified mode)")
        } catch (e: Exception) {
            Timber.e(e, "Error closing GeofenceIdResolver")
        }
    }
}
