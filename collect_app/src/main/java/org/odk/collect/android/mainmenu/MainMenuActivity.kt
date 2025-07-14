package org.odk.collect.android.mainmenu

import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import org.odk.collect.android.R
import org.odk.collect.android.activities.ActivityUtils
import org.odk.collect.android.activities.CrashHandlerActivity
import org.odk.collect.android.activities.FirstLaunchActivity
import org.odk.collect.android.activities.AC_FirstLaunchActivity
import org.odk.collect.android.injection.DaggerUtils
import org.odk.collect.android.projects.ProjectSettingsDialog
import org.odk.collect.android.utilities.ThemeUtils
import org.odk.collect.androidshared.ui.FragmentFactoryBuilder
import org.odk.collect.crashhandler.CrashHandler
import org.odk.collect.permissions.PermissionsProvider
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.strings.localization.LocalizedActivity
import org.odk.collect.utilities.CSVParser
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
    private val csvParser = CSVParser()

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


        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        currentProjectViewModel = viewModelProvider[CurrentProjectViewModel::class.java]

        ThemeUtils(this).setDarkModeForCurrentProject()

        if (!currentProjectViewModel.hasCurrentProject()) {
            super.onCreate(null)

           // ActivityUtils.startActivityAndCloseAllOthers(this, FirstLaunchActivity::class.java)
            ActivityUtils.startActivityAndCloseAllOthers(this, AC_FirstLaunchActivity::class.java)

                handleIntent(intent)
            return
        } else {
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
            //Handle initial intent


        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //Handle new intent if Activity is already open
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        Timber.d("handleIntent called with intent: $intent")
        Toast.makeText(this, "handleIntent called with intent: $intent", Toast.LENGTH_SHORT).show()
       if (intent == null) {
           Timber.d("Intent is null.  returning")
           Toast.makeText(this, "Intent is null.  returning", Toast.LENGTH_SHORT).show()
           return
       }




        //Retrieve csv data using keys
        val csvData1 = intent.getStringExtra("csv_data")
        val csvData2 = intent.getStringExtra("org.odk.collect.android.CSV_DATA")
        Toast.makeText(this, "CSV Data 1: $csvData1", Toast.LENGTH_SHORT).show()

        //Lod debug the info that csv data was retrieved for dat1 and data2, and process csv data

        // First check if csvdat1 is not null
        when {
            csvData1 != null -> {
                //Timber.tag("MainMenuActivity").d("CSV Data 1: $csvData1")
                Timber.d("Processing CSV Data 1: $csvData1")
                processCSVData(csvData1)
            }
            csvData2 != null -> {
                //Timber.tag("MainMenuActivity").d("CSV Data 1: $csvData2")
                Timber.d("Processing CSV Data 1: $csvData2")
                processCSVData(csvData2)
            }
            else -> {
                Timber.tag("MainMenuActivity").d("No CSV Data found")
            }
        }
    }

    private fun processCSVData(csvData: String) {
        //Split CSV content into rows

        val rows  = csvData.split("\n")
        for (row in rows) {
            Timber.tag("MainMenuActivity").d("CSV Receiver, Row: $row")
            //Split each row into columns
            val columns = row.split(",")
            Timber.tag("MainMenuActivity").d("CSV Receiver, Columns: $columns")
        }



        try {
            //Convert to Input Stream
            val instr = ByteArrayInputStream(csvData.toByteArray())
            //Timber.tag("MainMenuActivity").d("CSV Receiver, Input Stream: $instr")

            //Parsing CSV headers
            val parsedData = CSVParser().parseCsvWithHeaders(instr)
            Timber.tag("MainMenuActivity").d("CSV Receiver, Parsed Data: $parsedData")

        } catch(e: Exception) {
            Timber.tag("MainMenuActivity").d("Error parsing CSV, Data: ${e.message}")
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
}
