package org.odk.collect.android.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.odk.collect.android.R
import org.odk.collect.android.geofencing.GeoFenceManager
import org.odk.collect.android.geofencing.GeofenceFormHelper
import org.odk.collect.android.geofencing.GeofenceType
import org.odk.collect.maps.MapPoint
import timber.log.Timber

/**
 * Test Activity for manually testing geofencing functionality
 * Allows entering coordinates and seeing what location names are detected
 */
class GeofenceTestActivity : AppCompatActivity() {

    private lateinit var latitudeInput: EditText
    private lateinit var longitudeInput: EditText
    private lateinit var stateInput: EditText
    private lateinit var testButton: Button
    private lateinit var loadAllButton: Button
    private lateinit var resultsText: TextView
    private lateinit var geoFenceManager: GeoFenceManager

    // Sample test coordinates for quick testing
    private val sampleCoordinates = mapOf(
        "Kaduna City" to Pair(10.5105, 7.4165),
        "Kaduna Hadejia (Strategic)" to Pair(10.519886, 8.775070),
        "Kano City" to Pair(12.0022, 8.5919),
        "Abuja FCT" to Pair(9.0765, 7.3986),
        "Bauchi City" to Pair(10.3158, 9.8442),
        "Outside Nigeria" to Pair(0.0, 0.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofence_test)

        // Initialize views
        latitudeInput = findViewById(R.id.latitude_input)
        longitudeInput = findViewById(R.id.longitude_input)
        stateInput = findViewById(R.id.state_input)
        testButton = findViewById(R.id.test_button)
        loadAllButton = findViewById(R.id.load_all_button)
        resultsText = findViewById(R.id.results_text)

        // Initialize manager
        geoFenceManager = GeoFenceManager.getInstance(this)

        // Set up button listeners
        testButton.setOnClickListener {
            testCoordinates()
        }

        loadAllButton.setOnClickListener {
            loadAllStates()
        }

        // Set default values (Kaduna city center)
        latitudeInput.setText("10.5105")
        longitudeInput.setText("7.4165")
        stateInput.setText("Kaduna")

        // Add sample coordinate buttons
        addSampleButtons()
    }

    private fun addSampleButtons() {
        // You can add buttons for each sample coordinate here
        // For simplicity, showing in results initially
        val samplesText = StringBuilder("Sample Coordinates:\n\n")
        sampleCoordinates.forEach { (name, coords) ->
            samplesText.append("$name: ${coords.first}, ${coords.second}\n")
        }
        resultsText.text = samplesText.toString()
    }

    private fun testCoordinates() {
        val latStr = latitudeInput.text.toString()
        val lonStr = longitudeInput.text.toString()
        val state = stateInput.text.toString()

        if (latStr.isEmpty() || lonStr.isEmpty()) {
            resultsText.text = "Error: Please enter latitude and longitude"
            return
        }

        try {
            val latitude = latStr.toDouble()
            val longitude = lonStr.toDouble()
            val point = MapPoint(latitude, longitude)

            resultsText.text = "Testing coordinates: $latitude, $longitude\nLoading..."

            lifecycleScope.launch {
                // Load geofences for specified state
                if (state.isNotEmpty()) {
                    resultsText.text = "Loading $state geofences..."
                    val loaded = withContext(Dispatchers.IO) {
                        geoFenceManager.loadGeofences(state)
                    }

                    if (!loaded) {
                        resultsText.text = "Error: Failed to load $state geofences"
                        return@launch
                    }
                }

                // Test containment
                val results = withContext(Dispatchers.IO) {
                    testPoint(point)
                }

                // Display results
                resultsText.text = results
            }

        } catch (e: NumberFormatException) {
            resultsText.text = "Error: Invalid coordinate format\n${e.message}"
            Timber.e(e, "Invalid coordinates")
        } catch (e: Exception) {
            resultsText.text = "Error: ${e.message}"
            Timber.e(e, "Error testing coordinates")
        }
    }

    private fun loadAllStates() {
        resultsText.text = "Loading all states..."

        lifecycleScope.launch {
            val states = listOf(
                "Adamawa", "Bauchi", "Benue", "Borno", "FCT", "Gombe",
                "Jigawa", "Kaduna", "Kano", "Katsina", "Kebbi", "Kogi",
                "Kwara", "Nasarawa", "Niger", "Plateau", "Sokoto",
                "Taraba", "Yobe", "Zamfara"
            )

            val results = StringBuilder("Loading All States:\n\n")
            var successCount = 0

            for (state in states) {
                results.append("$state: ")
                try {
                    val loaded = withContext(Dispatchers.IO) {
                        geoFenceManager.loadGeofences(state)
                    }

                    if (loaded) {
                        // Get all polygon types for this state
                        val strategicCount = geoFenceManager.getPolygonsForState(state, GeofenceType.STRATEGIC_CATCHMENT).size
                        val microCount = geoFenceManager.getPolygonsForState(state, GeofenceType.MICRO_CATCHMENT).size
                        val totalCount = strategicCount + microCount
                        results.append("✓ $totalCount polygons (SC:$strategicCount, MC:$microCount)\n")
                        successCount++
                    } else {
                        results.append("✗ Failed\n")
                    }
                } catch (e: Exception) {
                    results.append("✗ Error: ${e.message}\n")
                    Timber.e(e, "Error loading $state")
                }

                // Update UI periodically
                resultsText.text = results.toString()
            }

            results.append("\n✅ Loaded $successCount/${states.size} states successfully")
            resultsText.text = results.toString()
        }
    }

    private suspend fun testPoint(point: MapPoint): String {
        val results = StringBuilder()
        results.append("📍 Testing: ${point.latitude}, ${point.longitude}\n\n")

        // Get all containing polygons
        val polygons = withContext(Dispatchers.IO) {
            geoFenceManager.getContainingPolygons(point)
        }

        if (polygons.isEmpty()) {
            results.append("❌ No boundaries found at this location\n")
            results.append("Point is outside all loaded geofences\n")
            return results.toString()
        }

        results.append("✅ Found ${polygons.size} containing polygon(s):\n\n")

        // Group by type
        val byType = polygons.groupBy { it.type }

        // State
        byType[GeofenceType.STATE]?.let { states ->
            results.append("🏛️ STATE:\n")
            states.forEach { polygon ->
                results.append("  • ${polygon.name}\n")
                results.append("    ID: ${polygon.id}\n")
            }
            results.append("\n")
        }

        // LGA
        byType[GeofenceType.LGA]?.let { lgas ->
            results.append("🏘️ LOCAL GOVERNMENT AREA:\n")
            lgas.forEach { polygon ->
                results.append("  • ${polygon.name}\n")
                results.append("    ID: ${polygon.id}\n")
            }
            results.append("\n")
        }

        // Strategic Catchment
        byType[GeofenceType.STRATEGIC_CATCHMENT]?.let { catchments ->
            results.append("🌊 STRATEGIC CATCHMENT:\n")
            catchments.forEach { polygon ->
                results.append("  • ${polygon.name}\n")
                results.append("    ID: ${polygon.id}\n")
                results.append("    State: ${polygon.state}\n")
            }
            results.append("\n")
        }

        // Micro Catchment
        byType[GeofenceType.MICRO_CATCHMENT]?.let { catchments ->
            results.append("💧 MICRO CATCHMENT:\n")
            catchments.forEach { polygon ->
                results.append("  • ${polygon.name}\n")
                results.append("    ID: ${polygon.id}\n")
                results.append("    State: ${polygon.state}\n")
            }
            results.append("\n")
        }

        // Test form helper auto-populate
        results.append("─────────────────────────\n")
        results.append("FORM AUTO-POPULATE TEST:\n\n")

        val fieldValues = withContext(Dispatchers.IO) {
            GeofenceFormHelper.autoPopulateLocationFieldsBlocking(this@GeofenceTestActivity, point)
        }

        if (fieldValues.isWithinBoundaries) {
            results.append("✅ Within Boundaries\n\n")
            results.append("Form Fields:\n")
            fieldValues.state?.let { results.append("  State: $it\n") }
            fieldValues.lga?.let { results.append("  LGA: $it\n") }
            fieldValues.strategicCatchment?.let { results.append("  Strategic Catchment: $it\n") }
            fieldValues.microCatchment?.let { results.append("  Micro Catchment: $it\n") }
        } else {
            results.append("❌ Outside Boundaries\n")
            fieldValues.errorMessage?.let { results.append("  Error: $it\n") }
        }

        return results.toString()
    }
}
