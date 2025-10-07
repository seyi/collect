package org.odk.collect.android.geofencing

import org.junit.Assert.*
import org.junit.Test
import org.odk.collect.maps.MapPoint

/**
 * Unit tests for BoundingBox
 */
class BoundingBoxTest {

    @Test
    fun `point inside bounding box`() {
        val box = BoundingBox(
            minLat = 0.0,
            maxLat = 10.0,
            minLon = 0.0,
            maxLon = 10.0
        )

        val point = MapPoint(5.0, 5.0)

        assertTrue(box.contains(point))
    }

    @Test
    fun `point outside bounding box`() {
        val box = BoundingBox(
            minLat = 0.0,
            maxLat = 10.0,
            minLon = 0.0,
            maxLon = 10.0
        )

        val point = MapPoint(15.0, 15.0)

        assertFalse(box.contains(point))
    }

    @Test
    fun `point on boundary edge`() {
        val box = BoundingBox(
            minLat = 0.0,
            maxLat = 10.0,
            minLon = 0.0,
            maxLon = 10.0
        )

        // Point exactly on min edge
        val pointOnMin = MapPoint(0.0, 0.0)
        assertTrue(box.contains(pointOnMin))

        // Point exactly on max edge
        val pointOnMax = MapPoint(10.0, 10.0)
        assertTrue(box.contains(pointOnMax))
    }

    @Test
    fun `calculate area`() {
        val box = BoundingBox(
            minLat = 0.0,
            maxLat = 10.0,
            minLon = 0.0,
            maxLon = 10.0
        )

        val area = box.area()

        assertEquals(100.0, area, 0.001) // 10 * 10 = 100 square degrees
    }

    @Test
    fun `fromVertices creates correct bounding box`() {
        val vertices = listOf(
            MapPoint(5.0, 3.0),
            MapPoint(2.0, 7.0),
            MapPoint(8.0, 1.0),
            MapPoint(4.0, 9.0)
        )

        val box = BoundingBox.fromVertices(vertices)

        assertEquals(2.0, box.minLat, 0.001)
        assertEquals(8.0, box.maxLat, 0.001)
        assertEquals(1.0, box.minLon, 0.001)
        assertEquals(9.0, box.maxLon, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `fromVertices throws exception for empty list`() {
        BoundingBox.fromVertices(emptyList())
    }

    @Test
    fun `real world coordinates - Kaduna bounding box`() {
        // Approximate bounding box for Kaduna state
        val box = BoundingBox(
            minLat = 9.0,
            maxLat = 11.5,
            minLon = 6.5,
            maxLon = 8.5
        )

        // Point in Kaduna city
        val kadunaCity = MapPoint(10.52, 7.44)
        assertTrue(box.contains(kadunaCity))

        // Point in Lagos (outside)
        val lagos = MapPoint(6.5, 3.4)
        assertFalse(box.contains(lagos))
    }
}
