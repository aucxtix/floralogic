package com.example.core.i18n

import android.content.Context
import com.example.core.storage.KissanStorageService
import com.example.core.storage.StorageService
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.LocalizedStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralized Language & Internationalization Service for Kissan AI.
 * Supports: English, Hindi, Gujarati.
 * Features:
 * - Immediate reactivity (StateFlow)
 * - Safe persistence in StorageService
 * - Clean English fallback for any missing key
 * - Never leaks raw translation keys
 */
interface LanguageService {
  val currentLanguage: StateFlow<AppLanguage>
  fun setLanguage(language: AppLanguage)
  fun getStrings(language: AppLanguage = currentLanguage.value): AppStrings
}

class KissanLanguageService(
  private val storageService: StorageService
) : LanguageService {

  private val _currentLanguage: MutableStateFlow<AppLanguage>

  init {
    val savedCode = storageService.getString(KissanStorageService.KEY_LANGUAGE, AppLanguage.ENGLISH.code)
    _currentLanguage = MutableStateFlow(AppLanguage.fromCode(savedCode))
  }

  override val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  override fun setLanguage(language: AppLanguage) {
    _currentLanguage.value = language
    storageService.putString(KissanStorageService.KEY_LANGUAGE, language.code)
    try {
      java.util.Locale.setDefault(java.util.Locale(language.code))
    } catch (_: Exception) {}
  }

  override fun getStrings(language: AppLanguage): AppStrings {
    return LocalizedStrings.get(language)
  }

  companion object {
    @Volatile
    private var INSTANCE: KissanLanguageService? = null

    fun getInstance(context: Context, storageService: StorageService): KissanLanguageService {
      return INSTANCE ?: synchronized(this) {
        val instance = KissanLanguageService(storageService)
        INSTANCE = instance
        instance
      }
    }
  }
}
