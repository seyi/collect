package org.odk.collect.android.projects

import org.odk.collect.projects.Project
import org.odk.collect.projects.ProjectsRepository
import org.odk.collect.settings.ODKAppSettingsImporter
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.settings.importing.SettingsImportingResult
import org.odk.collect.utilities.CSVParser
import java.io.File

class ProjectCreator(
    private val projectsRepository: ProjectsRepository,
    private val projectsDataService: ProjectsDataService,
    private val settingsImporter: ODKAppSettingsImporter,
    private val settingsProvider: SettingsProvider,
    private val csvParser: CSVParser?
) {

    fun createNewProject(settingsJson: String, csvFile: File?): SettingsImportingResult {
        val savedProject = projectsRepository.save(Project.New("", "", ""))
        val settingsImportingResult = settingsImporter.fromJSON(settingsJson, savedProject)

        return if (settingsImportingResult == SettingsImportingResult.SUCCESS) {
            projectsDataService.setCurrentProject(savedProject.uuid)
            if (csvFile != null) {
                val prefilledData  = csvParser?.parse(csvFile)
                //storePrefilledData(savedProject.uuid, prefilledData)
            }
            settingsImportingResult
        } else {
            settingsProvider.getUnprotectedSettings(savedProject.uuid).clear()
            settingsProvider.getProtectedSettings(savedProject.uuid).clear()
            projectsRepository.delete(savedProject.uuid)
            settingsImportingResult
        }
    }

    private fun storePrefilledData(
        projectUuid: String,
        prefilledData: Map<String, String>) {
        val settings = settingsProvider.getUnprotectedSettings(projectUuid)
        settings.save("prefilled_data", prefilledData.toString())

    }
}
