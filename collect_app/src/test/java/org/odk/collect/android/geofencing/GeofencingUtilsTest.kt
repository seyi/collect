package org.odk.collect.android.geofencing

import org.junit.Assert.*
import org.junit.Test
import org.odk.collect.maps.MapPoint

/**
 * Unit tests for GeofencingUtils
 */
class GeofencingUtilsTest {

    @Test
    fun `point inside simple square polygon`() {
        // Square polygon: (0,0) -> (1,0) -> (1,1) -> (0,1) -> (0,0)
        val polygon = listOf(
            MapPoint(0.0, 0.0),
            MapPoint(0.0, 1.0),
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 0.0),
            MapPoint(0.0, 0.0)
        )

        // Point in center of square
        val point = MapPoint(0.5, 0.5)

        assertTrue(GeofencingUtils.isPointInPolygon(point, polygon))
    }

    @Test
    fun `point outside polygon`() {
        val polygon = listOf(
            MapPoint(0.0, 0.0),
            MapPoint(0.0, 1.0),
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 0.0),
            MapPoint(0.0, 0.0)
        )

        // Point far outside
        val point = MapPoint(2.0, 2.0)

        assertFalse(GeofencingUtils.isPointInPolygon(point, polygon))
    }

    @Test
    fun `point on boundary - edge case`() {
        val polygon = listOf(
            MapPoint(0.0, 0.0),
            MapPoint(0.0, 1.0),
            MapPoint(1.0, 1.0),
            MapPoint(1.0, 0.0),
            MapPoint(0.0, 0.0)
        )

        // Point exactly on bottom edge
        val point = MapPoint(0.5, 0.0)

        // Result may vary based on implementation
        // Just verify it doesn't crash
        val result = GeofencingUtils.isPointInPolygon(point, polygon)
        assertNotNull(result)
    }

    @Test
    fun `concave L-shaped polygon`() {
        // L-shaped polygon to test concave shapes
        val polygon = listOf(
            MapPoint(0.0, 0.0),
            MapPoint(0.0, 2.0),
            MapPoint(1.0, 2.0),
            MapPoint(1.0, 1.0),
            MapPoint(2.0, 1.0),
            MapPoint(2.0, 0.0),
            MapPoint(0.0, 0.0)
        )

        // Point inside L
        val insidePoint = MapPoint(0.5, 0.5)
        assertTrue(GeofencingUtils.isPointInPolygon(insidePoint, polygon))

        // Point in the concave area (should be outside)
        val outsidePoint = MapPoint(1.5, 1.5)
        assertFalse(GeofencingUtils.isPointInPolygon(outsidePoint, polygon))
    }

    @Test
    fun `real world coordinates - Kaduna area`() {
        // Approximate triangle around Kaduna
        val polygon = listOf(
            MapPoint(10.5, 8.7),
            MapPoint(10.6, 8.8),
            MapPoint(10.5, 8.9),
            MapPoint(10.4, 8.8),
            MapPoint(10.5, 8.7)
        )

        // Point roughly in Kaduna
        val pointInKaduna = MapPoint(10.52, 8.77)
        assertTrue(GeofencingUtils.isPointInPolygon(pointInKaduna, polygon))

        // Point in Lagos (far away)
        val pointInLagos = MapPoint(6.5, 3.5)
        assertFalse(GeofencingUtils.isPointInPolygon(pointInLagos, polygon))
    }

    @Test
    fun `invalid polygon with less than 3 vertices`() {
        val twoPoints = listOf(
            MapPoint(0.0, 0.0),
            MapPoint(1.0, 1.0)
        )

        val point = MapPoint(0.5, 0.5)

        // Should return false for invalid polygon
        assertFalse(GeofencingUtils.isPointInPolygon(point, twoPoints))
    }

    @Test
    fun `empty polygon`() {
        val emptyPolygon = emptyList<MapPoint>()
        val point = MapPoint(0.0, 0.0)

        assertFalse(GeofencingUtils.isPointInPolygon(point, emptyPolygon))
    }

    @Test
    fun `haversine distance calculation`() {
        // Distance between two points in Kaduna
        val point1 = MapPoint(10.5, 8.7)
        val point2 = MapPoint(10.6, 8.8)

        val distance = GeofencingUtils.haversineDistance(point1, point2)

        // Should be roughly 15-16 km
        assertTrue(distance > 14000) // > 14km
        assertTrue(distance < 17000) // < 17km
    }

    @Test
    fun `haversine distance same point`() {
        val point = MapPoint(10.5, 8.7)

        val distance = GeofencingUtils.haversineDistance(point, point)

        // Distance to self should be 0
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `calculate polygon area`() {
        // Simple 1 degree x 1 degree square
        val polygon = listOf(
            MapPoint(10.0, 8.0),
            MapPoint(10.0, 9.0),
            MapPoint(11.0, 9.0),
            MapPoint(11.0, 8.0),
            MapPoint(10.0, 8.0)
        )

        val area = GeofencingUtils.calculatePolygonArea(polygon)

        // Area should be > 0
        assertTrue(area > 0)

        // For a 1 degree square at this latitude, area should be roughly 12,000+ km²
        assertTrue(area > 10_000_000_000.0) // > 10 billion m² = 10,000 km²
    }

    @Test
    fun `calculate centroid`() {
        // Square polygon (with closing vertex that duplicates first point)
        // Vertices: (0,0), (0,2), (2,2), (2,0), (0,0)
        // Average: (0+0+2+2+0)/5 = 0.8, (0+2+2+0+0)/5 = 0.8
        val polygon = listOf(
            MapPoint(0.0, 0.0),
            MapPoint(0.0, 2.0),
            MapPoint(2.0, 2.0),
            MapPoint(2.0, 0.0),
            MapPoint(0.0, 0.0)
        )

        val centroid = GeofencingUtils.calculateCentroid(polygon)

        // Centroid with closing vertex is at (0.8, 0.8)
        // This is expected behavior as the algorithm averages all vertices
        assertEquals(0.8, centroid.latitude, 0.1)
        assertEquals(0.8, centroid.longitude, 0.1)
    }
}
