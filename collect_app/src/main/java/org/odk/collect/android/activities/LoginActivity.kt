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
import org.odk.collect.android.authentication.UserRole
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
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_STATE = "user_state"

        // Fallback mode for offline/testing
        private const val ENABLE_FALLBACK_AUTH = true
        private const val FALLBACK_USERNAME = "admin"
        private const val FALLBACK_PASSWORD = "admin123"

        // Anonymous/Guest login credentials
        const val ANONYMOUS_EMAIL = "guest@acresal.com"
        const val ANONYMOUS_PASSWORD = "guest123"

        // Key to track if user is guest
        private const val KEY_IS_GUEST_USER = "is_guest_user"

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
                remove(KEY_USER_ROLE)
                remove(KEY_USER_STATE)
                remove(KEY_IS_GUEST_USER)
                apply()
            }
            Timber.d("User logged out successfully")
        }

        fun isGuestUser(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_IS_GUEST_USER, false)
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
                token = prefs.getString(KEY_AUTH_TOKEN, "") ?: "",
                role = UserRole.fromString(prefs.getString(KEY_USER_ROLE, null)),
                state = prefs.getString(KEY_USER_STATE, "") ?: ""
            )
        }

        fun getUserRole(context: Context): UserRole {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return UserRole.fromString(prefs.getString(KEY_USER_ROLE, null))
        }

        fun getUserState(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_USER_STATE, "") ?: ""
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

        // Anonymous/Guest login
        binding.anonymousLoginText.setOnClickListener {
            hideKeyboard()
            performAnonymousLogin()
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
            putString(KEY_USER_ROLE, userData.role.roleName)
            putString(KEY_USER_STATE, userData.state)
            apply()
        }
        Timber.d("User session saved: ${userData.username}, role: ${userData.role}, state: ${userData.state}")
    }

    private fun performAnonymousLogin() {
        Timber.d("Performing anonymous/guest login")
        ToastUtils.showShortToast(this, "Logging in as Guest...")

        // Mark as guest user
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_GUEST_USER, true).apply()

        // Set loading state
        setLoadingState(true)

        // Use default guest credentials
        performAzureLogin(ANONYMOUS_EMAIL, ANONYMOUS_PASSWORD)
    }

    private fun navigateToApp() {
        // Check if this is a guest user
        val isGuest = isGuestUser(this)

        // Check if project is already configured
        try {
            val projectsDataService = org.odk.collect.android.injection.DaggerUtils.getComponent(this).currentProjectProvider()
            val currentProject = projectsDataService.getCurrentProject()

            // Project already exists
            if (isGuest) {
                // Guest users can still access project configuration screen
                Timber.d("Guest user logged in with existing project, allowing project configuration")
                ActivityUtils.startActivityAndCloseAllOthers(this, FirstLaunchActivity::class.java)
            } else {
                // Regular users go directly to main menu
                ActivityUtils.startActivityAndCloseAllOthers(this, MainMenuActivity::class.java)
            }
        } catch (e: Exception) {
            // No project yet
            if (isGuest) {
                // Guest users go to manual project configuration screen
                Timber.d("Guest user logged in, showing project configuration screen")
                ActivityUtils.startActivityAndCloseAllOthers(this, FirstLaunchActivity::class.java)
            } else {
                // Regular users get auto-configured project
                Timber.d("Regular user, navigating to auto-configure: ${e.message}")
                ActivityUtils.startActivityAndCloseAllOthers(this, AC_FirstLaunchActivity::class.java)
            }
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
