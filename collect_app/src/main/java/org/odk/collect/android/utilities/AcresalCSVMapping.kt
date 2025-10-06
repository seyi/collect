package org.odk.collect.android.utilities

import timber.log.Timber
import java.util.Locale

/**
 * FIXED: CSV mapping that matches your actual CSV column names
 * Updated to match the columns in afforestation_export.csv
 */
object AcresalCSVMapping {

    /**
     * Intent pattern key to CSV column mapping
     * FIXED: Now uses exact column names from your CSV
     */
    private val intentKeyToCSVMapping = mapOf(
        // Basic identification fields
        "ActivityID" to "Component Code",  // Using Component Code as activity identifier
        "site_name" to "Site",
        "Implementer" to "Implementer",

        // Geographic fields - using available fields or defaults
        "stra_catchment" to "Strategic Catchment",
        "micro_catchment" to "Micro Catchment", // Added micro catchment
        "State" to "Status", // Using Status field as state equivalent
        "LGA" to "Site", // Fallback to Site since LGA not available

        // Intervention details
        "interv_type" to "Nature of Intervention",
        "component" to "Component",
        "Sub_compon" to "Sub Component",
        "gcat_intrv" to "Intervention Category",

        // Metrics
        "NO_Trees_P" to "Trees Planted",
        "area_restored" to "Area Restored (Ha)", // Added area field
        "percent_NDVI" to "Description", // Using Description as NDVI placeholder

        // Dates
        "start_date" to "Start Date",
        "end_date" to "End Date",

        // Additional fields
        "Comments" to "Terms of Reference", // Using TOR as comments
        "PDO_indica" to "PDO Indicator",
        "comp_indica" to "Component Indicator", // Added component indicator
        "_comp" to "Component Code"
    )

    /**
     * Special default values for certain fields
     * ENHANCED: Added more defaults for missing fields
     */
    val defaultValues = mapOf(
        "percent_NDVI" to "0",
        "State" to "Unknown", // Default for state
        "LGA" to "Unknown",   // Default for LGA
        "area_restored" to "0" // Default for area
    )

    /**
     * Column name normalization for flexible matching
     * Handles spaces, case differences, and special characters
     */
    private fun normalizeColumnName(columnName: String): String {
        return columnName.trim()
            .toLowerCase(Locale.ROOT)        // Convert to lowercase for comparison
            .replace(Regex("\\s+"), " ")     // Normalize multiple spaces to single space
            .replace("_", " ")               // Convert underscores to spaces
            .replace("-", " ")               // Convert hyphens to spaces
            .replace("[()]".toRegex(), "")   // Remove parentheses
    }

    /**
     * ENHANCED: Find CSV column with flexible matching
     * Handles variations in column naming (spaces, case, underscores)
     */
    fun findCSVColumn(targetColumn: String, availableColumns: Array<String>): String? {
        val normalizedTarget = normalizeColumnName(targetColumn)

        // First try exact normalized match (case-insensitive)
        availableColumns.forEach { column ->
            if (normalizeColumnName(column) == normalizedTarget) {
                return column // Return the actual CSV column name
            }
        }

        // Try partial matches (case-insensitive)
        availableColumns.forEach { column ->
            val normalizedColumn = normalizeColumnName(column)
            if (normalizedColumn.contains(normalizedTarget) ||
                normalizedTarget.contains(normalizedColumn)) {
                return column // Return the actual CSV column name
            }
        }

        return null
    }

    /**
     * Reverse mapping for looking up intent keys from CSV column names
     */
    val csvToIntentKeyMapping =
        intentKeyToCSVMapping.entries.associate { (intentKey, csvColumn) ->
            csvColumn to intentKey
        }

    /**
     * Get CSV column name for a given intent pattern key
     * ENHANCED: Now includes flexible column matching
     */
    fun getCSVColumnForIntentKey(intentKey: String): String? {
        return intentKeyToCSVMapping[intentKey]
    }

    /**
     * ENHANCED: Get CSV column with fallback matching
     */
    fun getCSVColumnForIntentKeyWithFallback(
        intentKey: String,
        availableColumns: Array<String>
    ): String? {
        // First try direct mapping
        val directColumn = intentKeyToCSVMapping[intentKey]
        if (directColumn != null) {
            // Check if this column exists in available columns
            val foundColumn = findCSVColumn(directColumn, availableColumns)
            if (foundColumn != null) {
                return foundColumn
            }
        }

        // Try alternative column names based on intent key
        val alternativeNames = getAlternativeColumnNames(intentKey)
        for (altName in alternativeNames) {
            val foundColumn = findCSVColumn(altName, availableColumns)
            if (foundColumn != null) {
                return foundColumn
            }
        }

        return null
    }

    /**
     * Get alternative column names for intent keys
     * Helps find columns even if naming differs from mapping
     */
    private fun getAlternativeColumnNames(intentKey: String): List<String> {
        return when (intentKey) {
            "ActivityID" -> listOf(
                "Component Code", "Activity ID", "activity_id", "ACTIVITY ID",
                "ActivityID", "activityID", "Code"
            )
            "site_name" -> listOf(
                "Site", "site", "SITE", "Site Name", "SITE NAME",
                "site_name", "SiteName", "Location"
            )
            "Implementer" -> listOf(
                "Implementer", "implementer", "IMPLEMENTER", "IMPLIMENTER",
                "Implementation Agency", "Agency"
            )
            "stra_catchment" -> listOf(
                "Strategic Catchment", "STRATEGIC CATCHMENT", "strategic catchment",
                "Strategic catchment", "strategic_catchment", "stra_catchment",
                "StrategicCatchment", "Strategic_Catchment", "Catchment", "Strategic"
            )
            "interv_type" -> listOf(
                "Nature of Intervention", "NATURE OF INTERVENTION", "nature of intervention",
                "Intervention Type", "INTERVENTION TYPE", "intervention type",
                "interv_type", "Type", "Nature"
            )
            "PDO_indica" -> listOf(
                "PDO Indicator", "PDO INDICATOR", "pdo indicator",
                "PDO_indica", "pdo_indicator", "PDO"
            )
            "component" -> listOf(
                "Component", "COMPONENT", "component",
                "Project Component", "Comp"
            )
            "percent_NDVI" -> listOf(
                "Description", "DESCRIPTION", "description",
                "NDVI", "percent_ndvi", "PERCENTAGE NDVI", "percentage ndvi"
            )
            "start_date" -> listOf(
                "Start Date", "START DATE", "start date", "start_date",
                "Begin Date", "Date Started"
            )
            "end_date" -> listOf(
                "End Date", "END DATE", "end date", "end_date",
                "Completion Date", "Date Ended"
            )
            "Comments" -> listOf(
                "Terms of Reference", "TERMS OF REFERENCE", "terms of reference",
                "Comments", "COMMENT", "comments", "comment", "Notes", "Description"
            )
            "NO_Trees_P" -> listOf(
                "Trees Planted", "TREES PLANTED", "trees planted",
                "Number of Trees", "NUMBER OF TREES", "number of trees",
                "NO_Trees_P", "Trees", "Tree Count"
            )
            "Sub_compon" -> listOf(
                "Sub Component", "SUB COMPONENT", "sub component",
                "Sub_compon", "SubComponent", "sub_component"
            )
            "gcat_intrv" -> listOf(
                "Intervention Category", "INTERVENTION CATEGORY", "intervention category",
                "General Cat Intervention", "GENERAL CAT INTERVENTION", "general cat intervention",
                "gcat_intrv", "Category"
            )
            "_comp" -> listOf(
                "Component Code", "COMPONENT CODE", "component code",
                "_comp", "Code", "Comp Code"
            )
            "State" -> listOf("State", "STATE", "state", "Status", "STATUS", "status", "Region")
            "LGA" -> listOf("LGA", "lga", "Local Government", "Local Area", "Site")
            "area_restored" -> listOf("Area Restored (Ha)", "AREA RESTORED (HA)", "area restored (ha)")
            "micro_catchment" -> listOf("Micro Catchment", "MICRO CATCHMENT", "micro catchment")
            else -> listOf(intentKey)
        }
    }

    /**
     * Get intent pattern key for a given CSV column name
     */
    fun getIntentKeyForCSVColumn(csvColumn: String): String? {
        return csvToIntentKeyMapping[csvColumn]
    }

    /**
     * Get value from CSV with default handling
     */
    fun getValueWithDefault(intentKey: String, csvValue: String?): String {
        // Handle null or "null" string values
        if (csvValue.isNullOrEmpty() || csvValue == "null") {
            // Check if there's a default value for this intent key
            return defaultValues[intentKey] ?: ""
        }
        return csvValue
    }

    /**
     * Validate if an intent pattern key exists in the mapping
     */
    fun isValidIntentKey(intentKey: String): Boolean {
        return intentKeyToCSVMapping.containsKey(intentKey)
    }

    /**
     * Get all intent pattern keys
     */
    fun getAllIntentKeys(): Set<String> {
        return intentKeyToCSVMapping.keys
    }

    /**
     * Get all CSV columns that are mapped
     */
    fun getAllMappedCSVColumns(): Set<String> {
        return intentKeyToCSVMapping.values.toSet()
    }

    /**
     * ENHANCED CSV value retrieval with flexible column matching
     */
    fun getCSVValueWithDefaults(
        csvDataProvider: CSVDataProvider,
        intentKey: String,
        rowIndex: Int = 0
    ): String {
        // Get available columns from CSV
        val availableColumns = csvDataProvider.getColumnNames()

        // Try to find the column with flexible matching
        val csvColumn = getCSVColumnForIntentKeyWithFallback(intentKey, availableColumns)

        if (csvColumn == null) {
            // Log available columns for debugging
            val availableStr = availableColumns.joinToString(", ")
            Timber.tag("AcresalCSVMapping")
                .w("No column found for intent key '$intentKey'. Available columns: $availableStr")
            return defaultValues[intentKey] ?: ""
        }

        // Get value from CSV
        val csvValue = csvDataProvider.getCSVValue(csvColumn, rowIndex)

        // Apply default value handling
        return getValueWithDefault(intentKey, csvValue)
    }

    /**
     * DEBUG: Get mapping diagnostics
     */
    fun getMappingDiagnostics(csvDataProvider: CSVDataProvider): String {
        val availableColumns = csvDataProvider.getColumnNames()
        val diagnostics = StringBuilder()

        diagnostics.append("=== CSV Mapping Diagnostics ===\n")
        diagnostics.append("Available CSV Columns: ${availableColumns.joinToString(", ")}\n\n")

        diagnostics.append("Intent Key -> CSV Column Mapping:\n")
        intentKeyToCSVMapping.forEach { (intentKey, expectedColumn) ->
            val foundColumn = getCSVColumnForIntentKeyWithFallback(intentKey, availableColumns)
            val status = if (foundColumn != null) "✓ FOUND" else "✗ MISSING"
            diagnostics.append("$intentKey -> $expectedColumn ($status: $foundColumn)\n")
        }

        return diagnostics.toString()
    }
    fun testSpecificMapping(csvDataProvider: CSVDataProvider) {
        Timber.d("=== Testing Specific Mappings ===")

        val testCases = listOf(
            "ActivityID", "stra_catchment", "site_name", "interv_type",
            "PDO_indica", "component", "Implementer", "NO_Trees_P"
        )

        testCases.forEach { intentKey ->
            val directMapping = getCSVColumnForIntentKey(intentKey)
            val fallbackMapping = getCSVColumnForIntentKeyWithFallback(intentKey, csvDataProvider.getColumnNames())
            val finalValue = getCSVValueWithDefaults(csvDataProvider, intentKey, 0)

            val status = if (finalValue.isNotEmpty() && finalValue != "Unknown") "✓" else "✗"
            Timber.d("$status $intentKey -> direct:'$directMapping' fallback:'$fallbackMapping' value:'$finalValue'")
        }
    }

}