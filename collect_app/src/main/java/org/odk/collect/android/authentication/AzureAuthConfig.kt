package org.odk.collect.android.authentication

import android.content.Context

/**
 * Configuration helper for Azure Function App authentication
 */
object AzureAuthConfig {

    /**
     * Configure Azure Function URL for authentication
     * Call this during app initialization or from settings
     */
    fun configure(context: Context, azureFunctionUrl: String) {
        val authService = AzureAuthService(context)
        authService.setAzureFunctionUrl(azureFunctionUrl)
    }

    /**
     * Example Azure Function URLs for different environments
     */
    object Urls {
        // Production environment
        const val PRODUCTION = "https://your-app-prod.azurewebsites.net/api/login"

        // Staging environment
        const val STAGING = "https://your-app-staging.azurewebsites.net/api/login"

        // Development environment
        const val DEVELOPMENT = "https://your-app-dev.azurewebsites.net/api/login"

        // Local testing
        const val LOCAL = "http://localhost:7071/api/login"
    }
}
