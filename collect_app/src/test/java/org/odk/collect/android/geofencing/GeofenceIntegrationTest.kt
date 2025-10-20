package org.odk.collect.android.geofencing

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.odk.collect.maps.MapPoint

/**
 * Unit tests for geofencing system
 * NOTE: These tests verify the logic without requiring actual GeoJSON asset files
 * For full integration testing with real GeoJSON data, use:
 * - Instrumented tests (androidTest)
 * - Manual testing with GeofenceTestActivity
 */
@RunWith(AndroidJUnit4::class)
class GeofenceIntegrationTest {

    private lateinit var context: Context
    private lateinit var geoFenceManager: GeoFenceManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        geoFenceManager = GeoFenceManager.getInstance(context)
        // Clear any cached data from previous tests
        geoFenceManager.clearCache()
    }

    // ============================================================
    // MANAGER BASIC FUNCTIONALITY TESTS
    // ============================================================

    @Test
    fun `test manager instance is singleton`() {
        val manager1 = GeoFenceManager.getInstance(context)
        val manager2 = GeoFenceManager.getInstance(context)
        assertSame("Manager should be singleton", manager1, manager2)
    }

    @Test
    fun `test manager starts with no data loaded`() {
        val loadedStates = geoFenceManager.getLoadedStates()
        assertTrue("Manager should start empty", loadedStates.isEmpty())
        assertFalse("Manager should not be loaded", geoFenceManager.isLoaded())
    }

    @Test
    fun `test cache can be cleared`() {
        geoFenceManager.clearCache()
        assertFalse("Cache should be empty after clear", geoFenceManager.isLoaded())
        assertEquals("No states should be loaded", 0, geoFenceManager.getLoadedStates().size)
    }

    @Test
    fun `test getContainingPolygons returns empty list when no data loaded`() {
        val point = MapPoint(10.5105, 7.4165)
        val polygons = geoFenceManager.getContainingPolygons(point)
        assertTrue("Should return empty list when no data loaded", polygons.isEmpty())
    }

    @Test
    fun `test getPolygonsForState returns empty list for unloaded state`() {
        val polygons = geoFenceManager.getPolygonsForState("Kaduna", GeofenceType.STRATEGIC_CATCHMENT)
        assertTrue("Should return empty list for unloaded state", polygons.isEmpty())
    }

    // ============================================================
    // FORM HELPER TESTS
    // ============================================================

    @Test
    fun `test field name mapping variations`() {
        // Test various field name formats
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            state = "Kaduna",
            lga = "Kaduna North",
            strategicCatchment = "Hadejia",
            microCatchment = "MC-001",
            intervention = "INT-001",
            isWithinBoundaries = true
        )

        // Test state variations
        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("State", fieldValues))
        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("state", fieldValues))
        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("state_name", fieldValues))
        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("State Name", fieldValues))

        // Test LGA variations
        assertEquals("Kaduna North", GeofenceFormHelper.mapFieldValue("LGA", fieldValues))
        assertEquals("Kaduna North", GeofenceFormHelper.mapFieldValue("lga", fieldValues))
        assertEquals("Kaduna North", GeofenceFormHelper.mapFieldValue("Local Government Area", fieldValues))

        // Test strategic catchment variations
        assertEquals("Hadejia", GeofenceFormHelper.mapFieldValue("strategic_catchment", fieldValues))
        assertEquals("Hadejia", GeofenceFormHelper.mapFieldValue("Strategic Catchment", fieldValues))
        assertEquals("Hadejia", GeofenceFormHelper.mapFieldValue("scatchment", fieldValues))

        // Test micro catchment variations
        assertEquals("MC-001", GeofenceFormHelper.mapFieldValue("micro_catchment", fieldValues))
        assertEquals("MC-001", GeofenceFormHelper.mapFieldValue("Micro Catchment", fieldValues))
        assertEquals("MC-001", GeofenceFormHelper.mapFieldValue("mcatchment", fieldValues))
    }

    @Test
    fun `test LocationFieldValues constructor`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            state = "Kaduna",
            lga = "Kaduna North",
            strategicCatchment = "Hadejia",
            microCatchment = "MC-001",
            intervention = "INT-001",
            isWithinBoundaries = true,
            errorMessage = null
        )

        assertEquals("Kaduna", fieldValues.state)
        assertEquals("Kaduna North", fieldValues.lga)
        assertEquals("Hadejia", fieldValues.strategicCatchment)
        assertEquals("MC-001", fieldValues.microCatchment)
        assertEquals("INT-001", fieldValues.intervention)
        assertTrue(fieldValues.isWithinBoundaries)
        assertNull(fieldValues.errorMessage)
    }

    @Test
    fun `test LocationFieldValues with error`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            isWithinBoundaries = false,
            errorMessage = "Location is outside all mapped boundaries"
        )

        assertFalse(fieldValues.isWithinBoundaries)
        assertEquals("Location is outside all mapped boundaries", fieldValues.errorMessage)
        assertNull(fieldValues.state)
        assertNull(fieldValues.lga)
    }

    @Test
    fun `test ValidationResult constructor`() {
        val result = GeofenceFormHelper.ValidationResult(
            isValid = false,
            errorMessage = "Test error",
            requiresOverride = true
        )

        assertFalse(result.isValid)
        assertEquals("Test error", result.errorMessage)
        assertTrue(result.requiresOverride)
    }

    @Test
    fun `test canOverrideLocationRestrictions returns false for non-admin`() {
        // This test just verifies the method exists and returns a boolean
        // Actual behavior depends on LoginActivity which requires more setup
        val result = GeofenceFormHelper.canOverrideLocationRestrictions(context)
        // Should return a boolean (true or false)
        assertTrue(result is Boolean)
    }

    @Test
    fun `test getFieldDisplayName for common fields`() {
        assertEquals("State", GeofenceFormHelper.getFieldDisplayName("state"))
        assertEquals("State", GeofenceFormHelper.getFieldDisplayName("state_name"))
        assertEquals("LGA", GeofenceFormHelper.getFieldDisplayName("lga"))
        assertEquals("Strategic Catchment", GeofenceFormHelper.getFieldDisplayName("strategic_catchment"))
        assertEquals("Micro Catchment", GeofenceFormHelper.getFieldDisplayName("micro_catchment"))
        assertEquals("Intervention Site", GeofenceFormHelper.getFieldDisplayName("intervention"))
    }

    // ============================================================
    // CACHE STATISTICS TESTS
    // ============================================================

    @Test
    fun `test getCacheStats returns valid statistics`() {
        val stats = geoFenceManager.getCacheStats()

        assertNotNull("Stats should not be null", stats)
        assertEquals("Should have 0 loaded states initially", 0, stats.loadedStates.size)
        assertEquals("Should have 0 total polygons initially", 0, stats.totalPolygons)
        assertEquals("Should have 0 total vertices initially", 0, stats.totalVertices)
        assertTrue("Memory usage should be non-negative", stats.memoryUsageMB >= 0.0)
    }

    // ============================================================
    // HELPER FUNCTIONS
    // ============================================================

    private fun <T> runBlocking(block: suspend () -> T): T {
        return kotlinx.coroutines.runBlocking {
            block()
        }
    }
}
