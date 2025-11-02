package org.odk.collect.android.cosmosdb.models

import com.google.gson.annotations.SerializedName

/**
 * ODK Collect form submission document for Azure Cosmos DB
 * Follows the hierarchical structure with foreign key references to existing containers
 *
 * This will be stored in the "ODKSubmissions" container
 * Partition key: /state
 */
data class ODKSubmission(
    @SerializedName("id")
    val id: String,

    @SerializedName("submissionId")
    val submissionId: String,

    @SerializedName("formId")
    val formId: String,

    @SerializedName("formVersion")
    val formVersion: String? = null,

    // User Information
    @SerializedName("submittedBy")
    val submittedBy: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("userRole")
    val userRole: String,

    @SerializedName("state")
    val state: String,  // Partition key

    // Timestamps
    @SerializedName("submittedAt")  // Changed from "submissionTime" to match Azure Function
    val submittedAt: String,

    @SerializedName("syncedAt")
    val syncedAt: String,

    @SerializedName("lastModified")
    val lastModified: String,

    // Geofence References (Foreign keys to hierarchical containers)
    @SerializedName("geofenceReference")
    val geofenceReference: GeofenceReference,

    // GPS Location
    @SerializedName("geolocation")
    val geolocation: GeoLocation? = null,

    // Form Data (all form answers as key-value pairs)
    @SerializedName("formData")  // Changed from "data" to match Azure Function
    val formData: Map<String, Any?>,

    // Metadata
    @SerializedName("metadata")
    val metadata: SubmissionMetadata,

    // Hierarchy information (similar to your existing structure)
    @SerializedName("hierarchy")
    val hierarchy: HierarchyInfo,

    // Relationships (for consistency with your existing pattern)
    @SerializedName("relationships")
    val relationships: RelationshipInfo? = null
) {
    companion object {
        /**
         * Generate submission ID
         * Format: odk_{timestamp}_{userId}
         */
        fun generateSubmissionId(userId: String): String {
            val timestamp = System.currentTimeMillis()
            val random = (0..999999).random().toString().padStart(6, '0')
            return "odk_${timestamp}_${userId}_${random}"
        }

        /**
         * Get current ISO 8601 timestamp
         */
        fun getCurrentTimestamp(): String {
            return java.time.Instant.now().toString()
        }
    }
}

/**
 * Hierarchy information showing the relationship to parent containers
 */
data class HierarchyInfo(
    @SerializedName("state")
    val state: String,

    @SerializedName("level")
    val level: String = "odk_submission",

    @SerializedName("displayName")
    val displayName: String = "ODK Submission",

    @SerializedName("strategicCatchmentId")
    val strategicCatchmentId: String? = null,

    @SerializedName("strategicCatchmentName")
    val strategicCatchmentName: String? = null,

    @SerializedName("microCatchmentId")
    val microCatchmentId: String? = null,

    @SerializedName("microCatchmentName")
    val microCatchmentName: String? = null
)

/**
 * Relationship information showing parent-child links
 */
data class RelationshipInfo(
    @SerializedName("parentLevel")
    val parentLevel: String = "micro_catchments",

    @SerializedName("parentId")
    val parentId: String? = null,  // microCatchmentId

    @SerializedName("childLevels")
    val childLevels: List<String> = emptyList()
)
