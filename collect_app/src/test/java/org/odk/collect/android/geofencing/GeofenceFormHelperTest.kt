package org.odk.collect.android.geofencing

import org.junit.Assert.*
import org.junit.Test
import org.odk.collect.maps.MapPoint

/**
 * Unit tests for GeofenceFormHelper
 */
class GeofenceFormHelperTest {

    @Test
    fun `getFieldDisplayName returns proper display names`() {
        assertEquals("State", GeofenceFormHelper.getFieldDisplayName("state"))
        assertEquals("State", GeofenceFormHelper.getFieldDisplayName("state_name"))
        assertEquals("LGA", GeofenceFormHelper.getFieldDisplayName("lga"))
        assertEquals("Strategic Catchment", GeofenceFormHelper.getFieldDisplayName("strategic_catchment"))
        assertEquals("Micro Catchment", GeofenceFormHelper.getFieldDisplayName("micro_catchment"))
        assertEquals("Intervention Site", GeofenceFormHelper.getFieldDisplayName("intervention"))
    }

    @Test
    fun `getFieldDisplayName handles custom field names`() {
        assertEquals("Custom Field", GeofenceFormHelper.getFieldDisplayName("custom_field"))
        assertEquals("Another Name", GeofenceFormHelper.getFieldDisplayName("another_name"))
    }

    @Test
    fun `mapFieldValue returns correct values for state fields`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            state = "Kaduna",
            lga = "Zaria",
            strategicCatchment = "Hadejia",
            microCatchment = "MC-001",
            intervention = "INT-001",
            isWithinBoundaries = true
        )

        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("state", fieldValues))
        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("state_name", fieldValues))
        assertEquals("Zaria", GeofenceFormHelper.mapFieldValue("lga", fieldValues))
        assertEquals("Zaria", GeofenceFormHelper.mapFieldValue("lga_name", fieldValues))
    }

    @Test
    fun `mapFieldValue returns correct values for catchment fields`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            state = "Kaduna",
            strategicCatchment = "Hadejia",
            microCatchment = "MC-001",
            isWithinBoundaries = true
        )

        assertEquals("Hadejia", GeofenceFormHelper.mapFieldValue("strategic_catchment", fieldValues))
        assertEquals("Hadejia", GeofenceFormHelper.mapFieldValue("scatchment", fieldValues))
        assertEquals("Hadejia", GeofenceFormHelper.mapFieldValue("s_catchment", fieldValues))
        assertEquals("MC-001", GeofenceFormHelper.mapFieldValue("micro_catchment", fieldValues))
        assertEquals("MC-001", GeofenceFormHelper.mapFieldValue("mcatchment", fieldValues))
        assertEquals("MC-001", GeofenceFormHelper.mapFieldValue("m_catchment", fieldValues))
    }

    @Test
    fun `mapFieldValue returns null for unknown fields`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            state = "Kaduna",
            isWithinBoundaries = true
        )

        assertNull(GeofenceFormHelper.mapFieldValue("unknown_field", fieldValues))
        assertNull(GeofenceFormHelper.mapFieldValue("random", fieldValues))
    }

    @Test
    fun `mapFieldValue handles case insensitivity`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues(
            state = "Kaduna",
            lga = "Zaria",
            isWithinBoundaries = true
        )

        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("STATE", fieldValues))
        assertEquals("Kaduna", GeofenceFormHelper.mapFieldValue("State", fieldValues))
        assertEquals("Zaria", GeofenceFormHelper.mapFieldValue("LGA", fieldValues))
        assertEquals("Zaria", GeofenceFormHelper.mapFieldValue("Lga", fieldValues))
    }

    @Test
    fun `LocationFieldValues has correct default values`() {
        val fieldValues = GeofenceFormHelper.LocationFieldValues()

        assertNull(fieldValues.state)
        assertNull(fieldValues.lga)
        assertNull(fieldValues.strategicCatchment)
        assertNull(fieldValues.microCatchment)
        assertNull(fieldValues.intervention)
        assertFalse(fieldValues.isWithinBoundaries)
        assertNull(fieldValues.errorMessage)
    }

    @Test
    fun `ValidationResult has correct structure`() {
        val validResult = GeofenceFormHelper.ValidationResult(
            isValid = true
        )
        assertTrue(validResult.isValid)
        assertNull(validResult.errorMessage)
        assertFalse(validResult.requiresOverride)

        val invalidResult = GeofenceFormHelper.ValidationResult(
            isValid = false,
            errorMessage = "Error message",
            requiresOverride = true
        )
        assertFalse(invalidResult.isValid)
        assertEquals("Error message", invalidResult.errorMessage)
        assertTrue(invalidResult.requiresOverride)
    }
}
