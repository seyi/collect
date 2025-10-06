# Role-Based Access Control Usage Guide

This guide shows how to use role-based permissions in the ACReSAL Collect app.

## User Roles

The app supports the following user roles from the Azure Function API:

- **`federal_admin`** - Federal Administrator (highest privileges)
- **`state_admin`** - State Administrator
- **`federal_user`** - Federal User
- **`state_user`** - State User
- **`admin`** - General Administrator
- **`testuser`** - Test User (typically read-only)

## Getting User Information

### Get Current User Role

```kotlin
import org.odk.collect.android.activities.LoginActivity

val userRole = LoginActivity.getUserRole(context)
// Returns UserRole enum: FEDERAL_ADMIN, STATE_ADMIN, etc.
```

### Get Current User Data

```kotlin
val userData = LoginActivity.getUserData(context)
if (userData != null) {
    val email = userData.email
    val displayName = userData.displayName
    val role = userData.role
    val state = userData.state
    val token = userData.token
}
```

### Get User State

```kotlin
val userState = LoginActivity.getUserState(context)
// Returns state string (e.g., "Lagos", "Kano", etc.)
```

## Checking Permissions

### Using UserPermissions Helper

```kotlin
import org.odk.collect.android.authentication.UserPermissions

// Check if user is admin
if (UserPermissions.isAdmin(context)) {
    // Show admin menu
}

// Check if user is federal admin
if (UserPermissions.isFederalAdmin(context)) {
    // Allow federal-level operations
}

// Check if user is state admin
if (UserPermissions.isStateAdmin(context)) {
    // Allow state-level operations
}

// Check if user can manage other users
if (UserPermissions.canManageUsers(context)) {
    // Show user management UI
}

// Check if user can edit data
if (UserPermissions.canEditData(context)) {
    // Enable edit button
} else {
    // Show read-only view
}

// Check if user can delete data
if (UserPermissions.canDeleteData(context)) {
    // Show delete button
}

// Check if user can approve data
if (UserPermissions.canApproveData(context)) {
    // Show approval button
}

// Check if user can upload bulk data
if (UserPermissions.canUploadBulkData(context)) {
    // Show bulk upload feature
}
```

### State-Based Access Control

```kotlin
// Check if user can access a specific state's data
val targetState = "Lagos"
if (UserPermissions.canAccessState(context, targetState)) {
    // Load Lagos state data
} else {
    // Show access denied message
    val message = UserPermissions.showPermissionDeniedMessage(context, "access Lagos data")
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
```

## Using UserRole Enum Directly

```kotlin
import org.odk.collect.android.authentication.UserRole

val role = LoginActivity.getUserRole(context)

when (role) {
    UserRole.FEDERAL_ADMIN -> {
        // Federal admin specific logic
    }
    UserRole.STATE_ADMIN -> {
        // State admin specific logic
    }
    UserRole.FEDERAL_USER -> {
        // Federal user specific logic
    }
    UserRole.STATE_USER -> {
        // State user specific logic
    }
    UserRole.TEST_USER -> {
        // Test user specific logic (read-only)
    }
    else -> {
        // Handle unknown role
    }
}

// Check role properties
if (role.isAdmin()) {
    // User is any type of admin
}

if (role.isFederalLevel()) {
    // User has federal-level access
}

if (role.isStateLevel()) {
    // User has state-level access
}

// Compare role hierarchy
val otherRole = UserRole.STATE_USER
if (role.hasHigherPrivilegesThan(otherRole)) {
    // Current user has higher privileges
}
```

## Example Use Cases

### Example 1: Conditional Menu Items

```kotlin
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)

    // Only show user management for admins
    val manageUsersItem = menu.findItem(R.id.action_manage_users)
    manageUsersItem.isVisible = UserPermissions.canManageUsers(this)

    // Only show bulk upload for admins
    val bulkUploadItem = menu.findItem(R.id.action_bulk_upload)
    bulkUploadItem.isVisible = UserPermissions.canUploadBulkData(this)

    return true
}
```

### Example 2: Conditional UI Elements

```kotlin
// In your Activity or Fragment
binding.apply {
    // Show/hide edit button based on permissions
    editButton.isVisible = UserPermissions.canEditData(this@MyActivity)

    // Show/hide delete button based on permissions
    deleteButton.isVisible = UserPermissions.canDeleteData(this@MyActivity)

    // Show/hide approve button based on permissions
    approveButton.isVisible = UserPermissions.canApproveData(this@MyActivity)

    // Display user role
    userRoleText.text = UserPermissions.getRoleDescription(this@MyActivity)
}
```

### Example 3: Data Filtering by State

```kotlin
fun loadData() {
    val userState = LoginActivity.getUserState(this)
    val role = LoginActivity.getUserRole(this)

    if (role.isFederalLevel()) {
        // Load all states data
        loadAllStatesData()
    } else {
        // Load only user's assigned state data
        loadStateData(userState)
    }
}
```

### Example 4: Form Submission with Role Check

```kotlin
fun submitForm() {
    if (!UserPermissions.canEditData(this)) {
        Toast.makeText(
            this,
            UserPermissions.showPermissionDeniedMessage(this, "submit forms"),
            Toast.LENGTH_LONG
        ).show()
        return
    }

    // Proceed with form submission
    performSubmit()
}
```

### Example 5: Approval Workflow

```kotlin
fun approveIntervention(interventionId: String) {
    if (!UserPermissions.canApproveData(this)) {
        Toast.makeText(
            this,
            "Only Federal Admins and Federal Users can approve interventions",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    // Proceed with approval
    sendApprovalRequest(interventionId)
}
```

### Example 6: Debug User Permissions

```kotlin
// Log all user permissions for debugging
UserPermissions.logUserPermissions(this)

// Output in Logcat:
// === User Permissions ===
// User: John Doe (john@example.com)
// Role: federal_admin - Federal Administrator
// State: Lagos
// Is Admin: true
// Can Manage Users: true
// Can Edit Data: true
// Can Delete Data: true
// Can Approve Data: true
// ======================
```

## Role Hierarchy

The role hierarchy from highest to lowest privilege:

1. **Federal Admin** (highest)
2. **Admin**
3. **State Admin**
4. **Federal User**
5. **State User**
6. **Test User** (lowest)

```kotlin
val currentRole = LoginActivity.getUserRole(context)
val targetRole = UserRole.STATE_USER

if (currentRole.hasHigherPrivilegesThan(targetRole)) {
    // Current user can perform actions on target user
}
```

## Permission Matrix

| Action | Federal Admin | State Admin | Federal User | State User | Test User |
|--------|---------------|-------------|--------------|------------|-----------|
| View All States | ✅ | ❌ | ✅ | ❌ | ❌ |
| View Own State | ✅ | ✅ | ✅ | ✅ | ✅ |
| Edit Data | ✅ | ✅ | ✅ | ✅ | ❌ |
| Delete Data | ✅ | ✅ | ❌ | ❌ | ❌ |
| Approve Data | ✅ | ✅ | ✅ | ❌ | ❌ |
| Manage Users | ✅ | ✅ | ❌ | ❌ | ❌ |
| Bulk Upload | ✅ | ✅ | ❌ | ❌ | ❌ |

## Best Practices

1. **Always check permissions before showing UI elements**
   ```kotlin
   button.isVisible = UserPermissions.canDeleteData(context)
   ```

2. **Check permissions before performing actions**
   ```kotlin
   if (!UserPermissions.canEditData(context)) {
       showPermissionDeniedMessage()
       return
   }
   ```

3. **Use descriptive messages**
   ```kotlin
   val message = UserPermissions.showPermissionDeniedMessage(context, "delete this record")
   ```

4. **Log permissions for debugging**
   ```kotlin
   UserPermissions.logUserPermissions(context)
   ```

5. **Handle state-based access**
   ```kotlin
   if (!UserPermissions.canAccessState(context, targetState)) {
       return // Don't show data from other states
   }
   ```

## Integration with Existing Code

To integrate role-based permissions into existing activities/fragments:

```kotlin
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        if (!LoginActivity.isLoggedIn(this)) {
            // Redirect to login
            return
        }

        // Setup UI based on permissions
        setupPermissionBasedUI()
    }

    private fun setupPermissionBasedUI() {
        val isAdmin = UserPermissions.isAdmin(this)
        val canEdit = UserPermissions.canEditData(this)

        // Configure UI elements
        binding.adminPanel.isVisible = isAdmin
        binding.editButton.isEnabled = canEdit

        // Log permissions for debugging
        if (BuildConfig.DEBUG) {
            UserPermissions.logUserPermissions(this)
        }
    }
}
```
