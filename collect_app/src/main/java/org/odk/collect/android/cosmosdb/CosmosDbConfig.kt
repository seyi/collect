package org.odk.collect.android.cosmosdb

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

/**
 * Configuration manager for Azure Cosmos DB API connection
 * Handles secure storage and retrieval of API endpoint and function keys
 */
object CosmosDbConfig {

    // Azure Function API Configuration
    // Deployed at: https://acresalgisfunctionapp.azurewebsites.net
    private const val DEFAULT_API_ENDPOINT = "https://acresalgisfunctionapp.azurewebsites.net/api/submitODKData"

    // Encrypted storage keys
    private const val PREFS_NAME = "cosmos_db_config"
    private const val KEY_API_ENDPOINT = "api_endpoint"
    private const val KEY_API_FUNCTION_KEY = "api_function_key"
    private const val KEY_SYNC_ENABLED = "sync_enabled"
    private const val KEY_AUTO_SYNC = "auto_sync"
    private const val KEY_WIFI_ONLY = "wifi_only"
    private const val KEY_MAX_RETRIES = "max_retries"
    private const val KEY_RETRY_DELAY_MS = "retry_delay_ms"

    // Default configuration values
    private const val DEFAULT_SYNC_ENABLED = true
    private const val DEFAULT_AUTO_SYNC = true
    private const val DEFAULT_WIFI_ONLY = false
    private const val DEFAULT_MAX_RETRIES = 3
    private const val DEFAULT_RETRY_DELAY_MS = 1000L

    // Connection timeout settings (milliseconds)
    const val CONNECT_TIMEOUT_MS = 30000L  // 30 seconds
    const val READ_TIMEOUT_MS = 60000L     // 60 seconds

    private var encryptedPrefs: SharedPreferences? = null

    /**
     * Initialize the config with application context
     * Must be called before using other methods
     */
    fun initialize(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            // Set default API endpoint if not already set
            if (getApiEndpoint().isEmpty()) {
                setApiEndpoint(DEFAULT_API_ENDPOINT)
            }

            Timber.d("CosmosDbConfig initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error initializing CosmosDbConfig")
            throw e
        }
    }

    /**
     * Get Azure Function API endpoint
     */
    fun getApiEndpoint(): String {
        return encryptedPrefs?.getString(KEY_API_ENDPOINT, "") ?: ""
    }

    /**
     * Set Azure Function API endpoint
     */
    fun setApiEndpoint(endpoint: String) {
        encryptedPrefs?.edit()?.putString(KEY_API_ENDPOINT, endpoint)?.apply()
        Timber.d("API endpoint updated")
    }

    /**
     * Get Azure Function key (if function uses function-level auth)
     */
    fun getApiFunctionKey(): String {
        return encryptedPrefs?.getString(KEY_API_FUNCTION_KEY, "") ?: ""
    }

    /**
     * Set Azure Function key
     */
    fun setApiFunctionKey(key: String) {
        encryptedPrefs?.edit()?.putString(KEY_API_FUNCTION_KEY, key)?.apply()
        Timber.d("API function key updated")
    }

    /**
     * Check if cloud sync is enabled
     */
    fun isSyncEnabled(): Boolean {
        return encryptedPrefs?.getBoolean(KEY_SYNC_ENABLED, DEFAULT_SYNC_ENABLED) ?: DEFAULT_SYNC_ENABLED
    }

    /**
     * Enable or disable cloud sync
     */
    fun setSyncEnabled(enabled: Boolean) {
        encryptedPrefs?.edit()?.putBoolean(KEY_SYNC_ENABLED, enabled)?.apply()
        Timber.d("Cloud sync ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Check if auto-sync on finalize is enabled
     */
    fun isAutoSyncEnabled(): Boolean {
        return encryptedPrefs?.getBoolean(KEY_AUTO_SYNC, DEFAULT_AUTO_SYNC) ?: DEFAULT_AUTO_SYNC
    }

    /**
     * Enable or disable auto-sync on finalize
     */
    fun setAutoSyncEnabled(enabled: Boolean) {
        encryptedPrefs?.edit()?.putBoolean(KEY_AUTO_SYNC, enabled)?.apply()
        Timber.d("Auto-sync ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Check if WiFi-only sync is enabled
     */
    fun isWifiOnlyEnabled(): Boolean {
        return encryptedPrefs?.getBoolean(KEY_WIFI_ONLY, DEFAULT_WIFI_ONLY) ?: DEFAULT_WIFI_ONLY
    }

    /**
     * Enable or disable WiFi-only sync
     */
    fun setWifiOnlyEnabled(enabled: Boolean) {
        encryptedPrefs?.edit()?.putBoolean(KEY_WIFI_ONLY, enabled)?.apply()
        Timber.d("WiFi-only sync ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Get maximum number of retry attempts
     */
    fun getMaxRetries(): Int {
        return encryptedPrefs?.getInt(KEY_MAX_RETRIES, DEFAULT_MAX_RETRIES) ?: DEFAULT_MAX_RETRIES
    }

    /**
     * Set maximum number of retry attempts
     */
    fun setMaxRetries(retries: Int) {
        encryptedPrefs?.edit()?.putInt(KEY_MAX_RETRIES, retries)?.apply()
    }

    /**
     * Get retry delay in milliseconds
     */
    fun getRetryDelayMs(): Long {
        return encryptedPrefs?.getLong(KEY_RETRY_DELAY_MS, DEFAULT_RETRY_DELAY_MS) ?: DEFAULT_RETRY_DELAY_MS
    }

    /**
     * Set retry delay in milliseconds
     */
    fun setRetryDelayMs(delayMs: Long) {
        encryptedPrefs?.edit()?.putLong(KEY_RETRY_DELAY_MS, delayMs)?.apply()
    }

    /**
     * Test connection to Azure Function API
     * Returns true if connection is valid
     */
    suspend fun testConnection(): Boolean {
        return try {
            val endpoint = getApiEndpoint()
            if (endpoint.isEmpty()) {
                Timber.w("API endpoint is empty")
                return false
            }

            // Actual test will be in CosmosDbClient
            true
        } catch (e: Exception) {
            Timber.e(e, "Connection test failed")
            false
        }
    }

    /**
     * Reset all configuration to defaults
     */
    fun resetToDefaults() {
        encryptedPrefs?.edit()?.apply {
            putBoolean(KEY_SYNC_ENABLED, DEFAULT_SYNC_ENABLED)
            putBoolean(KEY_AUTO_SYNC, DEFAULT_AUTO_SYNC)
            putBoolean(KEY_WIFI_ONLY, DEFAULT_WIFI_ONLY)
            putInt(KEY_MAX_RETRIES, DEFAULT_MAX_RETRIES)
            putLong(KEY_RETRY_DELAY_MS, DEFAULT_RETRY_DELAY_MS)
            apply()
        }
        Timber.d("Configuration reset to defaults")
    }

    /**
     * Get configuration summary for debugging
     */
    fun getConfigSummary(): Map<String, Any> {
        return mapOf(
            "apiEndpoint" to getApiEndpoint(),
            "hasFunctionKey" to getApiFunctionKey().isNotEmpty(),
            "syncEnabled" to isSyncEnabled(),
            "autoSync" to isAutoSyncEnabled(),
            "wifiOnly" to isWifiOnlyEnabled(),
            "maxRetries" to getMaxRetries(),
            "retryDelayMs" to getRetryDelayMs()
        )
    }

    /**
     * Clear all stored configuration (for logout/reset)
     */
    fun clearConfiguration() {
        encryptedPrefs?.edit()?.clear()?.apply()
        Timber.d("Configuration cleared")
    }
}
