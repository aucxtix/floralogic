package com.example.core.auth

import android.content.Context
import com.example.core.storage.KissanStorageService
import com.example.core.storage.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Farmer Authentication & Persistent Session Service.
 * Ensures security: NEVER stores raw passwords, password hashes, or biometric templates.
 */
interface AuthService {
  val isLoggedIn: StateFlow<Boolean>
  val currentUserEmail: StateFlow<String>
  
  suspend fun login(email: String, token: String = "session_token_valid")
  suspend fun logout()
  suspend fun restoreSession(): Boolean
  fun isAuthenticated(): Boolean
}

class KissanAuthService(
  private val storageService: StorageService
) : AuthService {

  private val _isLoggedIn = MutableStateFlow(false)
  override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  private val _currentUserEmail = MutableStateFlow("")
  override val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

  init {
    restoreSessionSync()
  }

  private fun restoreSessionSync(): Boolean {
    val active = storageService.getBoolean(KissanStorageService.KEY_SESSION_ACTIVE, false)
    val email = storageService.getString(KissanStorageService.KEY_USER_EMAIL, "")
    val token = storageService.getString(KissanStorageService.KEY_SESSION_TOKEN, "")

    return if (active && email.isNotBlank() && token.isNotBlank()) {
      _isLoggedIn.value = true
      _currentUserEmail.value = email
      true
    } else {
      _isLoggedIn.value = false
      _currentUserEmail.value = ""
      false
    }
  }

  override suspend fun restoreSession(): Boolean {
    return restoreSessionSync()
  }

  override suspend fun login(email: String, token: String) {
    storageService.putBoolean(KissanStorageService.KEY_SESSION_ACTIVE, true)
    storageService.putString(KissanStorageService.KEY_USER_EMAIL, email)
    storageService.putString(KissanStorageService.KEY_SESSION_TOKEN, token)

    _isLoggedIn.value = true
    _currentUserEmail.value = email
  }

  override suspend fun logout() {
    storageService.remove(KissanStorageService.KEY_SESSION_ACTIVE)
    storageService.remove(KissanStorageService.KEY_USER_EMAIL)
    storageService.remove(KissanStorageService.KEY_SESSION_TOKEN)

    _isLoggedIn.value = false
    _currentUserEmail.value = ""
  }

  override fun isAuthenticated(): Boolean {
    return _isLoggedIn.value
  }

  companion object {
    @Volatile
    private var INSTANCE: KissanAuthService? = null

    fun getInstance(context: Context): KissanAuthService {
      return INSTANCE ?: synchronized(this) {
        val storage = KissanStorageService(context.applicationContext)
        val instance = KissanAuthService(storage)
        INSTANCE = instance
        instance
      }
    }
  }
}
