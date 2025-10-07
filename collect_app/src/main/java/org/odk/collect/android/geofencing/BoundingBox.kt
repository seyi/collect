package org.odk.collect.android.geofencing

import org.odk.collect.maps.MapPoint

/**
 * Rectangular bounding box for quick spatial filtering
 * Used to optimize point-in-polygon queries by quickly rejecting points
 * that are obviously outside the polygon
 */
data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
) {
    /**
     * Check if a point is within this bounding box
     * This is a fast check that can eliminate polygons before
     * running the more expensive point-in-polygon algorithm
     *
     * @param point The point to test
     * @return true if point is within bounding box
     */
    fun contains(point: MapPoint): Boolean {
        return point.latitude >= minLat &&
               point.latitude <= maxLat &&
               point.longitude >= minLon &&
               point.longitude <= maxLon
    }

    /**
     * Calculate the area of this bounding box (in square degrees)
     */
    fun area(): Double {
        return (maxLat - minLat) * (maxLon - minLon)
    }

    companion object {
        /**
         * Calculate bounding box from a list of vertices
         *
         * @param vertices List of points defining a polygon
         * @return BoundingBox that contains all vertices
         * @throws IllegalArgumentException if vertices list is empty
         */
        fun fromVertices(vertices: List<MapPoint>): BoundingBox {
            if (vertices.isEmpty()) {
                throw IllegalArgumentException("Cannot calculate bounding box for empty vertices")
            }

            val minLat = vertices.minOf { it.latitude }
            val maxLat = vertices.maxOf { it.latitude }
            val minLon = vertices.minOf { it.longitude }
            val maxLon = vertices.maxOf { it.longitude }

            return BoundingBox(minLat, maxLat, minLon, maxLon)
        }
    }
}
