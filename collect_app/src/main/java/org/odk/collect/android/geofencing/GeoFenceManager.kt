package org.odk.collect.android.geofencing

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.odk.collect.maps.MapPoint
import timber.log.Timber
import java.io.IOException

/**
 * Singleton manager for loading and querying geofence polygons
 *
 * Loads GeoJSON files from assets and provides efficient spatial queries
 */
class GeoFenceManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: GeoFenceManager? = null

        /**
         * Get singleton instance
         *
         * @param context Application context
         * @return GeoFenceManager instance
         */
        fun getInstance(context: Context): GeoFenceManager {
            return instance ?: synchronized(this) {
                instance ?: GeoFenceManager(context.applicationContext).also { instance = it }
            }
        }

        // Asset directory structure
        private const val GEOFENCING_DIR = "geofencing"
        private const val STATE_BOUNDARY_SUFFIX = "_state_boundary.geojson"
        private const val LGA_BOUNDARIES_SUFFIX = "_lga_boundaries.geojson"
        private const val STRATEGIC_CATCHMENTS_SUFFIX = "_strategic_catchments.geojson"
        private const val MICRO_CATCHMENTS_SUFFIX = "_micro_catchments.geojson"
        private const val INTERVENTIONS_SUFFIX = "_interventions.geojson"

        // All Nigerian states in ACReSAL project
        private val ALL_STATES = listOf(
            "Adamawa", "Bauchi", "Benue", "Borno", "Fct", "Gombe",
            "Jigawa", "Kaduna", "Kano", "Katsina", "Kebbi", "Kogi",
            "Kwara", "Nasarawa", "Niger", "Plateau", "Sokoto",
            "Taraba", "Yobe", "Zamfara"
        )
    }

    private val parser = GeoJsonParser()
    private val polygonCache = mutableMapOf<String, List<GeoFencePolygon>>()
    private val loadedStates = mutableSetOf<String>()

    /**
     * Load geofences from assets
     *
     * @param state State name to load, or null to load all states
     * @param types Types of geofences to load (default: all types except INTERVENTION)
     * @return true if successful, false if any errors occurred
     */
    suspend fun loadGeofences(
        state: String? = null,
        types: List<GeofenceType> = listOf(
            GeofenceType.STATE,
            GeofenceType.LGA,
            GeofenceType.STRATEGIC_CATCHMENT,
            GeofenceType.MICRO_CATCHMENT
        )
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val statesToLoad = if (state != null) {
                listOf(state)
            } else {
                ALL_STATES
            }

            var hasErrors = false

            statesToLoad.forEach { stateName ->
                Timber.d("Loading geofences for $stateName...")

                types.forEach { type ->
                    try {
                        val polygons = loadGeofencesForStateAndType(stateName, type)
                        if (polygons.isNotEmpty()) {
                            val cacheKey = getCacheKey(stateName, type)
                            polygonCache[cacheKey] = polygons
                            Timber.d("Loaded ${polygons.size} $type polygons for $stateName")

                            // Debug: Log micro catchment names
                            if (type == GeofenceType.MICRO_CATCHMENT) {
                                polygons.forEach { poly ->
                                    Timber.d("  Micro catchment: ${poly.name} (${poly.id})")
                                }
                            }
                        } else {
                            // STATE, Strategic Catchment, and Micro Catchment are REQUIRED
                            // LGA is OPTIONAL
                            if (type == GeofenceType.LGA) {
                                Timber.d("Optional LGA polygons not found for $stateName (this is OK)")
                            } else {
                                Timber.w("Critical: $type not found for $stateName")
                                hasErrors = true
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error loading $type for $stateName")
                        // Only LGA is optional, all others are required
                        if (type != GeofenceType.LGA) {
                            hasErrors = true
                        }
                    }
                }

                loadedStates.add(stateName)
            }

            Timber.i("Geofence loading complete. Loaded ${loadedStates.size} states, ${polygonCache.values.sumOf { it.size }} total polygons")
            !hasErrors
        } catch (e: Exception) {
            Timber.e(e, "Error loading geofences")
            false
        }
    }

    /**
     * Load geofences for a specific state and type
     *
     * @param state State name
     * @param type Geofence type
     * @return List of parsed polygons
     */
    private fun loadGeofencesForStateAndType(
        state: String,
        type: GeofenceType
    ): List<GeoFencePolygon> {
        val fileName = when (type) {
            GeofenceType.STATE -> "$state$STATE_BOUNDARY_SUFFIX"
            GeofenceType.LGA -> "$state$LGA_BOUNDARIES_SUFFIX"
            GeofenceType.STRATEGIC_CATCHMENT -> "$state$STRATEGIC_CATCHMENTS_SUFFIX"
            GeofenceType.MICRO_CATCHMENT -> "$state$MICRO_CATCHMENTS_SUFFIX"
            GeofenceType.INTERVENTION -> "$state$INTERVENTIONS_SUFFIX"
        }

        val assetPath = "$GEOFENCING_DIR/$state/$fileName"

        return try {
            val jsonString = context.assets.open(assetPath).bufferedReader().use { it.readText() }
            parser.parseGeoJson(jsonString, type, state)
        } catch (e: IOException) {
            Timber.e(e, "File not found: $assetPath")
            emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Error loading $assetPath")
            emptyList()
        }
    }

    /**
     * Find all polygons containing a point
     *
     * @param point GPS coordinate
     * @param types Filter by geofence types (null = all types)
     * @return List of containing polygons, ordered by area (smallest first, most specific)
     */
    fun getContainingPolygons(
        point: MapPoint,
        types: List<GeofenceType>? = null
    ): List<GeoFencePolygon> {
        val results = mutableListOf<GeoFencePolygon>()

        // Debug: Log cache state
        Timber.d("Cache contains ${polygonCache.size} keys, checking point (${point.latitude}, ${point.longitude})")

        polygonCache.values.forEach { polygons ->
            polygons.forEach { polygon ->
                // Filter by type if specified
                if (types == null || polygon.type in types) {
                    if (polygon.contains(point)) {
                        results.add(polygon)
                        Timber.d("Point is inside ${polygon.type}: ${polygon.name}")
                    } else {
                        // Debug: Log why micro catchments aren't matching
                        if (polygon.type == GeofenceType.MICRO_CATCHMENT) {
                            Timber.d("Point is OUTSIDE ${polygon.type}: ${polygon.name} (${polygon.state})")
                        }
                    }
                }
            }
        }

        // Sort by bounding box area (smallest first = most specific)
        return results.sortedBy { it.boundingBox.area() }
    }

    /**
     * Find polygon by unique ID
     *
     * @param id Polygon ID (e.g., "Kaduna-SC-6")
     * @return Polygon if found, null otherwise
     */
    fun getPolygonById(id: String): GeoFencePolygon? {
        polygonCache.values.forEach { polygons ->
            polygons.forEach { polygon ->
                if (polygon.id == id) {
                    return polygon
                }
            }
        }
        return null
    }

    /**
     * Get all polygons for a state and type
     *
     * @param state State name
     * @param type Geofence type
     * @return List of polygons
     */
    fun getPolygonsForState(
        state: String,
        type: GeofenceType
    ): List<GeoFencePolygon> {
        val cacheKey = getCacheKey(state, type)
        return polygonCache[cacheKey] ?: emptyList()
    }

    /**
     * Get all polygons of a specific type across all loaded states
     *
     * @param type Geofence type
     * @return List of polygons
     */
    fun getAllPolygonsOfType(type: GeofenceType): List<GeoFencePolygon> {
        return polygonCache.values.flatten().filter { it.type == type }
    }

    /**
     * Check if point is in specific polygon
     *
     * @param point GPS coordinate
     * @param polygonId Polygon ID
     * @return true if point is inside polygon
     */
    fun isPointInPolygon(point: MapPoint, polygonId: String): Boolean {
        val polygon = getPolygonById(polygonId)
        return polygon?.contains(point) ?: false
    }

    /**
     * Get distance from point to nearest polygon boundary
     *
     * @param point GPS coordinate
     * @param polygonId Polygon ID
     * @return Distance in meters, or null if polygon not found
     */
    fun getDistanceToPolygon(point: MapPoint, polygonId: String): Double? {
        val polygon = getPolygonById(polygonId) ?: return null
        return GeofencingUtils.distanceToPolygon(point, polygon.vertices)
    }

    /**
     * Check if any geofences are loaded
     *
     * @return true if at least one state is loaded
     */
    fun isLoaded(): Boolean {
        return loadedStates.isNotEmpty()
    }

    /**
     * Check if a specific state is loaded
     *
     * @param state State name
     * @return true if state is loaded
     */
    fun isStateLoaded(state: String): Boolean {
        return state in loadedStates
    }

    /**
     * Get list of loaded states
     *
     * @return List of state names
     */
    fun getLoadedStates(): List<String> {
        return loadedStates.toList()
    }

    /**
     * Get cache statistics
     *
     * @return Statistics about loaded data
     */
    fun getCacheStats(): CacheStats {
        val totalPolygons = polygonCache.values.sumOf { it.size }
        val totalVertices = polygonCache.values.flatten().sumOf { it.vertices.size }

        // Rough estimate: each MapPoint = ~40 bytes (2 doubles + overhead)
        // Each polygon = ~200 bytes overhead + vertices
        val estimatedMemoryBytes = totalPolygons * 200L + totalVertices * 40L
        val estimatedMemoryMB = estimatedMemoryBytes / (1024.0 * 1024.0)

        return CacheStats(
            loadedStates = loadedStates.toList(),
            totalPolygons = totalPolygons,
            totalVertices = totalVertices,
            memoryUsageMB = estimatedMemoryMB,
            cacheEntries = polygonCache.size
        )
    }

    /**
     * Clear all cached data
     *
     * Useful for testing or forcing a reload
     */
    fun clearCache() {
        polygonCache.clear()
        loadedStates.clear()
        Timber.d("Geofence cache cleared")
    }

    /**
     * Clear cache for a specific state
     *
     * @param state State name
     */
    fun clearStateCache(state: String) {
        val keysToRemove = polygonCache.keys.filter { it.startsWith("$state-") }
        keysToRemove.forEach { polygonCache.remove(it) }
        loadedStates.remove(state)
        Timber.d("Cleared cache for $state")
    }

    /**
     * Generate cache key for state and type
     */
    private fun getCacheKey(state: String, type: GeofenceType): String {
        return "$state-${type.name}"
    }

    /**
     * Search polygons by name
     *
     * @param query Search query (case-insensitive)
     * @param types Filter by types (null = all types)
     * @return List of matching polygons
     */
    fun searchByName(query: String, types: List<GeofenceType>? = null): List<GeoFencePolygon> {
        val lowerQuery = query.lowercase()
        return polygonCache.values.flatten()
            .filter { polygon ->
                (types == null || polygon.type in types) &&
                polygon.name.lowercase().contains(lowerQuery)
            }
    }

    /**
     * Get all unique polygon names for a type
     *
     * @param type Geofence type
     * @return List of unique names
     */
    fun getUniqueNames(type: GeofenceType): List<String> {
        return getAllPolygonsOfType(type)
            .map { it.name }
            .distinct()
            .sorted()
    }
}

/**
 * Statistics about cached geofence data
 */
data class CacheStats(
    val loadedStates: List<String>,
    val totalPolygons: Int,
    val totalVertices: Int,
    val memoryUsageMB: Double,
    val cacheEntries: Int
)
