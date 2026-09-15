package com.example.core.session

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages user session state, authentication tokens, and credentials securely.
 */
class SessionManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _isLoggedIn = MutableStateFlow(prefs.getBoolean(KEY_IS_LOGGED_IN, false))
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _currentUserEmail = MutableStateFlow(prefs.getString(KEY_USER_EMAIL, "") ?: "")
  val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

  fun saveSession(email: String, token: String) {
    prefs.edit()
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .putString(KEY_USER_EMAIL, email)
      .putString(KEY_AUTH_TOKEN, token)
      .apply()
    _isLoggedIn.value = true
    _currentUserEmail.value = email
  }

  fun clearSession() {
    prefs.edit()
      .remove(KEY_IS_LOGGED_IN)
      .remove(KEY_USER_EMAIL)
      .remove(KEY_AUTH_TOKEN)
      .apply()
    _isLoggedIn.value = false
    _currentUserEmail.value = ""
  }

  fun getAuthToken(): String? {
    return prefs.getString(KEY_AUTH_TOKEN, null)
  }

  companion object {
    private const val PREFS_NAME = "kisan_session_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_AUTH_TOKEN = "auth_token"
  }
}
