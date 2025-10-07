package org.odk.collect.android.geofencing

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GeoJsonParser
 */
class GeoJsonParserTest {

    private lateinit var parser: GeoJsonParser

    @Before
    fun setup() {
        parser = GeoJsonParser()
    }

    @Test
    fun `parse valid Polygon GeoJSON`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {"Id": 1, "NAME": "Test Area"},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.0, 9.0], [7.1, 9.0], [7.1, 9.1], [7.0, 9.1], [7.0, 9.0]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STATE, "TestState")

        assertEquals(1, polygons.size)
        assertEquals("Test Area", polygons[0].name)
        assertEquals(5, polygons[0].vertices.size)
        assertEquals(GeofenceType.STATE, polygons[0].type)
        assertEquals("TestState", polygons[0].state)
    }

    @Test
    fun `parse MultiPolygon GeoJSON`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {"Id": 2, "NAME": "Multi Area"},
                  "geometry": {
                    "type": "MultiPolygon",
                    "coordinates": [
                      [
                        [[7.0, 9.0], [7.1, 9.0], [7.1, 9.1], [7.0, 9.1], [7.0, 9.0]]
                      ],
                      [
                        [[8.0, 10.0], [8.2, 10.0], [8.2, 10.2], [8.0, 10.2], [8.0, 10.0]]
                      ]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STRATEGIC_CATCHMENT, "TestState")

        assertEquals(1, polygons.size)
        assertEquals("Multi Area", polygons[0].name)
        // Should have chosen the larger polygon (both have 5 vertices, so picks first)
        assertTrue(polygons[0].vertices.size >= 5)
    }

    @Test
    fun `parse multiple features`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {"Id": 1, "NAME": "Area 1"},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.0, 9.0], [7.1, 9.0], [7.1, 9.1], [7.0, 9.1], [7.0, 9.0]]
                    ]
                  }
                },
                {
                  "type": "Feature",
                  "properties": {"Id": 2, "NAME": "Area 2"},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[8.0, 10.0], [8.1, 10.0], [8.1, 10.1], [8.0, 10.1], [8.0, 10.0]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.MICRO_CATCHMENT, "TestState")

        assertEquals(2, polygons.size)
        assertEquals("Area 1", polygons[0].name)
        assertEquals("Area 2", polygons[1].name)
    }

    @Test
    fun `coordinate conversion from GeoJSON lon-lat to MapPoint lat-lon`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {"Id": 1, "NAME": "Test"},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.5, 10.5], [7.6, 10.6]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STATE, "TestState")

        assertEquals(1, polygons.size)

        val firstVertex = polygons[0].vertices[0]
        // GeoJSON: [7.5, 10.5] = [longitude, latitude]
        // MapPoint: (latitude, longitude)
        assertEquals(10.5, firstVertex.latitude, 0.001)
        assertEquals(7.5, firstVertex.longitude, 0.001)
    }

    @Test
    fun `extract properties correctly`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {
                    "Id": 42,
                    "NAME": "Hadejia",
                    "Shape_Area": 616.03,
                    "source_state": "Kaduna"
                  },
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.0, 9.0], [7.1, 9.0], [7.1, 9.1], [7.0, 9.0]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STRATEGIC_CATCHMENT, "Kaduna")

        assertEquals(1, polygons.size)

        val polygon = polygons[0]
        assertEquals("Hadejia", polygon.name)
        assertEquals(42, polygon.getPropertyAsInt("Id"))
        assertEquals(616.03, polygon.getPropertyAsDouble("Shape_Area"), 0.01)
        assertEquals("Kaduna", polygon.getPropertyAsString("source_state"))
    }

    @Test
    fun `generate unique IDs correctly`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {"Id": 6, "NAME": "Test"},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.0, 9.0], [7.1, 9.0], [7.1, 9.1], [7.0, 9.0]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STRATEGIC_CATCHMENT, "Kaduna")

        assertEquals(1, polygons.size)
        assertEquals("Kaduna-SC-6", polygons[0].id)
    }

    @Test
    fun `handle invalid JSON gracefully`() {
        val invalidJson = "{ invalid json }"

        val polygons = parser.parseGeoJson(invalidJson, GeofenceType.STATE, "TestState")

        // Should return empty list instead of throwing exception
        assertTrue(polygons.isEmpty())
    }

    @Test
    fun `handle missing properties`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.0, 9.0], [7.1, 9.0], [7.1, 9.1], [7.0, 9.0]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STATE, "TestState")

        assertEquals(1, polygons.size)
        // Should have generated default name
        assertTrue(polygons[0].name.contains("Unnamed"))
    }

    @Test
    fun `bounding box automatically calculated`() {
        val geoJson = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {"Id": 1, "NAME": "Test"},
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                      [[7.0, 9.0], [8.0, 9.0], [8.0, 10.0], [7.0, 10.0], [7.0, 9.0]]
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val polygons = parser.parseGeoJson(geoJson, GeofenceType.STATE, "TestState")

        assertEquals(1, polygons.size)

        val bbox = polygons[0].boundingBox
        assertEquals(9.0, bbox.minLat, 0.001)
        assertEquals(10.0, bbox.maxLat, 0.001)
        assertEquals(7.0, bbox.minLon, 0.001)
        assertEquals(8.0, bbox.maxLon, 0.001)
    }
}
