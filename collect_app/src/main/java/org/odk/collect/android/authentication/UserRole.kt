package org.odk.collect.android.authentication

/**
 * User roles matching Azure Function API roles
 */
enum class UserRole(val roleName: String) {
    FEDERAL_ADMIN("federal_admin"),
    STATE_ADMIN("state_admin"),
    FEDERAL_USER("federal_user"),
    STATE_USER("state_user"),
    ADMIN("admin"),
    TEST_USER("testuser"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(role: String?): UserRole {
            return values().find { it.roleName.equals(role, ignoreCase = true) } ?: UNKNOWN
        }
    }

    /**
     * Check if this role is an admin role (federal_admin, state_admin, or admin)
     */
    fun isAdmin(): Boolean {
        return this == FEDERAL_ADMIN || this == STATE_ADMIN || this == ADMIN
    }

    /**
     * Check if this role is a federal-level role
     */
    fun isFederalLevel(): Boolean {
        return this == FEDERAL_ADMIN || this == FEDERAL_USER || this == ADMIN
    }

    /**
     * Check if this role is a state-level role
     */
    fun isStateLevel(): Boolean {
        return this == STATE_ADMIN || this == STATE_USER
    }

    /**
     * Check if this role can manage users
     */
    fun canManageUsers(): Boolean {
        return isAdmin()
    }

    /**
     * Check if this role has higher privileges than another role
     */
    fun hasHigherPrivilegesThan(other: UserRole): Boolean {
        val hierarchy = mapOf(
            FEDERAL_ADMIN to 5,
            ADMIN to 4,
            STATE_ADMIN to 3,
            FEDERAL_USER to 2,
            STATE_USER to 1,
            TEST_USER to 0,
            UNKNOWN to -1
        )
        return (hierarchy[this] ?: -1) > (hierarchy[other] ?: -1)
    }
}
