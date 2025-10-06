package org.odk.collect.android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import org.odk.collect.android.databinding.LoginActivityBinding
import org.odk.collect.androidshared.ui.ToastUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding

    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"

        // Hardcoded credentials (you can modify these or make them configurable)
        private const val VALID_USERNAME = "admin"
        private const val VALID_PASSWORD = "admin123"

        fun isLoggedIn(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        }

        fun logout(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, false)
                remove(KEY_USERNAME)
                apply()
            }
        }

        fun getLoggedInUsername(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_USERNAME, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Simulate authentication delay (remove in production or replace with actual API call)
        binding.root.postDelayed({
            performLogin(username, password)
        }, 500)
    }

    private fun performLogin(username: String, password: String) {
        // Validate credentials
        if (username == VALID_USERNAME && password == VALID_PASSWORD) {
            // Save login state
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, true)
                putString(KEY_USERNAME, username)
                apply()
            }

            // Navigate to AC_FirstLaunchActivity
            ActivityUtils.startActivityAndCloseAllOthers(this, AC_FirstLaunchActivity::class.java)
        } else {
            setLoadingState(false)
            showError("Invalid username or password")
            ToastUtils.showShortToast(this, "Login failed. Please try again.")
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
