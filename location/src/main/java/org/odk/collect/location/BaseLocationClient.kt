package org.odk.collect.location

import android.location.LocationManager
import org.odk.collect.location.LocationClient.LocationClientListener

/**
 * An abstract base LocationClient class that provides some shared functionality for determining
 * whether or not certain Location providers are available.
 *
 * Constructs a new BaseLocationClient with the provided LocationManager.
 * This Constructor is only accessible to child classes.
 *
 * @param locationManager The LocationManager to retrieve locations from.
 */
abstract class BaseLocationClient(protected val locationManager: LocationManager?) :
    LocationClient {

    private var listener: LocationClientListener? = null
    private var priority = LocationClient.Priority.PRIORITY_HIGH_ACCURACY

    override fun isLocationAvailable(): Boolean {
        return getProvider() != null
    }

    protected fun getProvider(): String? {
        var provider = LocationManager.PASSIVE_PROVIDER
        var backupProvider: String? = null

        when (priority) {
            LocationClient.Priority.PRIORITY_HIGH_ACCURACY -> {
                provider = LocationManager.GPS_PROVIDER
                backupProvider = LocationManager.NETWORK_PROVIDER
            }
            LocationClient.Priority.PRIORITY_BALANCED_POWER_ACCURACY -> {
                provider = LocationManager.NETWORK_PROVIDER
                backupProvider = LocationManager.GPS_PROVIDER
            }
            LocationClient.Priority.PRIORITY_LOW_POWER -> {
                provider = LocationManager.NETWORK_PROVIDER
                backupProvider = LocationManager.PASSIVE_PROVIDER
            }
            LocationClient.Priority.PRIORITY_NO_POWER -> {
                provider = LocationManager.PASSIVE_PROVIDER
                backupProvider = null
            }
        }

        val selectedProvider = getProviderIfEnabled(provider, backupProvider)
        timber.log.Timber.d("GPS Provider selection - Requested: $provider, Backup: $backupProvider, Selected: $selectedProvider")

        return selectedProvider
    }

    private fun getProviderIfEnabled(provider: String, backupProvider: String?): String? {
        if (hasProvider(provider)) {
            return provider
        } else if (hasProvider(backupProvider)) {
            return backupProvider
        }

        // Fallback: Try to find ANY provider (for FakeGPS compatibility)
        val allProviders = locationManager?.getAllProviders() ?: emptyList()
        timber.log.Timber.w("Preferred providers not enabled. All available providers: ${allProviders.joinToString()}")

        // Try GPS first (even if disabled, FakeGPS might work)
        if (LocationManager.GPS_PROVIDER in allProviders) {
            timber.log.Timber.i("Using GPS_PROVIDER as fallback (may work with FakeGPS)")
            return LocationManager.GPS_PROVIDER
        }

        // Then try network
        if (LocationManager.NETWORK_PROVIDER in allProviders) {
            timber.log.Timber.i("Using NETWORK_PROVIDER as fallback")
            return LocationManager.NETWORK_PROVIDER
        }

        // Last resort: passive
        if (LocationManager.PASSIVE_PROVIDER in allProviders) {
            timber.log.Timber.i("Using PASSIVE_PROVIDER as fallback")
            return LocationManager.PASSIVE_PROVIDER
        }

        return null
    }

    private fun hasProvider(provider: String?): Boolean {
        if (provider == null) {
            return false
        }

        val enabledProviders = locationManager!!.getProviders(true)
        timber.log.Timber.d("Checking provider '$provider' - Enabled providers: ${enabledProviders.joinToString()}")

        for (enabledProvider in enabledProviders) {
            if (enabledProvider.equals(provider, ignoreCase = true)) {
                timber.log.Timber.d("Provider '$provider' is enabled")
                return true
            }
        }

        timber.log.Timber.w("Provider '$provider' is NOT enabled")
        return false
    }

    override fun setPriority(priority: LocationClient.Priority) {
        this.priority = priority
    }

    protected fun getPriority(): LocationClient.Priority {
        return priority
    }

    override fun setListener(locationClientListener: LocationClientListener?) {
        listener = locationClientListener
    }

    fun getListener(): LocationClientListener? {
        return listener
    }
}
