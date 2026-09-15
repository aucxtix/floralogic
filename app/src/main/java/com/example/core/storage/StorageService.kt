package com.example.core.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject

/**
 * StorageService provides safe, versioned local persistence for Kissan AI.
 * Handles corruption recovery gracefully and supports schema migrations.
 * Strictly adheres to security rules: NEVER stores raw passwords, secret keys, or biometric data.
 */
interface StorageService {
  fun getString(key: String, defaultValue: String = ""): String
  fun putString(key: String, value: String)
  
  fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
  fun putBoolean(key: String, value: Boolean)
  
  fun getInt(key: String, defaultValue: Int = 0): Int
  fun putInt(key: String, value: Int)
  
  fun getLong(key: String, defaultValue: Long = 0L): Long
  fun putLong(key: String, value: Long)

  fun remove(key: String)
  fun clear()
  
  fun getStorageVersion(): Int
  fun migrateIfNeeded()
}

class KissanStorageService(
  private val context: Context,
  private val prefsName: String = PREFS_NAME
) : StorageService {

  private val prefs: SharedPreferences by lazy {
    try {
      context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } catch (e: Exception) {
      Log.e(TAG, "Error opening SharedPreferences, falling back to safe context", e)
      context.getSharedPreferences("${prefsName}_safe_fallback", Context.MODE_PRIVATE)
    }
  }

  init {
    migrateIfNeeded()
  }

  override fun getString(key: String, defaultValue: String): String {
    return try {
      prefs.getString(key, defaultValue) ?: defaultValue
    } catch (e: Exception) {
      Log.w(TAG, "Corrupted value for key '$key', recovering with default: $defaultValue", e)
      defaultValue
    }
  }

  override fun putString(key: String, value: String) {
    try {
      prefs.edit().putString(key, value).apply()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to safely write string for key '$key'", e)
    }
  }

  override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
    return try {
      prefs.getBoolean(key, defaultValue)
    } catch (e: Exception) {
      Log.w(TAG, "Corrupted boolean for key '$key', recovering with default: $defaultValue", e)
      defaultValue
    }
  }

  override fun putBoolean(key: String, value: Boolean) {
    try {
      prefs.edit().putBoolean(key, value).apply()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to safely write boolean for key '$key'", e)
    }
  }

  override fun getInt(key: String, defaultValue: Int): Int {
    return try {
      prefs.getInt(key, defaultValue)
    } catch (e: Exception) {
      Log.w(TAG, "Corrupted int for key '$key', recovering with default: $defaultValue", e)
      defaultValue
    }
  }

  override fun putInt(key: String, value: Int) {
    try {
      prefs.edit().putInt(key, value).apply()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to safely write int for key '$key'", e)
    }
  }

  override fun getLong(key: String, defaultValue: Long): Long {
    return try {
      prefs.getLong(key, defaultValue)
    } catch (e: Exception) {
      Log.w(TAG, "Corrupted long for key '$key', recovering with default: $defaultValue", e)
      defaultValue
    }
  }

  override fun putLong(key: String, value: Long) {
    try {
      prefs.edit().putLong(key, value).apply()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to safely write long for key '$key'", e)
    }
  }

  override fun remove(key: String) {
    try {
      prefs.edit().remove(key).apply()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to remove key '$key'", e)
    }
  }

  override fun clear() {
    try {
      prefs.edit().clear().apply()
    } catch (e: Exception) {
      Log.e(TAG, "Failed to clear storage", e)
    }
  }

  override fun getStorageVersion(): Int {
    return getInt(KEY_STORAGE_VERSION, CURRENT_STORAGE_VERSION)
  }

  override fun migrateIfNeeded() {
    try {
      val existingVersion = getInt(KEY_STORAGE_VERSION, 0)
      if (existingVersion < CURRENT_STORAGE_VERSION) {
        Log.i(TAG, "Migrating storage schema from v$existingVersion to v$CURRENT_STORAGE_VERSION")
        // Perform migration logic if schema changes in future
        putInt(KEY_STORAGE_VERSION, CURRENT_STORAGE_VERSION)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Migration failed, resetting to safe defaults without crashing", e)
      putInt(KEY_STORAGE_VERSION, CURRENT_STORAGE_VERSION)
    }
  }

  companion object {
    private const val TAG = "KissanStorageService"
    const val PREFS_NAME = "kissan_ai_storage_v1"
    const val CURRENT_STORAGE_VERSION = 1
    const val KEY_STORAGE_VERSION = "storage_schema_version"

    // Recognized safe storage keys
    const val KEY_LANGUAGE = "pref_language_code"
    const val KEY_SESSION_ACTIVE = "session_active"
    const val KEY_USER_EMAIL = "session_user_email"
    const val KEY_SESSION_TOKEN = "session_auth_token"
    const val KEY_BIOMETRIC_ENABLED = "pref_biometric_login_enabled"
    const val KEY_SAVED_LOCATION_JSON = "pref_saved_farm_location"
    const val KEY_DEMO_MODE = "pref_demo_mode_active"
    const val KEY_ONBOARDING_COMPLETED = "pref_onboarding_completed"
  }
}
