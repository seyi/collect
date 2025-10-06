package org.odk.collect.android.authentication

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Service class to handle authentication with Azure Function App
 */
class AzureAuthService(private val context: Context) {

    companion object {
        // Default Azure Function URL - can be overridden via configuration
        private const val DEFAULT_AZURE_FUNCTION_URL = "https://acresalgisfunctionapp.azurewebsites.net/api/login"
        private const val PREFS_NAME = "AzureAuthConfig"
        private const val KEY_FUNCTION_URL = "azure_function_url"

        private const val TIMEOUT_SECONDS = 30L
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    /**
     * Get the configured Azure Function URL
     */
    private fun getAzureFunctionUrl(): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FUNCTION_URL, DEFAULT_AZURE_FUNCTION_URL) ?: DEFAULT_AZURE_FUNCTION_URL
    }

    /**
     * Set custom Azure Function URL
     */
    fun setAzureFunctionUrl(url: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FUNCTION_URL, url).apply()
        Timber.d("Azure Function URL updated: $url")
    }

    /**
     * Authenticate user with Azure Function App
     *
     * @param username User's username or email
     * @param password User's password
     * @return AuthResult with success/failure status and optional user data
     */
    suspend fun authenticate(username: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("Attempting authentication for user: $username")

                // Build JSON request body
                // Note: Azure Function expects "email" field, not "username"
                val jsonBody = JSONObject().apply {
                    put("email", username)
                    put("password", password)
                }.toString()

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonBody.toRequestBody(mediaType)

                // Build HTTP request
                val request = Request.Builder()
                    .url(getAzureFunctionUrl())
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                Timber.d("Sending authentication request to: ${getAzureFunctionUrl()}")

                // Execute request
                val response = okHttpClient.newCall(request).execute()

                response.use { resp ->
                    val responseBody = resp.body?.string() ?: ""

                    Timber.d("Authentication response status: ${resp.code}")

                    when {
                        resp.isSuccessful -> {
                            // Parse successful response
                            try {
                                val jsonResponse = JSONObject(responseBody)

                                // Azure Function returns: { message, token, user: { id, email, displayName, role, state } }
                                val token = jsonResponse.optString("token", "")
                                val userObj = jsonResponse.optJSONObject("user")

                                if (token.isNotEmpty() && userObj != null) {
                                    val userData = UserData(
                                        username = userObj.optString("email", username),
                                        userId = userObj.optString("id", ""),
                                        email = userObj.optString("email", ""),
                                        displayName = userObj.optString("displayName", username),
                                        token = token
                                    )

                                    Timber.d("Authentication successful for user: ${userData.username}")
                                    AuthResult.Success(userData)
                                } else {
                                    val message = jsonResponse.optString("message", "Authentication failed")
                                    Timber.w("Authentication failed: $message")
                                    AuthResult.Error(message)
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing authentication response")
                                AuthResult.Error("Invalid server response")
                            }
                        }
                        resp.code == 401 -> {
                            Timber.w("Authentication failed: Invalid credentials")
                            AuthResult.Error("Invalid username or password")
                        }
                        resp.code == 403 -> {
                            Timber.w("Authentication failed: Access forbidden")
                            AuthResult.Error("Access denied")
                        }
                        resp.code >= 500 -> {
                            Timber.e("Server error during authentication: ${resp.code}")
                            AuthResult.Error("Server error. Please try again later.")
                        }
                        else -> {
                            Timber.e("Unexpected response code: ${resp.code}")
                            AuthResult.Error("Authentication failed. Please try again.")
                        }
                    }
                }
            } catch (e: IOException) {
                Timber.e(e, "Network error during authentication")
                AuthResult.Error("Network error. Please check your connection.")
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during authentication")
                AuthResult.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    /**
     * Verify token with Azure Function (optional - for token-based sessions)
     */
    suspend fun verifyToken(token: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = JSONObject().apply {
                    put("token", token)
                }.toString()

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonBody.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(getAzureFunctionUrl().replace("/login", "/verify"))
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = okHttpClient.newCall(request).execute()

                response.use { resp ->
                    if (resp.isSuccessful) {
                        val responseBody = resp.body?.string() ?: ""
                        val jsonResponse = JSONObject(responseBody)

                        if (jsonResponse.optBoolean("valid", false)) {
                            val userData = UserData(
                                username = jsonResponse.optString("username", ""),
                                userId = jsonResponse.optString("userId", ""),
                                email = jsonResponse.optString("email", ""),
                                displayName = jsonResponse.optString("displayName", ""),
                                token = token
                            )
                            AuthResult.Success(userData)
                        } else {
                            AuthResult.Error("Invalid token")
                        }
                    } else {
                        AuthResult.Error("Token verification failed")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error verifying token")
                AuthResult.Error("Token verification failed")
            }
        }
    }
}

/**
 * Sealed class representing authentication results
 */
sealed class AuthResult {
    data class Success(val userData: UserData) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Data class representing authenticated user data
 */
data class UserData(
    val username: String,
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val token: String = ""
)
