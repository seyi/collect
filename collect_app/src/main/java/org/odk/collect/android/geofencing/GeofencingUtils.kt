package org.odk.collect.android.geofencing

import org.odk.collect.maps.MapPoint
import kotlin.math.*

/**
 * Utility functions for geofencing operations
 */
object GeofencingUtils {

    /**
     * Ray casting algorithm for point-in-polygon test
     *
     * Algorithm:
     * 1. Cast a horizontal ray from the point to infinity (to the right)
     * 2. Count how many times the ray crosses the polygon boundary
     * 3. If odd number of crossings, point is inside
     * 4. If even number of crossings, point is outside
     *
     * Time Complexity: O(n) where n is number of vertices
     * Space Complexity: O(1)
     *
     * @param point The point to test
     * @param polygon List of vertices defining the polygon
     * @return true if point is inside polygon, false otherwise
     */
    fun isPointInPolygon(point: MapPoint, polygon: List<MapPoint>): Boolean {
        if (polygon.size < 3) return false // Not a valid polygon

        var inside = false
        var j = polygon.size - 1

        for (i in polygon.indices) {
            val xi = polygon[i].longitude
            val yi = polygon[i].latitude
            val xj = polygon[j].longitude
            val yj = polygon[j].latitude

            // Check if ray crosses this edge
            val intersect = ((yi > point.latitude) != (yj > point.latitude)) &&
                (point.longitude < (xj - xi) * (point.latitude - yi) / (yj - yi) + xi)

            if (intersect) inside = !inside
            j = i
        }

        return inside
    }

    /**
     * Calculate shortest distance from point to polygon edge
     *
     * Uses Haversine formula for geographic coordinates
     *
     * @param point The point
     * @param polygon The polygon vertices
     * @return Distance in meters
     */
    fun distanceToPolygon(point: MapPoint, polygon: List<MapPoint>): Double {
        if (polygon.isEmpty()) return Double.MAX_VALUE

        var minDistance = Double.MAX_VALUE

        for (i in polygon.indices) {
            val j = (i + 1) % polygon.size
            val distance = distanceToLineSegment(point, polygon[i], polygon[j])
            minDistance = minOf(minDistance, distance)
        }

        return minDistance
    }

    /**
     * Calculate distance from point to line segment using Haversine formula
     *
     * @param point The point
     * @param lineStart Start of line segment
     * @param lineEnd End of line segment
     * @return Distance in meters
     */
    private fun distanceToLineSegment(
        point: MapPoint,
        lineStart: MapPoint,
        lineEnd: MapPoint
    ): Double {
        // If line segment is actually a point
        if (lineStart.latitude == lineEnd.latitude && lineStart.longitude == lineEnd.longitude) {
            return haversineDistance(point, lineStart)
        }

        // Calculate projection of point onto line
        val dx = lineEnd.longitude - lineStart.longitude
        val dy = lineEnd.latitude - lineStart.latitude

        val t = max(0.0, min(1.0,
            ((point.longitude - lineStart.longitude) * dx +
             (point.latitude - lineStart.latitude) * dy) /
            (dx * dx + dy * dy)
        ))

        val projectionLat = lineStart.latitude + t * dy
        val projectionLon = lineStart.longitude + t * dx

        return haversineDistance(
            point,
            MapPoint(projectionLat, projectionLon)
        )
    }

    /**
     * Calculate distance between two points using Haversine formula
     *
     * @param point1 First point
     * @param point2 Second point
     * @return Distance in meters
     */
    fun haversineDistance(point1: MapPoint, point2: MapPoint): Double {
        val R = 6371000.0 // Earth's radius in meters

        val lat1Rad = Math.toRadians(point1.latitude)
        val lat2Rad = Math.toRadians(point2.latitude)
        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLon = Math.toRadians(point2.longitude - point1.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    /**
     * Calculate the area of a polygon in square meters
     *
     * Uses spherical excess formula for geographic coordinates
     *
     * @param polygon List of vertices
     * @return Area in square meters
     */
    fun calculatePolygonArea(polygon: List<MapPoint>): Double {
        if (polygon.size < 3) return 0.0

        val R = 6371000.0 // Earth's radius in meters
        var area = 0.0

        for (i in polygon.indices) {
            val j = (i + 1) % polygon.size
            val p1 = polygon[i]
            val p2 = polygon[j]

            area += Math.toRadians(p2.longitude - p1.longitude) *
                    (2 + sin(Math.toRadians(p1.latitude)) +
                     sin(Math.toRadians(p2.latitude)))
        }

        area = abs(area * R * R / 2.0)
        return area
    }

    /**
     * Calculate the centroid (center point) of a polygon
     *
     * @param polygon List of vertices
     * @return Centroid point
     */
    fun calculateCentroid(polygon: List<MapPoint>): MapPoint {
        if (polygon.isEmpty()) {
            throw IllegalArgumentException("Cannot calculate centroid of empty polygon")
        }

        var sumLat = 0.0
        var sumLon = 0.0

        polygon.forEach { point ->
            sumLat += point.latitude
            sumLon += point.longitude
        }

        return MapPoint(
            sumLat / polygon.size,
            sumLon / polygon.size
        )
    }

    /**
     * Check if two polygons intersect
     *
     * Simple implementation: checks if any vertex of polygon1 is inside polygon2
     * or vice versa. Not comprehensive but good enough for most cases.
     *
     * @param polygon1 First polygon
     * @param polygon2 Second polygon
     * @return true if polygons intersect
     */
    fun polygonsIntersect(polygon1: List<MapPoint>, polygon2: List<MapPoint>): Boolean {
        // Check if any vertex of polygon1 is inside polygon2
        for (point in polygon1) {
            if (isPointInPolygon(point, polygon2)) {
                return true
            }
        }

        // Check if any vertex of polygon2 is inside polygon1
        for (point in polygon2) {
            if (isPointInPolygon(point, polygon1)) {
                return true
            }
        }

        return false
    }
}
