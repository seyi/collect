package org.odk.collect.android.geofencing

/**
 * Types of geofences supported by the system
 */
enum class GeofenceType {
    /**
     * State boundary
     */
    STATE,

    /**
     * Local Government Area boundary
     */
    LGA,

    /**
     * Strategic catchment area (large watershed)
     */
    STRATEGIC_CATCHMENT,

    /**
     * Micro catchment subdivision (smaller watershed within strategic catchment)
     */
    MICRO_CATCHMENT,

    /**
     * Project intervention site
     */
    INTERVENTION
}
