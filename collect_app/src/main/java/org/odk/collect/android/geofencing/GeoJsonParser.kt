package org.odk.collect.android.geofencing

import org.json.JSONArray
import org.json.JSONObject
import org.odk.collect.maps.MapPoint
import timber.log.Timber

/**
 * Parser for GeoJSON files containing geofence polygons
 */
class GeoJsonParser {

    /**
     * Parse a GeoJSON string into a list of GeoFencePolygon objects
     *
     * @param jsonString The GeoJSON string to parse
     * @param type The type of geofence being parsed
     * @param state The source state
     * @return List of parsed polygons
     * @throws Exception if parsing fails
     */
    fun parseGeoJson(
        jsonString: String,
        type: GeofenceType,
        state: String
    ): List<GeoFencePolygon> {
        return try {
            val json = JSONObject(jsonString)
            parseFeatureCollection(json, type, state)
        } catch (e: Exception) {
            Timber.e(e, "Error parsing GeoJSON for state=$state, type=$type")
            emptyList()
        }
    }

    /**
     * Parse a FeatureCollection from JSONObject
     *
     * @param json The JSONObject containing the FeatureCollection
     * @param type The type of geofence
     * @param state The source state
     * @return List of parsed polygons
     */
    private fun parseFeatureCollection(
        json: JSONObject,
        type: GeofenceType,
        state: String
    ): List<GeoFencePolygon> {
        val polygons = mutableListOf<GeoFencePolygon>()

        if (json.getString("type") != "FeatureCollection") {
            Timber.w("JSON is not a FeatureCollection")
            return emptyList()
        }

        val features = json.getJSONArray("features")

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            try {
                val polygon = parseFeature(feature, type, state)
                if (polygon != null) {
                    polygons.add(polygon)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error parsing feature $i in $state")
                // Continue parsing other features
            }
        }

        Timber.d("Parsed ${polygons.size} polygons from $state ($type)")
        return polygons
    }

    /**
     * Parse a single Feature into a GeoFencePolygon
     *
     * @param feature The feature JSON object
     * @param type The type of geofence
     * @param state The source state
     * @return Parsed polygon or null if parsing fails
     */
    private fun parseFeature(
        feature: JSONObject,
        type: GeofenceType,
        state: String
    ): GeoFencePolygon? {
        // Extract properties
        val properties = feature.getJSONObject("properties")
        val propertiesMap = mutableMapOf<String, Any>()

        properties.keys().forEach { key ->
            val value = properties.get(key)
            if (value != null && value != JSONObject.NULL) {
                propertiesMap[key] = value
            }
        }

        // Get ID and name
        val id = extractId(propertiesMap, type, state)
        val name = extractName(propertiesMap, type)

        // Parse geometry
        val geometry = feature.getJSONObject("geometry")
        val vertices = parseGeometry(geometry)

        if (vertices.isEmpty()) {
            Timber.w("No vertices found for feature in $state")
            return null
        }

        // Calculate bounding box
        val boundingBox = BoundingBox.fromVertices(vertices)

        return GeoFencePolygon(
            id = id,
            name = name,
            type = type,
            state = state,
            vertices = vertices,
            properties = propertiesMap,
            boundingBox = boundingBox
        )
    }

    /**
     * Extract ID from properties
     *
     * Tries multiple field names: Id, ID, id, FID, fid, OBJECTID
     *
     * @param properties The properties map
     * @param type The geofence type
     * @param state The state
     * @return Unique ID string
     */
    private fun extractId(
        properties: Map<String, Any>,
        type: GeofenceType,
        state: String
    ): String {
        // Try common ID field names
        val idValue = properties["Id"]
            ?: properties["ID"]
            ?: properties["id"]
            ?: properties["FID"]
            ?: properties["fid"]
            ?: properties["OBJECTID"]
            ?: properties["objectid"]

        val idStr = idValue?.toString() ?: "unknown"

        // Create unique ID combining state, type, and feature ID
        val typePrefix = when (type) {
            GeofenceType.STATE -> "STATE"
            GeofenceType.LGA -> "LGA"
            GeofenceType.STRATEGIC_CATCHMENT -> "SC"
            GeofenceType.MICRO_CATCHMENT -> "MC"
            GeofenceType.INTERVENTION -> "INT"
        }

        return "$state-$typePrefix-$idStr"
    }

    /**
     * Extract name from properties
     *
     * Tries multiple field names: NAME, Name, name
     *
     * @param properties The properties map
     * @param type The geofence type
     * @return Name string
     */
    private fun extractName(
        properties: Map<String, Any>,
        type: GeofenceType
    ): String {
        val nameValue = properties["NAME"]
            ?: properties["Name"]
            ?: properties["name"]
            ?: properties["NUMB"]
            ?: properties["Id"]

        return nameValue?.toString() ?: "Unnamed ${type.name}"
    }

    /**
     * Parse geometry into a list of MapPoint vertices
     *
     * Handles both Polygon and MultiPolygon types
     *
     * @param geometry The geometry JSON object
     * @return List of vertices (for MultiPolygon, returns the largest polygon)
     */
    private fun parseGeometry(geometry: JSONObject): List<MapPoint> {
        val geometryType = geometry.getString("type")
        val coordinates = geometry.getJSONArray("coordinates")

        return when (geometryType) {
            "Polygon" -> parsePolygonCoordinates(coordinates)
            "MultiPolygon" -> parseMultiPolygonCoordinates(coordinates)
            else -> {
                Timber.w("Unsupported geometry type: $geometryType")
                emptyList()
            }
        }
    }

    /**
     * Parse Polygon coordinates
     *
     * Polygon coordinates are an array of linear rings
     * First ring is outer boundary, subsequent rings are holes (which we ignore)
     *
     * @param coordinates JSONArray of linear rings
     * @return List of vertices from outer ring
     */
    private fun parsePolygonCoordinates(coordinates: JSONArray): List<MapPoint> {
        if (coordinates.length() == 0) return emptyList()

        // Get outer ring (first element)
        val outerRing = coordinates.getJSONArray(0)
        return parseLinearRing(outerRing)
    }

    /**
     * Parse MultiPolygon coordinates
     *
     * MultiPolygon is an array of Polygons
     * We return the largest polygon (by vertex count)
     *
     * @param coordinates JSONArray of Polygons
     * @return List of vertices from largest polygon
     */
    private fun parseMultiPolygonCoordinates(coordinates: JSONArray): List<MapPoint> {
        var largestPolygon = emptyList<MapPoint>()

        for (i in 0 until coordinates.length()) {
            val polygon = coordinates.getJSONArray(i)
            val vertices = parsePolygonCoordinates(polygon)

            if (vertices.size > largestPolygon.size) {
                largestPolygon = vertices
            }
        }

        return largestPolygon
    }

    /**
     * Parse a linear ring (array of coordinate pairs) into MapPoints
     *
     * GeoJSON coordinates are [longitude, latitude] (not lat, lon!)
     *
     * @param ring JSONArray of [lon, lat] pairs
     * @return List of MapPoint objects
     */
    private fun parseLinearRing(ring: JSONArray): List<MapPoint> {
        val points = mutableListOf<MapPoint>()

        for (i in 0 until ring.length()) {
            val coordinate = ring.getJSONArray(i)

            if (coordinate.length() >= 2) {
                // GeoJSON format: [longitude, latitude]
                val longitude = coordinate.getDouble(0)
                val latitude = coordinate.getDouble(1)

                // MapPoint constructor: (latitude, longitude)
                points.add(MapPoint(latitude, longitude))
            }
        }

        return points
    }
}
