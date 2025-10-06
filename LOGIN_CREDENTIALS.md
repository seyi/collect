# ACReSAL Collect - Login Credentials Reference

## Login Options

The app supports two login methods:

### 1. Standard Login
Users can login with their registered email and password from the Azure Function database.

### 2. Anonymous/Guest Login
Click "Continue as Guest" button on the login screen.

**Guest Credentials:**
- Email: `guest@acresal.com`
- Password: `guest123`
- Role: Typically `testuser` (read-only access)

## Offline Fallback Login

If Azure Function is unreachable and fallback mode is enabled:

**Fallback Credentials:**
- Username: `admin`
- Password: `admin123`
- Note: Only works when `ENABLE_FALLBACK_AUTH = true` in `LoginActivity.kt`

## User Roles in Database

When creating users in your Azure Function database, assign one of these roles:

| Role | Description | Access Level |
|------|-------------|--------------|
| `federal_admin` | Federal Administrator | Full access to all states |
| `state_admin` | State Administrator | Full access to assigned state only |
| `federal_user` | Federal User | View/edit all states, cannot delete |
| `state_user` | State User | View/edit assigned state only |
| `admin` | General Administrator | Full access to all states |
| `testuser` | Test User | Read-only access |

## Guest User Configuration

The guest user should be configured in your Azure Function database as:

```json
{
  "email": "guest@acresal.com",
  "password": "guest123", // Hashed in database
  "role": "testuser",
  "displayName": "Guest User",
  "state": "",
  "isEmailVerified": true,
  "isActive": true
}
```

## Security Notes

1. **Change default passwords** in production
2. **Guest account** should have `testuser` role for read-only access
3. **Fallback mode** should be disabled in production (`ENABLE_FALLBACK_AUTH = false`)
4. **Email verification** must be enabled for the guest account in the database
5. **Account status** must be set to active for the guest account

## Testing Login

### Test with Guest Account
1. Open the app
2. Click "Continue as Guest"
3. App will automatically login with guest credentials
4. Verify you can view but not edit data (if role is `testuser`)

### Test with Regular Account
1. Open the app
2. Enter your email and password
3. Click "Sign In"
4. App will authenticate with Azure Function
5. Permissions will be based on your assigned role

## Troubleshooting

### Guest Login Fails
- Verify guest user exists in database: `guest@acresal.com`
- Check `isEmailVerified` is `true`
- Check `isActive` is `true`
- Verify password hash is correct for `guest123`
- Check Azure Function logs for authentication errors

### Regular Login Fails
- Verify email is correct and registered
- Verify password is correct
- Check if email is verified
- Check if account is active
- Verify Azure Function URL is correct in `AzureAuthService.kt`
- Check network connectivity

### Fallback Login Activates
- Means Azure Function is unreachable
- Check internet connection
- Verify Azure Function is running
- Check Azure Function URL
- Review app logs for network errors

## Modifying Credentials

### Change Guest Credentials

Edit `LoginActivity.kt`:
```kotlin
// Anonymous/Guest login credentials
const val ANONYMOUS_EMAIL = "your-guest@email.com"
const val ANONYMOUS_PASSWORD = "your-guest-password"
```

### Change Fallback Credentials

Edit `LoginActivity.kt`:
```kotlin
// Fallback mode for offline/testing
private const val FALLBACK_USERNAME = "your-admin"
private const val FALLBACK_PASSWORD = "your-admin-password"
```

### Disable Fallback Mode

Edit `LoginActivity.kt`:
```kotlin
private const val ENABLE_FALLBACK_AUTH = false
```

## Role-Based Features

Based on the logged-in user's role, different features are available:

- **Federal Admin**: Can view/edit/delete all states, manage users, bulk upload
- **State Admin**: Can view/edit/delete assigned state only, manage users
- **Federal User**: Can view/edit all states, approve data
- **State User**: Can view/edit assigned state only
- **Test User**: Read-only access, no editing or deletion
- **Admin**: Full access like Federal Admin

See `ROLE_BASED_ACCESS_USAGE.md` for complete permission details.
