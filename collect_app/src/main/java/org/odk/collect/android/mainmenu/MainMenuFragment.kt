package org.odk.collect.android.mainmenu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.odk.collect.android.activities.ActivityUtils
import org.odk.collect.android.activities.DeleteFormsActivity
import org.odk.collect.android.activities.FormDownloadListActivity
import org.odk.collect.android.activities.InstanceChooserList
import org.odk.collect.android.activities.LoginActivity
import org.odk.collect.android.application.MapboxClassInstanceCreator
import org.odk.collect.android.databinding.MainMenuBinding
import org.odk.collect.android.formlists.blankformlist.BlankFormListActivity
import org.odk.collect.android.formmanagement.FormFillingIntentFactory
import org.odk.collect.android.geofencing.GeoFenceManager
import org.odk.collect.android.geofencing.GeofenceFormHelper
import org.odk.collect.android.geofencing.GeofenceType
import org.odk.collect.android.instancemanagement.send.InstanceUploaderListActivity
import org.odk.collect.android.projects.ProjectIconView
import org.odk.collect.android.projects.ProjectSettingsDialog
import org.odk.collect.android.utilities.ActionRegister
import org.odk.collect.android.utilities.ApplicationConstants
import org.odk.collect.androidshared.data.consume
import org.odk.collect.androidshared.ui.DialogFragmentUtils
import org.odk.collect.androidshared.ui.SnackbarUtils
import org.odk.collect.androidshared.ui.multiclicksafe.MultiClickGuard
import org.odk.collect.maps.MapPoint
import org.odk.collect.permissions.PermissionsProvider
import org.odk.collect.projects.Project
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.strings.R.string
import org.odk.collect.webpage.WebViewActivity
import timber.log.Timber

class MainMenuFragment(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val settingsProvider: SettingsProvider,
    private val permissionsProvider: PermissionsProvider
) : Fragment(), LocationListener {

    private lateinit var mainMenuViewModel: MainMenuViewModel
    private lateinit var currentProjectViewModel: CurrentProjectViewModel
    private lateinit var permissionsViewModel: RequestPermissionsViewModel

    private val formEntryFlowLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data
            mainMenuViewModel.setSavedForm(uri)
        }

    private var locationManager: LocationManager? = null
    private var geofenceStatusMenuItem: MenuItem? = null
    private var geofenceStatusTextView: android.widget.TextView? = null
    private var currentLocation: Location? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val viewModelProvider = ViewModelProvider(requireActivity(), viewModelFactory)
        mainMenuViewModel = viewModelProvider[MainMenuViewModel::class.java]
        currentProjectViewModel = viewModelProvider[CurrentProjectViewModel::class.java]
        permissionsViewModel = viewModelProvider[RequestPermissionsViewModel::class.java]

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MainMenuBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentProjectViewModel.currentProject.observe(viewLifecycleOwner) { (_, name): Project.Saved ->
            requireActivity().invalidateOptionsMenu()
           // requireActivity().title =name
            requireActivity().title = "" // Remove title to save toolbar space
        }

        val binding = MainMenuBinding.bind(view)
        initToolbar(binding)
        initMapbox()
        initButtons(binding)
        initAppName(binding)

        if (permissionsViewModel.shouldAskForPermissions()) {
            DialogFragmentUtils.showIfNotShowing(
                PermissionsDialogFragment::class.java,
                this.parentFragmentManager
            )
        }

        mainMenuViewModel.savedForm.consume(viewLifecycleOwner) { value ->
            SnackbarUtils.showLongSnackbar(
                requireView(),
                getString(value.message),
                action = value.action?.let { action ->
                    SnackbarUtils.Action(getString(action)) {
                        formEntryFlowLauncher.launch(
                            FormFillingIntentFactory.editInstanceIntent(
                                requireContext(),
                                value.uri
                            )
                        )
                    }
                },
                displayDismissButton = true
            )
        }
    }

    override fun onResume() {
        super.onResume()

        currentProjectViewModel.refresh()
        mainMenuViewModel.refreshInstances()

        val binding = MainMenuBinding.bind(requireView())
        setButtonsVisibility(binding)
        manageGoogleDriveDeprecationBanner(binding)

        // Start location tracking for geofence status
        startLocationTracking()
    }

    override fun onPause() {
        super.onPause()
        // Stop location tracking to save battery
        stopLocationTracking()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val projectsMenuItem = menu.findItem(org.odk.collect.android.R.id.projects)

        (projectsMenuItem.actionView as ProjectIconView).apply {
            project = currentProjectViewModel.currentProject.value
          //  setOnClickListener { onOptionsItemSelected(projectsMenuItem) }
            contentDescription = getString(string.projects)
        }

        // Save reference to geofence status menu item and custom action view
        geofenceStatusMenuItem = menu.findItem(org.odk.collect.android.R.id.geofence_status)
        geofenceStatusMenuItem?.actionView?.let { actionView ->
            geofenceStatusTextView = actionView.findViewById(org.odk.collect.android.R.id.geofence_status_text)
        }
        updateGeofenceStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(org.odk.collect.android.R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!MultiClickGuard.allowClick(javaClass.name)) {
            return true
        }
        when (item.itemId) {
            org.odk.collect.android.R.id.projects -> {
                DialogFragmentUtils.showIfNotShowing(
                    ProjectSettingsDialog::class.java,
                    parentFragmentManager
                )
                return true
            }
            org.odk.collect.android.R.id.logout -> {
                performLogout()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun performLogout() {
        // Logout the user
        LoginActivity.logout(requireContext())

        // Navigate to login screen
        ActivityUtils.startActivityAndCloseAllOthers(requireActivity(), LoginActivity::class.java)
    }

    private fun initToolbar(binding: MainMenuBinding) {
        val toolbar = binding.root.findViewById<Toolbar>(org.odk.collect.androidshared.R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun initMapbox() {
        if (MapboxClassInstanceCreator.isMapboxAvailable()) {
            childFragmentManager
                .beginTransaction()
                .add(
                    org.odk.collect.android.R.id.map_box_initialization_fragment,
                    MapboxClassInstanceCreator.createMapBoxInitializationFragment()!!
                )
                .commit()
        }
    }

    private fun initButtons(binding: MainMenuBinding) {
        binding.enterData.setOnClickListener {
            ActionRegister.actionDetected()

            // DEVELOPMENT: Show location dialog before opening form
            // Dialog will launch form list after user clicks OK
            showDevelopmentLocationDialog {
                // Launch form list after dialog is dismissed
                formEntryFlowLauncher.launch(
                    Intent(requireActivity(), BlankFormListActivity::class.java)
                )
            }
        }

        binding.reviewData.setOnClickListener {
            formEntryFlowLauncher.launch(
                Intent(requireActivity(), InstanceChooserList::class.java).apply {
                    putExtra(
                        ApplicationConstants.BundleKeys.FORM_MODE,
                        ApplicationConstants.FormModes.EDIT_SAVED
                    )
                }
            )
        }

        binding.sendData.setOnClickListener {
            formEntryFlowLauncher.launch(
                Intent(
                    requireActivity(),
                    InstanceUploaderListActivity::class.java
                )
            )
        }

        binding.viewSentForms.setOnClickListener {
            startActivity(
                Intent(requireActivity(), InstanceChooserList::class.java).apply {
                    putExtra(
                        ApplicationConstants.BundleKeys.FORM_MODE,
                        ApplicationConstants.FormModes.VIEW_SENT
                    )
                }
            )
        }

        binding.getForms.setOnClickListener {
            val intent = Intent(requireContext(), FormDownloadListActivity::class.java)
            startActivity(intent)
        }

        binding.manageForms.setOnClickListener {
            startActivity(Intent(requireContext(), DeleteFormsActivity::class.java))
        }

        mainMenuViewModel.sendableInstancesCount.observe(viewLifecycleOwner) { finalized: Int ->
            binding.sendData.setNumberOfForms(finalized)
        }
        mainMenuViewModel.editableInstancesCount.observe(viewLifecycleOwner) { unsent: Int ->
            binding.reviewData.setNumberOfForms(unsent)
        }
        mainMenuViewModel.sentInstancesCount.observe(viewLifecycleOwner) { sent: Int ->
            binding.viewSentForms.setNumberOfForms(sent)
        }
    }

    private fun initAppName(binding: MainMenuBinding) {
//        binding.appName.text = String.format(
//            "%s %s",
//            getString(string.collect_app_name),
//            mainMenuViewModel.version
//        )

        binding.appName.text = String.format(
            getString(string.collect_app_name),
        )


        val versionSHA = mainMenuViewModel.versionCommitDescription
        if (versionSHA != null) {
           // binding.versionSha.text = versionSHA
            binding.versionSha.text  = "1.0.1"
        } else {
            binding.versionSha.visibility = View.GONE
        }
    }

    private fun setButtonsVisibility(binding: MainMenuBinding) {
        binding.reviewData.visibility =
            if (mainMenuViewModel.shouldEditSavedFormButtonBeVisible()) View.VISIBLE else View.GONE
        binding.sendData.visibility =
            if (mainMenuViewModel.shouldSendFinalizedFormButtonBeVisible()) View.VISIBLE else View.GONE
        binding.viewSentForms.visibility =
            if (mainMenuViewModel.shouldViewSentFormButtonBeVisible()) View.VISIBLE else View.GONE
        binding.getForms.visibility =
            if (mainMenuViewModel.shouldGetBlankFormButtonBeVisible()) View.VISIBLE else View.GONE
        binding.manageForms.visibility =
            if (mainMenuViewModel.shouldDeleteSavedFormButtonBeVisible()) View.VISIBLE else View.GONE
    }

    private fun manageGoogleDriveDeprecationBanner(binding: MainMenuBinding) {
        if (currentProjectViewModel.currentProject.value.isOldGoogleDriveProject) {
            binding.googleDriveDeprecationBanner.root.visibility = View.VISIBLE
            binding.googleDriveDeprecationBanner.learnMoreButton.setOnClickListener {
                val intent = Intent(requireContext(), WebViewActivity::class.java)
                intent.putExtra("url", "https://forum.getodk.org/t/40097")
                startActivity(intent)
            }
        } else {
            binding.googleDriveDeprecationBanner.root.visibility = View.GONE
        }
    }

    // Location tracking for geofence status
    private fun startLocationTracking() {
        try {
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Check location permission
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.d("Location permission not granted, requesting permissions")
                requestLocationPermissionsForGeofencing()
                return
            }

            // Load geofences for all Nigerian states
            lifecycleScope.launch {
                try {
                    val geoFenceManager = GeoFenceManager.getInstance(requireContext())

                    // Load geofences for all 20 states
                    val states = listOf(
                        "Adamawa", "Bauchi", "Benue", "Borno", "Fct", "Gombe",
                        "Jigawa", "Kaduna", "Kano", "Katsina", "Kebbi", "Kogi",
                        "Kwara", "Nasarawa", "Niger", "Plateau", "Sokoto",
                        "Taraba", "Yobe", "Zamfara"
                    )

                    states.forEach { state ->
                        geoFenceManager.loadGeofences(state)
                        Timber.d("Loaded geofences for $state")
                    }

                    Timber.i("All geofences loaded successfully")
                } catch (e: Exception) {
                    Timber.e(e, "Error loading geofences: ${e.message}")
                }
            }

            // Request location updates every 10 seconds
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000L, // 10 seconds
                10f, // 10 meters
                this
            )

            // Try to get last known location immediately
            val lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                onLocationChanged(lastLocation)
            }

            Timber.d("Location tracking started for geofence status")
        } catch (e: Exception) {
            Timber.e(e, "Error starting location tracking: ${e.message}")
        }
    }

    private fun stopLocationTracking() {
        try {
            locationManager?.removeUpdates(this)
            Timber.d("Location tracking stopped")
        } catch (e: Exception) {
            Timber.e(e, "Error stopping location tracking: ${e.message}")
        }
    }

    private fun requestLocationPermissionsForGeofencing() {
        permissionsProvider.requestEnabledLocationPermissions(
            requireActivity(),
            object : org.odk.collect.permissions.PermissionListener {
                override fun granted() {
                    Timber.i("Location permissions granted, starting geofence tracking")
                    startLocationTracking()
                }

                override fun denied() {
                    Timber.w("Location permissions denied, geofence tracking disabled")
                }
            }
        )
    }

    // LocationListener implementation
    override fun onLocationChanged(location: Location) {
        currentLocation = location
        Timber.d("Location changed: ${location.latitude}, ${location.longitude}")
        updateGeofenceStatus()
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Not used
    }

    override fun onProviderEnabled(provider: String) {
        Timber.d("Location provider enabled: $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Timber.d("Location provider disabled: $provider")
    }

    private fun updateGeofenceStatus() {
        val location = currentLocation
        val textView = geofenceStatusTextView

        if (textView == null) {
            return
        }

        if (location == null) {
            textView.text = "Location: Unknown"
            return
        }

        // Query geofences in background
        lifecycleScope.launch {
            try {
                val geoFenceManager = GeoFenceManager.getInstance(requireContext())
                val point = MapPoint(location.latitude, location.longitude)

                Timber.d("GPS Location: lat=${location.latitude}, lon=${location.longitude}")
                Timber.d("MapPoint created: lat=${point.latitude}, lon=${point.longitude}")

                val cacheStats = geoFenceManager.getCacheStats()
                Timber.d("Cache stats: ${cacheStats.totalPolygons} polygons, ${cacheStats.loadedStates.size} states")

                // Check if geofences are loaded
                if (cacheStats.totalPolygons == 0) {
                    Timber.w("Geofences not loaded yet, skipping validation")
                    textView.text = "Loading geofences..."
                    return@launch
                }

                // Get containing polygons
                val polygons = geoFenceManager.getContainingPolygons(point)

                Timber.d("Found ${polygons.size} containing polygons")
                polygons.forEach { polygon ->
                    Timber.d("  - ${polygon.name} (${polygon.type}) in ${polygon.state}")
                }

                if (polygons.isEmpty()) {
                    textView.text = "Outside"
                } else {
                    // Find state and catchment
                    val state = polygons.find { it.type == GeofenceType.STATE }
                    val catchment = polygons.find { it.type == GeofenceType.STRATEGIC_CATCHMENT }

                    val statusText = buildString {
                        if (state != null) {
                            append(state.name)
                        }
                        if (catchment != null) {
                            if (isNotEmpty()) append(" | ")
                            append(catchment.name)
                        }
                    }

                    // Display format: "Kaduna" or "Kaduna | Hadejia"
                    textView.text = if (statusText.isEmpty()) {
                        "Inside"
                    } else {
                        statusText
                    }
                }

                Timber.d("Geofence status updated: ${textView.text}")
            } catch (e: Exception) {
                Timber.e(e, "Error updating geofence status: ${e.message}")
                textView.text = "Error"
            }
        }
    }

    /**
     * DEVELOPMENT ONLY: Show location dialog with GPS coordinates and detected boundaries
     * TODO: Remove this method in production build or wrap with BuildConfig.DEBUG
     *
     * @param onDismiss Callback to execute after dialog is dismissed
     */
    private fun showDevelopmentLocationDialog(onDismiss: () -> Unit) {
        // Check if geofences are loaded
        val geoFenceManager = GeoFenceManager.getInstance(requireContext())
        val userState = LoginActivity.getUserState(requireContext())

        if (userState.isNotEmpty() && !geoFenceManager.isStateLoaded(userState)) {
            // Geofences not loaded yet - show warning
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Geofence Data Not Loaded")
                .setMessage("Boundary data for $userState is not loaded yet.\n\nPlease wait a moment and try again, or restart the app.")
                .setPositiveButton("OK") { _, _ -> onDismiss() }
                .setCancelable(false)
                .show()
            return
        }

        val location = currentLocation
        if (location == null) {
            // No GPS available
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("⚠️ No GPS Location")
                .setMessage("GPS location is not available.\n\nPlease ensure:\n• Location services are enabled\n• App has location permission\n• You are outdoors with clear sky view")
                .setPositiveButton("OK") { _, _ ->
                    onDismiss()  // Call callback when OK is clicked
                }
                .setCancelable(false)  // Prevent dismissing by tapping outside
                .show()
            return
        }

        // Query geofences in background
        lifecycleScope.launch {
            try {
                val geoFenceManager = GeoFenceManager.getInstance(requireContext())
                val point = MapPoint(location.latitude, location.longitude)

                // Get containing polygons
                val polygons = geoFenceManager.getContainingPolygons(point)

                // Get field values
                val fieldValues = GeofenceFormHelper.autoPopulateLocationFieldsBlocking(
                    requireContext(),
                    point
                )

                // Build dialog message
                val message = buildString {
                    append("📍 GPS Coordinates:\n")
                    append("Latitude: ${String.format("%.6f", location.latitude)}\n")
                    append("Longitude: ${String.format("%.6f", location.longitude)}\n")
                    append("Accuracy: ${String.format("%.1f", location.accuracy)} meters\n")
                    append("\n")

                    append("🗺️ Detected Boundaries:\n")
                    append("\n")

                    if (fieldValues.isWithinBoundaries) {
                        append("State:\n")
                        append("  ${fieldValues.state ?: "Not detected"}\n\n")

                        append("LGA:\n")
                        append("  ${fieldValues.lga ?: "Not detected"}\n\n")

                        append("Strategic Catchment:\n")
                        append("  ${fieldValues.strategicCatchment ?: "Not detected"}\n\n")

                        append("Micro Catchment:\n")
                        append("  ${fieldValues.microCatchment ?: "Not detected"}\n")
                    } else {
                        append("❌ Location is outside all mapped boundaries\n")
                        append("\n")
                        append("Error: ${fieldValues.errorMessage ?: "Unknown error"}")
                    }

                    append("\n\n")
                    append("📊 Total polygons found: ${polygons.size}")
                }

                // Show dialog on UI thread
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("🌍 Geofence Location Info")
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ ->
                        onDismiss()  // Call callback when OK is clicked
                    }
                    .setNeutralButton("Copy Coordinates") { dialog, _ ->
                        // Copy coordinates to clipboard
                        val coords = String.format("%.6f, %.6f",
                            location.latitude, location.longitude)

                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                            as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("GPS Coordinates", coords)
                        clipboard.setPrimaryClip(clip)

                        Toast.makeText(requireContext(),
                            "Coordinates copied: $coords",
                            Toast.LENGTH_SHORT).show()

                        // Don't dismiss dialog - user might want to review info again
                    }
                    .setCancelable(false)  // Prevent dismissing by tapping outside
                    .show()

                Timber.i("Development location dialog shown: lat=${location.latitude}, lon=${location.longitude}")

            } catch (e: Exception) {
                Timber.e(e, "Error showing development location dialog")
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("❌ Error")
                    .setMessage("Error getting location information:\n\n${e.message}")
                    .setPositiveButton("OK") { _, _ ->
                        onDismiss()  // Call callback even on error
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}
