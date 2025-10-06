# Azure Function App Authentication Setup

This document explains how to configure and use Azure Function App authentication for the ACReSAL Collect app.

## Overview

The app now supports authentication via Azure Function App API. The authentication flow:
1. User enters username and password
2. App sends credentials to Azure Function App endpoint
3. Azure Function validates credentials and returns success/error
4. On success, user session is saved locally
5. User is redirected to the app

## Azure Function App Setup

### Expected API Endpoint

**Endpoint:** `POST /api/login`

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "userPassword123"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "username": "user@example.com",
  "userId": "12345",
  "email": "user@example.com",
  "displayName": "John Doe",
  "token": "optional-jwt-token-here"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid username or password"
}
```

### Example Azure Function (Node.js)

```javascript
module.exports = async function (context, req) {
    const { username, password } = req.body;

    // Your authentication logic here
    // Example: Check against database, Active Directory, etc.

    if (isValidCredentials(username, password)) {
        context.res = {
            status: 200,
            body: {
                success: true,
                username: username,
                userId: "user-id-from-db",
                email: username,
                displayName: "User Display Name",
                token: "optional-jwt-token"
            }
        };
    } else {
        context.res = {
            status: 401,
            body: {
                success: false,
                message: "Invalid username or password"
            }
        };
    }
};
```

### Example Azure Function (C#)

```csharp
[FunctionName("Login")]
public static async Task<IActionResult> Run(
    [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = "login")] HttpRequest req,
    ILogger log)
{
    string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
    var data = JsonConvert.DeserializeObject<LoginRequest>(requestBody);

    // Your authentication logic here
    if (IsValidCredentials(data.Username, data.Password))
    {
        return new OkObjectResult(new
        {
            success = true,
            username = data.Username,
            userId = "user-id-from-db",
            email = data.Username,
            displayName = "User Display Name",
            token = "optional-jwt-token"
        });
    }
    else
    {
        return new UnauthorizedObjectResult(new
        {
            success = false,
            message = "Invalid username or password"
        });
    }
}

public class LoginRequest
{
    public string Username { get; set; }
    public string Password { get; set; }
}
```

## App Configuration

### Method 1: Update Default URL in Code

Edit `AzureAuthService.kt` and change the default URL:

```kotlin
private const val DEFAULT_AZURE_FUNCTION_URL = "https://your-function-app.azurewebsites.net/api/login"
```

### Method 2: Configure at Runtime

In your app initialization code (e.g., `Application.onCreate()` or `MainActivity`):

```kotlin
import org.odk.collect.android.authentication.AzureAuthConfig

// Configure Azure Function URL
AzureAuthConfig.configure(context, "https://your-function-app.azurewebsites.net/api/login")

// Or use predefined environments
AzureAuthConfig.configure(context, AzureAuthConfig.Urls.PRODUCTION)
```

### Method 3: Add Settings UI

You can create a settings screen to allow users/admins to configure the URL dynamically.

## Fallback Authentication

The app includes a fallback authentication mode for offline testing:

- **Enabled by default:** Set `ENABLE_FALLBACK_AUTH = true` in `LoginActivity.kt`
- **Test credentials:**
  - Username: `admin`
  - Password: `admin123`

When Azure authentication fails (network error, server down), the app will fall back to these credentials if they match.

**To disable fallback mode:**
```kotlin
private const val ENABLE_FALLBACK_AUTH = false
```

## Security Considerations

### 1. HTTPS Only
Always use HTTPS endpoints in production. The app supports HTTPS by default.

### 2. Token Management
If your Azure Function returns a JWT token, it's stored in SharedPreferences:
- Retrieve it using: `LoginActivity.getUserData(context)?.token`
- Use it for subsequent API calls if needed

### 3. Password Security
- Passwords are sent over HTTPS
- They are NOT stored locally
- Only the authentication result (token/session) is stored

### 4. Session Management
- Session data is stored in SharedPreferences
- Call `LoginActivity.logout(context)` to clear session
- Implement token refresh if using JWT tokens

## Testing

### Local Testing with Azure Functions Core Tools

1. Run your function locally:
```bash
func start
```

2. Configure app to use local endpoint:
```kotlin
AzureAuthConfig.configure(context, AzureAuthConfig.Urls.LOCAL)
// or
AzureAuthConfig.configure(context, "http://10.0.2.2:7071/api/login") // Android emulator
```

### Testing Scenarios

1. **Valid credentials:** Should authenticate successfully
2. **Invalid credentials:** Should show error message
3. **Network error:** Should fall back to offline mode (if enabled)
4. **Server error (500):** Should show appropriate error message
5. **Timeout:** Should show network error after 30 seconds

## Troubleshooting

### Authentication Always Fails

1. Check Azure Function URL is correct
2. Verify Azure Function is running and accessible
3. Check network connectivity
4. Review logs: `adb logcat | grep LoginActivity`

### Network Errors

1. Ensure Android app has INTERNET permission (already configured)
2. Check if device/emulator has network access
3. For local testing, use `10.0.2.2` instead of `localhost` on Android emulator

### CORS Issues (Web Testing)

If testing from a web browser, configure CORS in Azure Function:

```json
{
  "cors": {
    "allowedOrigins": ["*"]
  }
}
```

## Integration Examples

### Get Logged-In User Info

```kotlin
val userData = LoginActivity.getUserData(context)
if (userData != null) {
    Log.d("User", "Username: ${userData.username}")
    Log.d("User", "Display Name: ${userData.displayName}")
    Log.d("User", "Token: ${userData.token}")
}
```

### Logout User

```kotlin
LoginActivity.logout(context)
// Redirect to login screen
ActivityUtils.startActivityAndCloseAllOthers(context, LoginActivity::class.java)
```

### Check Login Status

```kotlin
if (LoginActivity.isLoggedIn(context)) {
    // User is logged in
} else {
    // Redirect to login
}
```

## API Response Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| success | Boolean | Yes | Authentication success status |
| username | String | Yes (on success) | User's username |
| userId | String | No | Unique user identifier |
| email | String | No | User's email address |
| displayName | String | No | User's display name |
| token | String | No | JWT or session token |
| message | String | Yes (on error) | Error message |

## Next Steps

1. Deploy your Azure Function App
2. Update the default URL in `AzureAuthService.kt`
3. Test authentication with real credentials
4. Disable fallback mode in production
5. Implement token refresh logic (if using tokens)
6. Add logout functionality to your app UI

## Support

For issues or questions:
- Check Azure Function logs in Azure Portal
- Review Android app logs: `adb logcat | grep -i auth`
- Verify network traffic with Charles Proxy or similar tools
