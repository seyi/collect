package org.odk.collect.android.cosmosdb.models

import com.google.gson.annotations.SerializedName

/**
 * Device and app metadata for the submission
 */
data class SubmissionMetadata(
    @SerializedName("deviceId")
    val deviceId: String,

    @SerializedName("appVersion")
    val appVersion: String,

    @SerializedName("androidVersion")
    val androidVersion: String,

    @SerializedName("deviceManufacturer")
    val deviceManufacturer: String? = null,

    @SerializedName("deviceModel")
    val deviceModel: String? = null,

    @SerializedName("syncStatus")
    val syncStatus: String = "synced",

    @SerializedName("syncAttempts")
    val syncAttempts: Int = 1,

    @SerializedName("instanceId")
    val instanceId: String? = null
)
