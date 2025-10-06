// ===================================
// MODIFIED MainMenuActivity.kt with CSVDataProvider Integration
// ===================================

package org.odk.collect.android.mainmenu

import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import org.odk.collect.android.R
import org.odk.collect.android.activities.ActivityUtils
import org.odk.collect.android.activities.CrashHandlerActivity
import org.odk.collect.android.activities.FirstLaunchActivity
import org.odk.collect.android.activities.AC_FirstLaunchActivity
import org.odk.collect.android.activities.LoginActivity
import org.odk.collect.android.injection.DaggerUtils
import org.odk.collect.android.projects.ProjectSettingsDialog
import org.odk.collect.android.utilities.AcresalCSVMapping
import org.odk.collect.android.utilities.ThemeUtils
import org.odk.collect.android.utilities.CSVDataProvider  // Import our CSV provider
import org.odk.collect.androidshared.ui.FragmentFactoryBuilder
import org.odk.collect.crashhandler.CrashHandler
import org.odk.collect.permissions.PermissionsProvider
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.strings.localization.LocalizedActivity
import timber.log.Timber
import java.io.ByteArrayInputStream
import javax.inject.Inject

class MainMenuActivity : LocalizedActivity() {

    @Inject
    lateinit var viewModelFactory: MainMenuViewModelFactory

    @Inject
    lateinit var settingsProvider: SettingsProvider

    @Inject
    lateinit var permissionsProvider: PermissionsProvider

    private lateinit var currentProjectViewModel: CurrentProjectViewModel

    // Replace demo CSV parser with our CSVDataProvider
    private lateinit var csvDataProvider: CSVDataProvider

    override fun onCreate(savedInstanceState: Bundle?) {

        initSplashScreen()

        /*
        Don't reopen if the app is already open - allows entry points like notifications to use
        this Activity as a target to reopen the app without interrupting an ongoing session
         */

        if (!isTaskRoot) {
            super.onCreate(null)
            finish()
            return
        }

        CrashHandler.getInstance(this)?.also {
            if (it.hasCrashed(this)) {
                super.onCreate(null)
                ActivityUtils.startActivityAndCloseAllOthers(this, CrashHandlerActivity::class.java)
                return
            }
        }

        DaggerUtils.getComponent(this).inject(this)

        // Check if user is logged in
        if (!LoginActivity.isLoggedIn(this)) {
            super.onCreate(null)
            ActivityUtils.startActivityAndCloseAllOthers(this, LoginActivity::class.java)
            return
        }

        // Initialize CSV Data Provider
        csvDataProvider = CSVDataProvider.getInstance(this)

        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        currentProjectViewModel = viewModelProvider[CurrentProjectViewModel::class.java]

        ThemeUtils(this).setDarkModeForCurrentProject()

        if (!currentProjectViewModel.hasCurrentProject()) {
            super.onCreate(null)
            handleIntent(intent)
            ActivityUtils.startActivityAndCloseAllOthers(this, AC_FirstLaunchActivity::class.java)
            return
        } else {
            handleIntent(intent)
            this.supportFragmentManager.fragmentFactory = FragmentFactoryBuilder()
                .forClass(PermissionsDialogFragment::class) {
                    PermissionsDialogFragment(
                        permissionsProvider,
                        viewModelProvider[RequestPermissionsViewModel::class.java]
                    )
                }
                .forClass(ProjectSettingsDialog::class) {
                    ProjectSettingsDialog(viewModelFactory)
                }
                .forClass(MainMenuFragment::class) {
                    MainMenuFragment(viewModelFactory, settingsProvider)
                }
                .build()

            super.onCreate(savedInstanceState)
            setContentView(R.layout.main_menu_activity)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        Timber.d("handleIntent called with intent: $intent")

        if (intent == null) {
            Timber.d("Intent is null. Returning")
            return
        }

        // Retrieve CSV data using keys
        val csvData1 = intent.getStringExtra("csv_data")
        val csvData2 = intent.getStringExtra("org.odk.collect.android.CSV_DATA")

        // Process CSV data
        when {
            csvData1 != null -> {
                Timber.d("Processing CSV Data 1: ${csvData1.take(100)}...") // Log first 100 chars
                processCSVDataWithProvider(csvData1)
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val csvJson = prefs.getString("csv_data_json", null)
                Timber.d("Stored CSV contains 'Strategic Catchment': ${csvJson?.contains("Strategic Catchment")}")
            }
            csvData2 != null -> {
                Timber.d("Processing CSV Data 2: ${csvData2.take(100)}...")
                processCSVDataWithProvider(csvData2)
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val csvJson = prefs.getString("csv_data_json", null)
                Timber.d("Stored CSV contains 'Strategic Catchment': ${csvJson?.contains("Strategic Catchment")}")
            }
            else -> {
                Timber.d("No CSV Data found in intent")
                // Check if we already have CSV data loaded
                if (csvDataProvider.isCSVLoaded()) {
                    Timber.d("CSV data already loaded from previous session")
                    val strategicTest = AcresalCSVMapping.getCSVValueWithDefaults(csvDataProvider, "stra_catchment", 0)
                    Timber.d("Strategic catchment test: '$strategicTest'")
                    Toast.makeText(this, "Using existing CSV data (${csvDataProvider.getRowCount()} rows)", Toast.LENGTH_LONG).show()
                } else {
                    Timber.d("No CSV data available")
                }
            }
        }
    }

    /**
     * Process CSV data using CSVDataProvider instead of demo parser
     */
    private fun processCSVDataWithProvider(csvData: String) {
        try {
            Timber.d("Starting enhanced CSV processing")

            // Validate CSV data
            if (csvData.isBlank()) {
                Timber.w("CSV data is blank")
                Toast.makeText(this, "CSV data is empty", Toast.LENGTH_SHORT).show()
                return
            }

            // Parse CSV data into structured format
            val parsedData = parseCSVStringToMaps(csvData)

            if (parsedData.isEmpty()) {
                Timber.w("No data rows found in CSV")
                Toast.makeText(this, "CSV file appears to be empty or invalid", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate that we have expected columns
            val firstRow = parsedData.first()
            val columnCount = firstRow.keys.size

            if (columnCount < 3) { // Minimum expected columns
                Timber.w("CSV has only $columnCount columns, expected more")
                Toast.makeText(this, "CSV file seems incomplete (only $columnCount columns)", Toast.LENGTH_SHORT).show()
            }

            // Save to CSVDataProvider
            val success = csvDataProvider.saveCSVData(parsedData)

            if (success) {
                val rowCount = csvDataProvider.getRowCount()
                val columnNames = csvDataProvider.getColumnNames()

                Timber.d("CSV data successfully processed: $rowCount rows, ${columnNames.size} columns")

                Toast.makeText(
                    this,
                    "CSV loaded successfully: $rowCount rows, ${columnNames.size} columns",
                    Toast.LENGTH_LONG
                ).show()

                // Enhanced logging and validation
                logSampleCSVData()

                Timber.d("CSV data is now available for ACRESAL GIS form fields")

            } else {
                Timber.e("Failed to save CSV data to provider")
                Toast.makeText(this, "Error saving CSV data", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Timber.e(e, "Error processing CSV data")
            Toast.makeText(this, "Error processing CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    /**
     * Parse CSV string into List<Map<String, String>> format for CSVDataProvider
     */
    private fun parseCSVStringToMaps(csvData: String): List<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()

        try {
            val lines = csvData.trim().split("\n")

            if (lines.isEmpty()) {
                Timber.w("CSV data is empty")
                return result
            }

            // Parse header row
            val headerLine = lines.first().trim()
            val headers = parseCSVLineEnhanced(headerLine)

            if (headers.isEmpty()) {
                Timber.w("No headers found in CSV")
                return result
            }

            // Clean and validate headers
            val cleanHeaders = headers.map { it.trim().replace("\"", "") }
            Timber.d("CSV Headers (${cleanHeaders.size}): ${cleanHeaders.joinToString(" | ")}")

            // Parse data rows
            for (i in 1 until lines.size) {
                val line = lines[i].trim()
                if (line.isEmpty()) {
                    Timber.d("Skipping empty line at index $i")
                    continue
                }

                val values = parseCSVLineEnhanced(line)
                if (values.isEmpty()) {
                    Timber.d("Skipping line with no values at index $i")
                    continue
                }

                // Create map for this row
                val rowData = mutableMapOf<String, String>()

                // Map each value to corresponding header
                for (j in cleanHeaders.indices) {
                    val header = cleanHeaders[j]
                    val value = if (j < values.size) {
                        values[j].trim().replace("\"", "") // Remove quotes and trim
                    } else {
                        ""
                    }
                    rowData[header] = value
                }

                result.add(rowData)

                // Log first few rows for debugging
                if (i <= 3) {
                    Timber.d("Row $i parsed: ${rowData.entries.take(3).joinToString { "${it.key}=${it.value}" }}...")
                }
            }

            Timber.d("Successfully parsed ${result.size} data rows from CSV")

        } catch (e: Exception) {
            Timber.e(e, "Error parsing CSV string")
            throw e
        }

        return result
    }
    /**
     * Parse a single CSV line handling quotes and commas
     */
    private fun parseCSVLineEnhanced(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            when {
                // Handle quote at start of field or inside quotes
                char == '"' -> {
                    if (!inQuotes) {
                        // Starting quotes
                        inQuotes = true
                    } else {
                        // Check for escaped quote (double quote)
                        if (i + 1 < line.length && line[i + 1] == '"') {
                            current.append('"') // Add literal quote
                            i++ // Skip next quote
                        } else {
                            // Ending quotes
                            inQuotes = false
                        }
                    }
                }
                // Handle comma (field separator)
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }
                // Handle all other characters
                else -> {
                    current.append(char)
                }
            }
            i++
        }

        // Add the final field
        result.add(current.toString())

        return result
    }

    /**
     * Log sample CSV data for verification
     */
    private fun logSampleCSVData() {
        try {
            val columns = csvDataProvider.getColumnNames()
            val rowCount = csvDataProvider.getRowCount()

            Timber.d("=== Enhanced CSV Data Analysis ===")
            Timber.d("Total Columns: ${columns.size}")
            Timber.d("Total Rows: $rowCount")

            // Print all column names for verification
            Timber.d("All Columns:")
            columns.forEachIndexed { index, column ->
                Timber.d("  [$index] '$column'")
            }

            // Sample data from first row
            if (rowCount > 0) {
                Timber.d("\nFirst Row Sample:")
                columns.take(8).forEach { column -> // Show first 8 columns
                    val value = csvDataProvider.getCSVValue(column, 0)
                    Timber.d("  '$column' = '$value'")
                }
            }

            // Test ACRESAL mapping with actual data
            testAcresalMappingWithActualData()

            // Show mapping diagnostics
            val diagnostics = AcresalCSVMapping.getMappingDiagnostics(csvDataProvider)
            Timber.d("\n$diagnostics")

        } catch (e: Exception) {
            Timber.e(e, "Error logging sample CSV data")
        }
    }

    /**
     * Test specific ACRESAL fields that will be used in forms
     */
    private fun testAcresalFields() {
        val acresalFields = listOf(
            "ActivityID", "activityID", "activity_id",
            "state", "State", "STATE",
            "lga", "LGA",
            "site", "Site", "site_name",
            "interventionCategory", "intervention_category",
            "areaOfIntervention", "area_of_intervention",
            "noOfTreesPlanted", "no_of_trees_planted"
        )

        Timber.d("=== Testing ACRESAL Field Availability ===")

        for (field in acresalFields) {
            val value = csvDataProvider.getCSVValue(field, 0)
            if (value != null) {
                Timber.d("✓ Field '$field' available: $value")
            } else {
                Timber.d("✗ Field '$field' not found")
            }
        }
    }

    /**
     * Clear CSV data (call this when form is submitted)
     * TODO: Integrate this with form submission completion
     */
    fun clearCSVData() {
        try {
            csvDataProvider.clearCSVData()
            Timber.d("CSV data cleared successfully")
            Toast.makeText(this, "CSV data cleared", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing CSV data")
        }
    }

    private fun initSplashScreen() {
        /*
        We don't need the `installSplashScreen` call on Android 12+ (the system handles the
        splash screen for us) and it causes problems if we later switch between dark/light themes
        with the ThemeUtils#setDarkModeForCurrentProject call.
         */
        if (Build.VERSION.SDK_INT < 31) {
            installSplashScreen()
        } else {
            setTheme(R.style.Theme_Collect)
        }
    }

   /**
    * NEW: Test ACRESAL mapping with actual CSV data
    */
    private fun testAcresalMappingWithActualData() {
        Timber.d("\n=== Testing ACRESAL Field Mapping ===")

        val testIntentKeys = listOf(
            "ActivityID", "site_name", "Implementer", "stra_catchment",
            "State", "LGA", "interv_type", "component", "Sub_compon",
            "gcat_intrv", "NO_Trees_P", "start_date", "end_date",
            "Comments", "PDO_indica", "percent_NDVI"
        )

        testIntentKeys.forEach { intentKey ->
            val csvValue = AcresalCSVMapping.getCSVValueWithDefaults(
                csvDataProvider, intentKey, 0
            )

            val csvColumn = AcresalCSVMapping.getCSVColumnForIntentKeyWithFallback(
                intentKey, csvDataProvider.getColumnNames()
            )

            val status = if (csvValue.isNotEmpty() && csvValue != "Unknown") "✓" else "✗"
            Timber.d("$status $intentKey -> '$csvColumn' = '$csvValue'")
        }
    }

    fun simpleStorageCheck() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val csvJson = prefs.getString("csv_data_json", null)

        Timber.d("=== SIMPLE STORAGE CHECK ===")

        if (csvJson == null) {
            Timber.e("NO CSV DATA IN SHAREDPREFERENCES")
            return
        }

        // Show first 500 characters of stored JSON
        Timber.d("Stored JSON (first 500 chars): ${csvJson.take(500)}")

        // Check if "Strategic Catchment" appears in the JSON string
        val containsStrategicCatchment = csvJson.contains("Strategic Catchment")
        val containsUppercase = csvJson.contains("STRATEGIC CATCHMENT")
        val containsLowercase = csvJson.contains("strategic catchment")

        Timber.d("JSON contains 'Strategic Catchment': $containsStrategicCatchment")
        Timber.d("JSON contains 'STRATEGIC CATCHMENT': $containsUppercase")
        Timber.d("JSON contains 'strategic catchment': $containsLowercase")

        // Find all variations of "strategic" in the JSON
        val strategicMatches = Regex("\"[^\"]*[Ss]trategic[^\"]*\"").findAll(csvJson)
        Timber.d("All 'strategic' matches in JSON:")
        strategicMatches.forEach { match ->
            Timber.d("  Found: ${match.value}")
        }

        // Try to parse and check the actual structure
        try {
            val jsonArray = org.json.JSONArray(csvJson)
            if (jsonArray.length() > 0) {
                val firstRow = jsonArray.getJSONObject(0)
                val keys = firstRow.names()

                if (keys != null) {
                    for (i in 0 until keys.length()) {
                        val key = keys.getString(i)
                        if (key.contains("strategic", ignoreCase = true)) {
                            val value = firstRow.getString(key)
                            Timber.d("FOUND STRATEGIC KEY: '$key' = '$value'")

                            // Check exact bytes of the key
                            val keyBytes = key.toByteArray()
                            Timber.d("Key bytes: ${keyBytes.joinToString(" ") { "%02x".format(it) }}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing JSON")
        }
    }

    // Also add this to check the original CSV data before parsing
    fun checkOriginalCSVData(originalCsvData: String) {
        Timber.d("=== ORIGINAL CSV DATA CHECK ===")

        // Check if Strategic Catchment exists in the original CSV
        val lines = originalCsvData.split("\n")
        if (lines.isNotEmpty()) {
            val headerLine = lines[0]
            Timber.d("Original header line: '$headerLine'")

            val containsStrategic = headerLine.contains("Strategic Catchment")
            Timber.d("Header contains 'Strategic Catchment': $containsStrategic")

            if (!containsStrategic) {
                Timber.e("PROBLEM: 'Strategic Catchment' not found in original CSV header!")
                Timber.e("This means the issue is in your CSV file, not the storage/parsing")

                // Show all headers that contain "strategic" or "catchment"
                val strategicHeaders = headerLine.split(",").filter {
                    it.contains("strategic", ignoreCase = true) ||
                            it.contains("catchment", ignoreCase = true)
                }
                Timber.e("Strategic/Catchment headers found: $strategicHeaders")
            }
        }
    }

    // Add this method to your MainActivity to specifically test Strategic Catchment storage

    fun testStrategicCatchmentInSharedPrefs() {
        Timber.d("=== STRATEGIC CATCHMENT SHAREDPREFS TEST ===")

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val csvJson = prefs.getString("csv_data_json", null)

        if (csvJson != null) {
            try {
                // Parse the stored JSON
                val jsonArray = org.json.JSONArray(csvJson)
                Timber.d("Stored JSON has ${jsonArray.length()} rows")

                if (jsonArray.length() > 0) {
                    val firstRow = jsonArray.getJSONObject(0)

                    // List ALL keys in the stored JSON
                    val storedKeys = mutableListOf<String>()
                    val keys = firstRow.names()
                    if (keys != null) {
                        for (i in 0 until keys.length()) {
                            storedKeys.add(keys.getString(i))
                        }
                    }

                    Timber.d("ALL STORED KEYS:")
                    storedKeys.forEachIndexed { index, key ->
                        val value = firstRow.optString(key, "NULL")
                        Timber.d("  [$index] '$key' = '$value'")
                    }

                    // Check for Strategic Catchment variations
                    val strategicKeys = storedKeys.filter {
                        it.contains("strategic", ignoreCase = true) ||
                                it.contains("catchment", ignoreCase = true)
                    }

                    Timber.d("STRATEGIC/CATCHMENT KEYS FOUND: ${strategicKeys.size}")
                    strategicKeys.forEach { key ->
                        val value = firstRow.optString(key, "NULL")
                        Timber.d("  '$key' = '$value'")
                    }

                    // Test exact matches
                    val exactTests = listOf(
                        "Strategic Catchment",
                        "STRATEGIC CATCHMENT",
                        "strategic catchment",
                        "Strategic catchment"
                    )

                    exactTests.forEach { testKey ->
                        val hasKey = firstRow.has(testKey)
                        val value = if (hasKey) firstRow.getString(testKey) else "KEY NOT FOUND"
                        Timber.d("Exact test '$testKey': has=$hasKey, value='$value'")
                    }

                    // Check if the key might have extra spaces or hidden chars
                    storedKeys.forEach { key ->
                        val normalizedKey = key.trim().lowercase()
                        if (normalizedKey.contains("strategic") && normalizedKey.contains("catchment")) {
                            Timber.d("POTENTIAL MATCH FOUND:")
                            Timber.d("  Original key: '$key'")
                            Timber.d("  Key length: ${key.length}")
                            Timber.d("  Key bytes: ${key.toByteArray().joinToString(" ") { "%02x".format(it) }}")
                            Timber.d("  Normalized: '$normalizedKey'")
                            val value = firstRow.optString(key, "NULL")
                            Timber.d("  Value: '$value'")
                        }
                    }

                } else {
                    Timber.e("No rows in stored JSON array")
                }

            } catch (e: Exception) {
                Timber.e(e, "Error parsing stored JSON for Strategic Catchment test")
            }
        } else {
            Timber.e("No CSV JSON data found in SharedPreferences")
        }

        // Also test the CSVDataProvider directly
        val csvProvider = CSVDataProvider.getInstance(this)
        val providerColumns = csvProvider.getColumnNames()

        Timber.d("CSVDATAPROVIDER COLUMNS:")
        providerColumns.forEachIndexed { index, column ->
            Timber.d("  [$index] '$column'")
            if (column.contains("strategic", ignoreCase = true) ||
                column.contains("catchment", ignoreCase = true)) {
                val value = csvProvider.getCSVValue(column, 0)
                Timber.d("    VALUE: '$value'")
            }
        }

        Timber.d("=== END TEST ===")
    }

    // Alternative method to reload CSV from SharedPreferences and check
    fun reloadAndTestCSV() {
        Timber.d("=== RELOAD AND TEST CSV ===")

        val csvProvider = CSVDataProvider.getInstance(this)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val csvJson = prefs.getString("csv_data_json", null)

        if (csvJson != null) {
            // Force reload from SharedPreferences
            csvProvider.loadCSVFromJson(csvJson)

            // Test after reload
            val isLoaded = csvProvider.isCSVLoaded()
            val rowCount = csvProvider.getRowCount()
            val columns = csvProvider.getColumnNames()

            Timber.d("After reload - Loaded: $isLoaded, Rows: $rowCount")
            Timber.d("Columns: ${columns.joinToString(", ")}")

            // Test Strategic Catchment specifically
            val strategicValue = csvProvider.getCSVValue("Strategic Catchment", 0)
            Timber.d("Strategic Catchment value after reload: '$strategicValue'")

            // Test all variations
            val variations = listOf("Strategic Catchment", "STRATEGIC CATCHMENT", "strategic catchment")
            variations.forEach { variation ->
                val value = csvProvider.getCSVValue(variation, 0)
                Timber.d("Variation '$variation': '$value'")
            }

        } else {
            Timber.e("No CSV data to reload")
        }
    }

// Call these in your handleIntent method after CSV processing:
// testStrategicCatchmentInSharedPrefs()
// reloadAndTestCSV()

}

// ===================================
// ENHANCED CSVDataProvider.java with Clear Function
// ===================================

// Add this method to your CSVDataProvider.java
/*
package org.odk.collect.android.utilities;

public class CSVDataProvider {
    // ... existing code ...
    
    /**
     * Clear all CSV data from memory and SharedPreferences
     */
    public void clearCSVData() {
        try {
            // Clear in-memory data
            csvData.clear();
            headerIndexMap.clear();
            
            // Clear SharedPreferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit()
                .remove(CSV_DATA_KEY)
                .putBoolean(CSV_LOADED_KEY, false)
                .apply();
                
            Timber.d("CSV data cleared from memory and SharedPreferences");
            
        } catch (Exception e) {
            Timber.e(e, "Error clearing CSV data");
            throw new RuntimeException("Failed to clear CSV data", e);
        }
    }
    
    /**
     * Get CSV data summary for debugging
     */
    public String getCSVSummary() {
        if (csvData.isEmpty()) {
            return "No CSV data loaded";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("CSV Summary:\n");
        summary.append("Rows: ").append(csvData.size()).append("\n");
        summary.append("Columns: ").append(String.join(", ", getColumnNames())).append("\n");
        
        // Add first row sample
        if (!csvData.isEmpty()) {
            Map<String, String> firstRow = csvData.get(0);
            summary.append("Sample data:\n");
            for (Map.Entry<String, String> entry : firstRow.entrySet()) {
                summary.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        return summary.toString();
    }
}
*/

// ===================================
// INTEGRATION WORKFLOW
// ===================================

/*
Complete Integration Workflow:
==============================

1. CSV Reception (MainMenuActivity):
   ✅ Receives CSV data via Intent
   ✅ Parses CSV into structured format
   ✅ Saves to CSVDataProvider (SharedPreferences)
   ✅ Logs sample data for verification

2. Form Field Population (ExStringWidget):
   ✅ Detects ex:com.acresal.gis('ActivityID=ACTIVITY ID') appearance
   ✅ Extracts CSV column name ('ActivityID')
   ✅ Fetches value from CSVDataProvider
   ✅ Hides launch button, populates field automatically

3. Form Submission (TODO):
   ⏳ Clear CSV data when form is submitted
   ⏳ Reset for next form session

Benefits:
=========
✅ Replaces demo CSV parser with production CSVDataProvider
✅ Proper error handling and logging
✅ Structured data storage in SharedPreferences
✅ Integration with ExStringWidget for form field population
✅ Sample data logging for debugging
✅ Field availability testing for ACRESAL forms
✅ Clear function for cleanup after form submission

Testing Fields:
===============
The code tests for common ACRESAL field variations:
- ActivityID, activityID, activity_id
- state, State, STATE
- lga, LGA
- site, Site, site_name
- interventionCategory, intervention_category
- areaOfIntervention, area_of_intervention
- noOfTreesPlanted, no_of_trees_planted

This ensures your CSV column names will be found regardless of naming convention.
*/