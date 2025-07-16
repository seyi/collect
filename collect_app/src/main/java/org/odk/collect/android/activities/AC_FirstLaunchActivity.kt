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

        FirstLaunchLayoutBinding.inflate(layoutInflater).apply {
            setContentView(this.root)

            MaterialProgressDialogFragment.showOn(
                this@AC_FirstLaunchActivity,
                viewModel.isLoading,
                supportFragmentManager
            ) {
                MaterialProgressDialogFragment().also { dialog ->
                    dialog.message = getString(org.odk.collect.strings.R.string.loading)
                }
            }

            viewModel.isLoading.observe(this@AC_FirstLaunchActivity) { isLoading ->
                if (!isLoading) {
                    ActivityUtils.startActivityAndCloseAllOthers(
                        this@AC_FirstLaunchActivity,
                        MainMenuActivity::class.java
                    )
                }
            }

//            configureViaQrButton.setOnClickListener {
//                DialogFragmentUtils.showIfNotShowing(
//                    QrCodeProjectCreatorDialog::class.java,
//                    supportFragmentManager
//                )
//            }

//            configureManuallyButton.setOnClickListener {
//                DialogFragmentUtils.showIfNotShowing(
//                    ManualProjectCreatorDialog::class.java,
//                    supportFragmentManager
//                )
//            }

//            appName.text = String.format(
//                "%s %s",
//                getString(org.odk.collect.strings.R.string.collect_app_name),
//                versionInformation.versionToDisplay
//            )
            appName.text = String.format("ACReSAL Collect 1.0.1")

//            dontHaveServer.apply {
//                text = SpannableStringBuilder()
//                    .append(getString(org.odk.collect.strings.R.string.dont_have_project))
//                    .append(" ")
//                    .color(getThemeAttributeValue(context, com.google.android.material.R.attr.colorAccent)) {
//                        append(getString(org.odk.collect.strings.R.string.try_demo))
//                    }
//
//                setOnClickListener {
//                    viewModel.tryDemo()
//                }
//            }
        }
        //viewModel.tryDemo()

        settingsConnectionMatcher = SettingsConnectionMatcher(projectsRepository, settingsProvider)

        handleAddingNewProject("https://kf.kobotoolbox.org",
            "anointedgeek",
            "P+@Z?sr+jc52PU3")
    }

    private fun handleAddingNewProject(url: String, userName: String, password: String) {
        if (!Validator.isUrlValid(url)) {
            ToastUtils.showShortToast(this@AC_FirstLaunchActivity, org.odk.collect.strings.R.string.url_error)
        } else {
            val settingsJson = appConfigurationGenerator.getAppConfigurationAsJsonWithServerDetails(
                url,
                userName,
                password
            )

            settingsConnectionMatcher.getProjectWithMatchingConnection(settingsJson)?.let { uuid ->
                val intent = Intent(this@AC_FirstLaunchActivity, MainMenuActivity::class.java).apply {
                    putExtra(SETTINGS_JSON, settingsJson)
                    putExtra(MATCHING_PROJECT, uuid)

                }

            } ?: run {
                projectCreatorHelper(settingsJson)
                Analytics.log(AnalyticsEvents.MANUAL_CREATE_PROJECT)
            }
        }
    }

    private fun projectCreatorHelper(settingsJson: String) : SettingsImportingResult {
        val pc: SettingsImportingResult = projectCreator.createNewProject(settingsJson)
        ActivityUtils.startActivityAndCloseAllOthers(this@AC_FirstLaunchActivity, MainMenuActivity::class.java)

        return if (pc == SettingsImportingResult.SUCCESS) {
            Analytics.log("Project creation successful {projectUuid: projectDataService.getCurrentProject().uuid")
            pc
        } else {
            Analytics.log("Project creation failed")
            pc

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
