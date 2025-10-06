package org.odk.collect.android.authentication

import android.content.Context
import org.odk.collect.android.activities.LoginActivity
import timber.log.Timber

/**
 * Helper class to check user permissions based on role
 */
object UserPermissions {

    /**
     * Check if the current user is an admin (federal_admin, state_admin, or admin)
     */
    fun isAdmin(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role.isAdmin()
    }

    /**
     * Check if the current user is a federal admin
     */
    fun isFederalAdmin(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role == UserRole.FEDERAL_ADMIN || role == UserRole.ADMIN
    }

    /**
     * Check if the current user is a state admin
     */
    fun isStateAdmin(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role == UserRole.STATE_ADMIN
    }

    /**
     * Check if the current user is a test user
     */
    fun isTestUser(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role == UserRole.TEST_USER
    }

    /**
     * Check if the current user has federal-level access
     */
    fun hasFederalAccess(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role.isFederalLevel()
    }

    /**
     * Check if the current user has state-level access
     */
    fun hasStateAccess(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role.isStateLevel()
    }

    /**
     * Check if the current user can manage other users
     */
    fun canManageUsers(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        return role.canManageUsers()
    }

    /**
     * Check if the current user can access a specific state's data
     * Federal admins can access all states, state admins can only access their assigned state
     */
    fun canAccessState(context: Context, targetState: String): Boolean {
        val role = LoginActivity.getUserRole(context)

        // Federal admins and general admins can access all states
        if (role == UserRole.FEDERAL_ADMIN || role == UserRole.ADMIN || role == UserRole.FEDERAL_USER) {
            return true
        }

        // State-level users can only access their assigned state
        val userState = LoginActivity.getUserState(context)
        return userState.equals(targetState, ignoreCase = true)
    }

    /**
     * Check if the current user can edit/modify data
     */
    fun canEditData(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        // Test users typically have read-only access
        return role != UserRole.TEST_USER && role != UserRole.UNKNOWN
    }

    /**
     * Check if the current user can delete data
     */
    fun canDeleteData(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        // Only admins can delete
        return role.isAdmin()
    }

    /**
     * Check if the current user can approve interventions/data
     */
    fun canApproveData(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        // Admins and federal users can approve
        return role.isAdmin() || role == UserRole.FEDERAL_USER
    }

    /**
     * Check if the current user can upload bulk data
     */
    fun canUploadBulkData(context: Context): Boolean {
        val role = LoginActivity.getUserRole(context)
        // Only admins can upload bulk data
        return role.isAdmin()
    }

    /**
     * Get a human-readable description of the current user's role
     */
    fun getRoleDescription(context: Context): String {
        return when (LoginActivity.getUserRole(context)) {
            UserRole.FEDERAL_ADMIN -> "Federal Administrator"
            UserRole.STATE_ADMIN -> "State Administrator"
            UserRole.FEDERAL_USER -> "Federal User"
            UserRole.STATE_USER -> "State User"
            UserRole.ADMIN -> "Administrator"
            UserRole.TEST_USER -> "Test User"
            UserRole.UNKNOWN -> "Unknown Role"
        }
    }

    /**
     * Log user permissions for debugging
     */
    fun logUserPermissions(context: Context) {
        val userData = LoginActivity.getUserData(context)
        if (userData != null) {
            Timber.d("=== User Permissions ===")
            Timber.d("User: ${userData.displayName} (${userData.email})")
            Timber.d("Role: ${userData.role} - ${getRoleDescription(context)}")
            Timber.d("State: ${userData.state}")
            Timber.d("Is Admin: ${isAdmin(context)}")
            Timber.d("Can Manage Users: ${canManageUsers(context)}")
            Timber.d("Can Edit Data: ${canEditData(context)}")
            Timber.d("Can Delete Data: ${canDeleteData(context)}")
            Timber.d("Can Approve Data: ${canApproveData(context)}")
            Timber.d("======================")
        }
    }

    /**
     * Show a permission denied message
     */
    fun showPermissionDeniedMessage(context: Context, action: String): String {
        return "You do not have permission to $action. Your role: ${getRoleDescription(context)}"
    }
}
