package org.odk.collect.android.mainmenu

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import org.odk.collect.permissions.PermissionsChecker
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.settings.keys.MetaKeys

class RequestPermissionsViewModel(
    private val settingsProvider: SettingsProvider,
    private val permissionChecker: PermissionsChecker
) : ViewModel() {

    val permissions: Array<String>
        get() {
            val permissionList = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            // Add POST_NOTIFICATIONS for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionList.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            return permissionList.toTypedArray()
        }

    fun shouldAskForPermissions(): Boolean {
        val permissionsAlreadyRequested =
            settingsProvider.getMetaSettings().getBoolean(MetaKeys.PERMISSIONS_REQUESTED)
        val permissionsGranted =
            permissionChecker.isPermissionGranted(*permissions)

        return !(permissionsAlreadyRequested || permissionsGranted)
    }

    fun permissionsRequested() {
        settingsProvider.getMetaSettings().save(MetaKeys.PERMISSIONS_REQUESTED, true)
    }
}
