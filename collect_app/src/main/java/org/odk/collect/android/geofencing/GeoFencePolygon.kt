package org.odk.collect.android.geofencing

import org.odk.collect.maps.MapPoint

/**
 * Represents a geofence polygon with associated metadata
 *
 * @property id Unique identifier (e.g., "Kaduna-SC-6")
 * @property name Display name (e.g., "Hadejia")
 * @property type Type of geofence (STATE, LGA, STRATEGIC_CATCHMENT, etc.)
 * @property state Source state (e.g., "Kaduna")
 * @property vertices List of points defining the polygon boundary
 * @property properties Additional properties from GeoJSON
 * @property boundingBox Rectangular boundary for spatial optimization
 */
data class GeoFencePolygon(
    val id: String,
    val name: String,
    val type: GeofenceType,
    val state: String,
    val vertices: List<MapPoint>,
    val properties: Map<String, Any>,
    val boundingBox: BoundingBox
) {
    /**
     * Check if a point is inside this geofence
     *
     * First checks bounding box for quick rejection,
     * then runs point-in-polygon algorithm if needed
     *
     * @param point The point to test
     * @return true if point is inside this geofence
     */
    fun contains(point: MapPoint): Boolean {
        // Quick rejection using bounding box
        if (!boundingBox.contains(point)) {
            return false
        }

        // Run point-in-polygon algorithm
        return GeofencingUtils.isPointInPolygon(point, vertices)
    }

    /**
     * Get a property value by key
     *
     * @param key Property key
     * @return Property value or null if not found
     */
    fun getProperty(key: String): Any? {
        return properties[key]
    }

    /**
     * Get a property as String
     *
     * @param key Property key
     * @param default Default value if property not found or cannot be converted
     * @return Property value as String
     */
    fun getPropertyAsString(key: String, default: String = ""): String {
        return properties[key]?.toString() ?: default
    }

    /**
     * Get a property as Double
     *
     * @param key Property key
     * @param default Default value if property not found or cannot be converted
     * @return Property value as Double
     */
    fun getPropertyAsDouble(key: String, default: Double = 0.0): Double {
        return when (val value = properties[key]) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: default
            else -> default
        }
    }

    /**
     * Get a property as Int
     *
     * @param key Property key
     * @param default Default value if property not found or cannot be converted
     * @return Property value as Int
     */
    fun getPropertyAsInt(key: String, default: Int = 0): Int {
        return when (val value = properties[key]) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: default
            else -> default
        }
    }

    override fun toString(): String {
        return "GeoFencePolygon(id='$id', name='$name', type=$type, state='$state', vertices=${vertices.size} points)"
    }
}
