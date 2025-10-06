package org.odk.collect.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.odk.collect.analytics.Analytics
import org.odk.collect.android.analytics.AnalyticsEvents
import org.odk.collect.android.configure.qr.AppConfigurationGenerator
import org.odk.collect.android.databinding.FirstLaunchLayoutBinding
import org.odk.collect.android.injection.DaggerUtils
import org.odk.collect.android.mainmenu.MainMenuActivity
import org.odk.collect.android.projects.DuplicateProjectConfirmationDialog
import org.odk.collect.android.projects.DuplicateProjectConfirmationKeys.MATCHING_PROJECT
import org.odk.collect.android.projects.DuplicateProjectConfirmationKeys.SETTINGS_JSON
import org.odk.collect.android.projects.ManualProjectCreatorDialog
import org.odk.collect.android.projects.ProjectCreator
import org.odk.collect.android.projects.ProjectsDataService
import org.odk.collect.android.projects.QrCodeProjectCreatorDialog
import org.odk.collect.android.projects.SettingsConnectionMatcher
import org.odk.collect.android.version.VersionInformation
import org.odk.collect.androidshared.system.ContextUtils.getThemeAttributeValue
import org.odk.collect.androidshared.ui.DialogFragmentUtils
import org.odk.collect.androidshared.ui.ToastUtils
import org.odk.collect.androidshared.utils.Validator
import org.odk.collect.async.Scheduler
import org.odk.collect.material.MaterialProgressDialogFragment
import org.odk.collect.projects.Project
import org.odk.collect.projects.ProjectsRepository
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.settings.importing.SettingsImportingResult
import org.odk.collect.strings.localization.LocalizedActivity
import javax.inject.Inject

class AC_FirstLaunchActivity : LocalizedActivity() {

    @Inject
    lateinit var projectsRepository: ProjectsRepository

    @Inject
    lateinit var versionInformation: VersionInformation

    @Inject
    lateinit var projectsDataService: ProjectsDataService

    @Inject
    lateinit var projectCreator: ProjectCreator

    @Inject
    lateinit var settingsProvider: SettingsProvider

    @Inject
    lateinit var scheduler: Scheduler
    @Inject
    lateinit var appConfigurationGenerator: AppConfigurationGenerator

    lateinit var settingsConnectionMatcher: SettingsConnectionMatcher

    private val viewModel: FirstLaunchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FirstLaunchViewModel(scheduler, projectsRepository, projectsDataService) as T
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerUtils.getComponent(this).inject(this)

        // Auto-configure project without showing UI
        // Skip the configuration screen entirely
        settingsConnectionMatcher = SettingsConnectionMatcher(projectsRepository, settingsProvider)

        // Automatically create project with hardcoded credentials
        autoConfigureProject(
            url = "https://kf.kobotoolbox.org",
            username = "anointedgeek",
            password = "P+@Z?sr+jc52PU3"
        )
    }

    private fun autoConfigureProject(url: String, username: String, password: String) {
        // Validate URL
        if (!Validator.isUrlValid(url)) {
            ToastUtils.showShortToast(this, org.odk.collect.strings.R.string.url_error)
            finish()
            return
        }

        // Generate configuration JSON
        val settingsJson = appConfigurationGenerator.getAppConfigurationAsJsonWithServerDetails(
            url,
            username,
            password
        )

        // Check if project with same configuration already exists
        settingsConnectionMatcher.getProjectWithMatchingConnection(settingsJson)?.let { uuid ->
            // Project already exists, just navigate to main menu
            ActivityUtils.startActivityAndCloseAllOthers(this, MainMenuActivity::class.java)
        } ?: run {
            // Create new project
            val result = projectCreator.createNewProject(settingsJson, csvFile = null)

            if (result == SettingsImportingResult.SUCCESS) {
                Analytics.log("Project auto-configured successfully")
                ActivityUtils.startActivityAndCloseAllOthers(this, MainMenuActivity::class.java)
            } else {
                Analytics.log("Project auto-configuration failed")
                ToastUtils.showShortToast(this, "Failed to configure project")
                finish()
            }
        }
    }

}

internal class FirstLaunchViewModel(
    private val scheduler: Scheduler,
    private val projectsRepository: ProjectsRepository,
    private val projectsDataService: ProjectsDataService
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun tryDemo() {
        Analytics.log(AnalyticsEvents.TRY_DEMO)

        _isLoading.value = true
        scheduler.immediate(
            background = {
                projectsRepository.save(Project.DEMO_PROJECT)
                projectsDataService.setCurrentProject(Project.DEMO_PROJECT_ID)
            },
            foreground = {
                _isLoading.value = false
            }
        )
    }
}
