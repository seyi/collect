package org.odk.collect.android.cosmosdb.models

import com.google.gson.annotations.SerializedName

/**
 * References to existing hierarchical geofence containers
 * Links ODK submission to strategic and micro catchment IDs
 */
data class GeofenceReference(
    @SerializedName("state")
    val state: String,

    @SerializedName("lga")
    val lga: String? = null,

    @SerializedName("strategicCatchmentId")
    val strategicCatchmentId: String? = null,

    @SerializedName("strategicCatchmentName")
    val strategicCatchmentName: String? = null,

    @SerializedName("microCatchmentId")
    val microCatchmentId: String? = null,

    @SerializedName("microCatchmentName")
    val microCatchmentName: String? = null
) {
    /**
     * Check if this has valid strategic catchment reference
     */
    fun hasStrategicCatchment(): Boolean {
        return strategicCatchmentId != null && strategicCatchmentName != null
    }

    /**
     * Check if this has valid micro catchment reference
     */
    fun hasMicroCatchment(): Boolean {
        return microCatchmentId != null && microCatchmentName != null
    }

    /**
     * Check if this has complete hierarchy (all levels populated)
     */
    fun isCompleteHierarchy(): Boolean {
        return hasStrategicCatchment() && hasMicroCatchment()
    }
}
