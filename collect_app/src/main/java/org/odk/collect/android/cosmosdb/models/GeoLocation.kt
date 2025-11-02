package org.odk.collect.android.cosmosdb.models

import com.google.gson.annotations.SerializedName

/**
 * GPS location information from the mobile device
 */
data class GeoLocation(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("accuracy")
    val accuracy: Double? = null,

    @SerializedName("altitude")
    val altitude: Double? = null,

    @SerializedName("capturedAt")
    val capturedAt: String? = null
) {
    /**
     * Convert to GeoJSON Point format (if needed for spatial queries)
     */
    fun toGeoJsonPoint(): Map<String, Any> {
        return mapOf(
            "type" to "Point",
            "coordinates" to listOf(longitude, latitude, altitude ?: 0.0)
        )
    }
}
