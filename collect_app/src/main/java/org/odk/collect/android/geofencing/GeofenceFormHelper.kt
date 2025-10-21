package org.odk.collect.android.geofencing

import android.content.Context
import org.odk.collect.android.activities.LoginActivity
import org.odk.collect.android.authentication.UserRole
import org.odk.collect.maps.MapPoint
import timber.log.Timber

/**
 * Helper class for integrating geofencing with form fields.
 * Provides auto-population and validation functionality.
 */
object GeofenceFormHelper {

    /**
     * Result of location-based field population
     */
    data class LocationFieldValues(
        val state: String? = null,
        val lga: String? = null,
        val strategicCatchment: String? = null,
        val microCatchment: String? = null,
        val isWithinBoundaries: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * Validation result for location-based restrictions
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val requiresOverride: Boolean = false
    )

    /**
     * Auto-populate location fields based on current GPS location (blocking version for Java)
     *
     * @param context Android context
     * @param location Current GPS location
     * @return LocationFieldValues with populated values or error
     */
    @JvmStatic
    fun autoPopulateLocationFieldsBlocking(
        context: Context,
        location: MapPoint
    ): LocationFieldValues {
        try {
            val geoFenceManager = GeoFenceManager.getInstance(context)

            // Get all containing polygons at this location
            val polygons = geoFenceManager.getContainingPolygons(location)

            if (polygons.isEmpty()) {
                return LocationFieldValues(
                    isWithinBoundaries = false,
                    errorMessage = "Location is outside all mapped boundaries"
                )
            }

            // Extract different polygon types (excluding Intervention Site)
            Timber.d("Found ${polygons.size} polygons at location")
            polygons.forEach { polygon ->
                Timber.d("  - ${polygon.type}: ${polygon.name} (${polygon.id})")
            }

            val state = polygons.find { it.type == GeofenceType.STATE }
            val lga = polygons.find { it.type == GeofenceType.LGA }
            val strategicCatchment = polygons.find { it.type == GeofenceType.STRATEGIC_CATCHMENT }
            val microCatchment = polygons.find { it.type == GeofenceType.MICRO_CATCHMENT }

            Timber.d("Extracted: State=${state?.name}, LGA=${lga?.name}, Strategic=${strategicCatchment?.name}, Micro=${microCatchment?.name}")

            return LocationFieldValues(
                state = state?.name,
                lga = lga?.name,
                strategicCatchment = strategicCatchment?.name,
                microCatchment = microCatchment?.name,
                isWithinBoundaries = true
            )
        } catch (e: Exception) {
            Timber.e(e, "Error auto-populating location fields")
            return LocationFieldValues(
                isWithinBoundaries = false,
                errorMessage = "Error determining location: ${e.message}"
            )
        }
    }

    /**
     * Auto-populate location fields based on current GPS location (suspend version for Kotlin)
     *
     * @param context Android context
     * @param location Current GPS location
     * @return LocationFieldValues with populated values or error
     */
    suspend fun autoPopulateLocationFields(
        context: Context,
        location: MapPoint
    ): LocationFieldValues {
        return autoPopulateLocationFieldsBlocking(context, location)
    }

    /**
     * Validate location against user role restrictions (blocking version for Java)
     *
     * @param context Android context
     * @param location Current GPS location
     * @return ValidationResult indicating if location is valid for user
     */
    @JvmStatic
    fun validateLocationForUserBlocking(
        context: Context,
        location: MapPoint
    ): ValidationResult {
        try {
            val userRole = LoginActivity.getUserRole(context)
            val userState = LoginActivity.getUserState(context)

            // Federal users and admins can work anywhere
            if (userRole.isFederalLevel() || userRole == UserRole.ADMIN) {
                return ValidationResult(isValid = true)
            }

            // State users must be in their assigned state
            if (userRole.isStateLevel()) {
                if (userState.isEmpty()) {
                    return ValidationResult(
                        isValid = false,
                        errorMessage = "User has no assigned state",
                        requiresOverride = false
                    )
                }

                val fieldValues = autoPopulateLocationFieldsBlocking(context, location)

                if (!fieldValues.isWithinBoundaries) {
                    return ValidationResult(
                        isValid = false,
                        errorMessage = "Location is outside all mapped boundaries",
                        requiresOverride = true
                    )
                }

                if (fieldValues.state != userState) {
                    return ValidationResult(
                        isValid = false,
                        errorMessage = "You are in ${fieldValues.state} but assigned to $userState. " +
                                "You can only collect data in your assigned state.",
                        requiresOverride = true
                    )
                }

                return ValidationResult(isValid = true)
            }

            // Test users and unknown roles - allow with warning
            return ValidationResult(isValid = true)

        } catch (e: Exception) {
            Timber.e(e, "Error validating location for user")
            return ValidationResult(
                isValid = false,
                errorMessage = "Error validating location: ${e.message}",
                requiresOverride = false
            )
        }
    }

    /**
     * Validate location against user role restrictions (suspend version for Kotlin)
     *
     * @param context Android context
     * @param location Current GPS location
     * @return ValidationResult indicating if location is valid for user
     */
    suspend fun validateLocationForUser(
        context: Context,
        location: MapPoint
    ): ValidationResult {
        return validateLocationForUserBlocking(context, location)
    }

    /**
     * Check if user can override location restrictions
     *
     * @param context Android context
     * @return True if user has override privileges
     */
    @JvmStatic
    fun canOverrideLocationRestrictions(context: Context): Boolean {
        val userRole = LoginActivity.getUserRole(context)
        return userRole == UserRole.FEDERAL_ADMIN || userRole == UserRole.ADMIN
    }

    /**
     * Get user-friendly field name from form field name
     */
    fun getFieldDisplayName(fieldName: String): String {
        return when (fieldName.lowercase()) {
            "state", "state_name" -> "State"
            "lga", "lga_name" -> "LGA"
            "strategic_catchment", "scatchment" -> "Strategic Catchment"
            "micro_catchment", "mcatchment" -> "Micro Catchment"
            else -> fieldName.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Map field values to form field names
     * Common field name variations handled
     * Note: Intervention Site fields are no longer auto-populated
     */
    fun mapFieldValue(fieldName: String, fieldValues: LocationFieldValues): String? {
        // Normalize field name: trim, lowercase, remove extra spaces
        val normalizedName = fieldName.trim().lowercase().replace("\\s+".toRegex(), " ")

        return when (normalizedName) {
            // State field variations
            "state", "state_name", "state name" -> fieldValues.state

            // LGA field variations
            "lga", "lga_name", "lga name",
            "local government area", "local government",
            "local govt area", "local govt" -> fieldValues.lga

            // Strategic catchment variations
            "strategic_catchment", "strategic catchment",
            "scatchment", "s_catchment", "s catchment",
            "strategiccatchment" -> fieldValues.strategicCatchment

            // Micro catchment variations
            "micro_catchment", "micro catchment",
            "mcatchment", "m_catchment", "m catchment",
            "microcatchment" -> fieldValues.microCatchment

            else -> null
        }
    }
}
