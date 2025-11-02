package org.odk.collect.android.cosmosdb

import android.content.Context
import android.location.Location
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.javarosa.core.model.FormIndex
import org.javarosa.core.model.instance.TreeReference
import org.odk.collect.android.BuildConfig
import org.odk.collect.android.activities.LoginActivity
import org.odk.collect.android.cosmosdb.models.*
import org.odk.collect.android.javarosawrapper.FormController
import timber.log.Timber
import java.time.Instant
import java.util.*

/**
 * Extracts form data from ODK FormController and builds ODKSubmission objects
 * Handles form traversal, data extraction, and metadata collection
 */
class SubmissionExtractor(private val context: Context) {

    /**
     * Extract submission from FormController
     *
     * @param formController The form controller containing submission data
     * @param location GPS location (if available)
     * @return ODKSubmission ready for upload to Cosmos DB
     */
    suspend fun extractSubmission(
        formController: FormController,
        location: Location? = null
    ): ODKSubmission = withContext(Dispatchers.Default) {
        try {
            Timber.d("Extracting submission from form: ${formController.getFormTitle()}")

            // Get user data
            val userData = LoginActivity.getUserData(context)
                ?: throw IllegalStateException("User data not available. User must be logged in.")

            // Extract form data as key-value pairs
            val formData = extractFormData(formController)

            // Extract geofence field values
            val geofenceData = extractGeofenceData(formData)

            // Resolve geofence IDs from hierarchical containers
            val geofenceReference = resolveGeofenceIds(
                state = geofenceData["state"] as? String ?: userData.state,
                strategicCatchmentName = geofenceData["strategicCatchment"] as? String,
                microCatchmentName = geofenceData["microCatchment"] as? String,
                lga = geofenceData["lga"] as? String
            )

            // Create geolocation
            val geoLocation = location?.let {
                GeoLocation(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracy = it.accuracy.toDouble(),
                    altitude = it.altitude,
                    capturedAt = Instant.now().toString()
                )
            }

            // Create metadata
            val metadata = SubmissionMetadata(
                deviceId = getDeviceId(),
                appVersion = BuildConfig.VERSION_NAME,
                androidVersion = Build.VERSION.RELEASE,
                deviceManufacturer = Build.MANUFACTURER,
                deviceModel = Build.MODEL,
                syncStatus = "synced",
                syncAttempts = 1,
                instanceId = formController.getAbsoluteInstancePath()
            )

            // Create hierarchy info
            val hierarchy = HierarchyInfo(
                state = geofenceReference.state,
                level = "odk_submission",
                displayName = "ODK Submission",
                strategicCatchmentId = geofenceReference.strategicCatchmentId,
                strategicCatchmentName = geofenceReference.strategicCatchmentName,
                microCatchmentId = geofenceReference.microCatchmentId,
                microCatchmentName = geofenceReference.microCatchmentName
            )

            // Create relationship info (if micro catchment exists)
            val relationships = if (geofenceReference.microCatchmentId != null) {
                RelationshipInfo(
                    parentLevel = "micro_catchments",
                    parentId = geofenceReference.microCatchmentId,
                    childLevels = emptyList()
                )
            } else null

            // Generate submission ID and timestamps
            val submissionId = ODKSubmission.generateSubmissionId(userData.userId)
            val timestamp = ODKSubmission.getCurrentTimestamp()

            // Build ODKSubmission
            ODKSubmission(
                id = submissionId,
                submissionId = submissionId,
                formId = formController.getFormDef()?.mainInstance?.name ?: "unknown",
                formVersion = null, // TODO: Get form version from FormDef
                submittedBy = userData.email,
                userId = userData.userId,
                userName = userData.username,
                userRole = userData.role.name,
                state = geofenceReference.state,
                submittedAt = timestamp,  // Changed from submissionTime
                syncedAt = timestamp,
                lastModified = timestamp,
                geofenceReference = geofenceReference,
                geolocation = geoLocation,
                formData = formData,  // Changed from data
                metadata = metadata,
                hierarchy = hierarchy,
                relationships = relationships
            )

        } catch (e: Exception) {
            Timber.e(e, "Error extracting submission")
            throw e
        }
    }

    /**
     * Extract all form data as key-value pairs
     * Traverses the entire form structure
     */
    private fun extractFormData(formController: FormController): Map<String, Any?> {
        val formData = mutableMapOf<String, Any?>()

        try {
            // Save current position
            val currentIndex = formController.getFormIndex()

            // Jump to beginning of form
            formController.jumpToIndex(FormIndex.createBeginningOfFormIndex())

            // Traverse form and extract all answers
            var index = formController.getFormIndex()
            while (index != null && !index.isEndOfFormIndex) {
                try {
                    val prompt = formController.getQuestionPrompt(index)

                    if (prompt != null && !prompt.isReadOnly) {
                        val questionText = prompt.questionText
                        val answerText = prompt.answerText

                        if (questionText != null && answerText != null && answerText.isNotEmpty()) {
                            // Use question text as key, clean it for use as field name
                            val fieldName = cleanFieldName(questionText)
                            formData[fieldName] = answerText

                            // Also store with original question text
                            formData["_original_$fieldName"] = questionText
                        }
                    }

                    // Move to next question (handle nullable result)
                    index = index?.let { formController.getNextQuestionIndex(it) }

                } catch (e: Exception) {
                    Timber.w(e, "Error extracting question at index: $index")
                    // Continue to next question (handle nullable index)
                    index = index?.let { formController.getNextQuestionIndex(it) }
                }
            }

            // Restore original position
            currentIndex?.let { formController.jumpToIndex(it) }

            Timber.d("Extracted ${formData.size} form fields")

        } catch (e: Exception) {
            Timber.e(e, "Error traversing form")
        }

        return formData
    }

    /**
     * Extract geofence-specific data from form data
     * Looks for state, LGA, strategic catchment, and micro catchment fields
     */
    private fun extractGeofenceData(formData: Map<String, Any?>): Map<String, Any?> {
        val geofenceData = mutableMapOf<String, Any?>()

        // Search for geofence fields by common field names
        formData.forEach { (key, value) ->
            val keyLower = key.lowercase()

            when {
                keyLower.contains("state") && !keyLower.contains("status") -> {
                    geofenceData["state"] = value
                }
                keyLower.contains("lga") -> {
                    geofenceData["lga"] = value
                }
                keyLower.contains("strategic") && keyLower.contains("catchment") -> {
                    geofenceData["strategicCatchment"] = value
                }
                keyLower.contains("micro") && keyLower.contains("catchment") -> {
                    geofenceData["microCatchment"] = value
                }
            }
        }

        Timber.d("Extracted geofence data: $geofenceData")
        return geofenceData
    }

    /**
     * Resolve geofence names to IDs using GeofenceIdResolver
     */
    private suspend fun resolveGeofenceIds(
        state: String,
        strategicCatchmentName: String?,
        microCatchmentName: String?,
        lga: String?
    ): GeofenceReference = withContext(Dispatchers.IO) {
        try {
            // Initialize GeofenceIdResolver if needed
            val resolver = GeofenceIdResolver.getInstance()
            if (!resolver.isInitialized()) {
                // Simplified initialization - no connection string needed
                resolver.initialize("", "")
            }

            // Resolve IDs
            resolver.resolveGeofenceIds(
                state = state,
                strategicCatchmentName = strategicCatchmentName,
                microCatchmentName = microCatchmentName,
                lga = lga
            )

        } catch (e: Exception) {
            Timber.e(e, "Error resolving geofence IDs, using names only")

            // Return reference with names only (IDs will be null)
            GeofenceReference(
                state = state,
                lga = lga,
                strategicCatchmentId = null,
                strategicCatchmentName = strategicCatchmentName,
                microCatchmentId = null,
                microCatchmentName = microCatchmentName
            )
        }
    }

    /**
     * Clean field name for use as JSON key
     * Removes special characters, normalizes spaces
     */
    private fun cleanFieldName(fieldName: String): String {
        return fieldName
            .trim()
            .replace(Regex("[^a-zA-Z0-9\\s]"), "") // Remove special chars
            .replace(Regex("\\s+"), "_") // Replace spaces with underscore
            .lowercase()
            .take(100) // Limit length
    }

    /**
     * Get unique device ID
     */
    private fun getDeviceId(): String {
        return try {
            android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ) ?: UUID.randomUUID().toString()
        } catch (e: Exception) {
            Timber.w(e, "Error getting device ID, using random UUID")
            UUID.randomUUID().toString()
        }
    }

    companion object {
        /**
         * Check if GeofenceIdResolver is initialized
         * This is needed before extraction if we want to resolve IDs
         */
        fun isResolverInitialized(): Boolean {
            return try {
                GeofenceIdResolver.getInstance().isInitialized()
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Initialize the resolver proactively
         */
        suspend fun initializeResolver(context: Context) {
            try {
                CosmosDbConfig.initialize(context)
                val resolver = GeofenceIdResolver.getInstance()
                // Simplified initialization - no connection string needed
                resolver.initialize("", "")
                Timber.d("GeofenceIdResolver initialized proactively (simplified mode)")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing GeofenceIdResolver")
            }
        }
    }
}
