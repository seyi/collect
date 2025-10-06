package org.odk.collect.android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.odk.collect.android.authentication.AzureAuthService
import org.odk.collect.android.authentication.AuthResult
import org.odk.collect.android.authentication.UserData
import org.odk.collect.android.databinding.LoginActivityBinding
import org.odk.collect.android.mainmenu.MainMenuActivity
import org.odk.collect.androidshared.ui.ToastUtils
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var azureAuthService: AzureAuthService

    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_AUTH_TOKEN = "auth_token"

        // Fallback mode for offline/testing
        private const val ENABLE_FALLBACK_AUTH = true
        private const val FALLBACK_USERNAME = "admin"
        private const val FALLBACK_PASSWORD = "admin123"

        fun isLoggedIn(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        }

        fun logout(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, false)
                remove(KEY_USERNAME)
                remove(KEY_USER_ID)
                remove(KEY_EMAIL)
                remove(KEY_DISPLAY_NAME)
                remove(KEY_AUTH_TOKEN)
                apply()
            }
            Timber.d("User logged out successfully")
        }

        fun getLoggedInUsername(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_USERNAME, null)
        }

        fun getUserData(context: Context): UserData? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val username = prefs.getString(KEY_USERNAME, null) ?: return null

            return UserData(
                username = username,
                userId = prefs.getString(KEY_USER_ID, "") ?: "",
                email = prefs.getString(KEY_EMAIL, "") ?: "",
                displayName = prefs.getString(KEY_DISPLAY_NAME, username) ?: username,
                token = prefs.getString(KEY_AUTH_TOKEN, "") ?: ""
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Azure Auth Service
        azureAuthService = AzureAuthService(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            hideKeyboard()
            attemptLogin()
        }

        // Allow login on "Enter" key press
        binding.passwordInput.setOnEditorActionListener { _, _, _ ->
            hideKeyboard()
            attemptLogin()
            true
        }
    }

    private fun attemptLogin() {
        val username = binding.usernameInput.text?.toString()?.trim() ?: ""
        val password = binding.passwordInput.text?.toString() ?: ""

        // Reset error message
        binding.errorMessage.visibility = View.GONE

        // Validate inputs
        if (username.isEmpty()) {
            showError("Please enter username")
            binding.usernameLayout.error = "Required"
            return
        }

        if (password.isEmpty()) {
            showError("Please enter password")
            binding.passwordLayout.error = "Required"
            return
        }

        // Clear field errors
        binding.usernameLayout.error = null
        binding.passwordLayout.error = null

        // Show loading state
        setLoadingState(true)

        // Authenticate with Azure Function App
        performAzureLogin(username, password)
    }

    private fun performAzureLogin(username: String, password: String) {
        lifecycleScope.launch {
            try {
                Timber.d("Authenticating user: $username")

                // Call Azure Function App for authentication
                val result = azureAuthService.authenticate(username, password)

                when (result) {
                    is AuthResult.Success -> {
                        // Authentication successful
                        Timber.d("Azure authentication successful for user: ${result.userData.username}")
                        saveUserSession(result.userData)
                        navigateToApp()
                    }
                    is AuthResult.Error -> {
                        // Authentication failed - try fallback if enabled
                        Timber.w("Azure authentication failed: ${result.message}")

                        if (ENABLE_FALLBACK_AUTH && username == FALLBACK_USERNAME && password == FALLBACK_PASSWORD) {
                            Timber.d("Using fallback authentication")
                            val fallbackUserData = UserData(
                                username = username,
                                displayName = "Admin User (Offline)",
                                userId = "fallback-admin"
                            )
                            saveUserSession(fallbackUserData)
                            ToastUtils.showShortToast(this@LoginActivity, "Logged in offline mode")
                            navigateToApp()
                        } else {
                            setLoadingState(false)
                            showError(result.message)
                            ToastUtils.showShortToast(this@LoginActivity, result.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during login")
                setLoadingState(false)
                showError("An unexpected error occurred")
                ToastUtils.showShortToast(this@LoginActivity, "Login error: ${e.message}")
            }
        }
    }

    private fun saveUserSession(userData: UserData) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USERNAME, userData.username)
            putString(KEY_USER_ID, userData.userId)
            putString(KEY_EMAIL, userData.email)
            putString(KEY_DISPLAY_NAME, userData.displayName)
            putString(KEY_AUTH_TOKEN, userData.token)
            apply()
        }
        Timber.d("User session saved: ${userData.username}")
    }

    private fun navigateToApp() {
        // Check if project is already configured, if so skip configuration screen
        val projectsDataService = (application as? android.app.Application)?.let {
            org.odk.collect.android.injection.DaggerUtils.getComponent(this).projectsDataService()
        }

        val hasProject = projectsDataService?.getCurrentProject() != null

        if (hasProject) {
            // Project already exists, go directly to MainMenuActivity
            ActivityUtils.startActivityAndCloseAllOthers(this, MainMenuActivity::class.java)
        } else {
            // No project yet, go to configuration screen which will auto-configure
            ActivityUtils.startActivityAndCloseAllOthers(this, AC_FirstLaunchActivity::class.java)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        binding.usernameInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.text = if (isLoading) "" else "Sign In"
    }

    private fun showError(message: String) {
        binding.errorMessage.text = message
        binding.errorMessage.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        // Prevent going back to previous screen
        // User must login to proceed
        finishAffinity()
    }
}
