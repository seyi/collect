package org.odk.collect.android.projects

import androidx.core.content.ContentProviderCompat.requireContext
import org.odk.collect.analytics.Analytics
import org.odk.collect.android.activities.ActivityUtils
import org.odk.collect.android.mainmenu.MainMenuActivity
import org.odk.collect.androidshared.ui.ToastUtils
import org.odk.collect.settings.importing.SettingsImportingResult
import javax.inject.Inject

class ACProjectCreation {

    @Inject
    lateinit var projectCreator: ProjectCreator
    @Inject
    lateinit var projectDataService: ProjectsDataService

    @Inject
    lateinit var settingsConnectionMatcher: SettingsConnectionMatcher

    @Inject
    lateinit var analytics: Analytics


    /**
     * Handles a new project or switch to an existing project
     */

    fun handleProjectCreation(settingsJson: String) {
        //Check if there is a matching project
        val matchingProjectUuid = settingsConnectionMatcher.getProjectWithMatchingConnection(settingsJson)


        if(matchingProjectUuid != null) {
            // A matching project was found, switch to it
            switchToProject(matchingProjectUuid)
        }
    }

    private fun switchToProject(matchingProjectUuid: String) {
        TODO("Yet to be implemented")
    }

    private fun projectCreatorHelper(settingsJson: String) : SettingsImportingResult {
        val pc: SettingsImportingResult = projectCreator.createNewProject(settingsJson)

        return if (pc == SettingsImportingResult.SUCCESS) {
            Analytics.log("Project creation successful {projectUuid: projectDataService.getCurrentProject().uuid")
            pc
        } else {
            Analytics.log("Project creation failed")
            pc

        }
    }


}