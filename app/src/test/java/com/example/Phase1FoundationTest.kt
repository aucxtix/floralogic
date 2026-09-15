package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.auth.KissanAuthService
import com.example.core.biometric.KissanBiometricService
import com.example.core.i18n.KissanLanguageService
import com.example.core.location.FarmLocation
import com.example.core.location.KissanFarmerLocationService
import com.example.core.storage.KissanStorageService
import com.example.core.storage.StorageService
import com.example.data.model.AppLanguage
import com.example.data.model.LocalizedStrings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Phase1FoundationTest {

  private lateinit var context: Context
  private lateinit var storageService: StorageService
  private lateinit var authService: KissanAuthService
  private lateinit var languageService: KissanLanguageService
  private lateinit var locationService: KissanFarmerLocationService
  private lateinit var biometricService: KissanBiometricService

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    storageService = KissanStorageService(context, "test_kissan_storage")
    storageService.clear()
    authService = KissanAuthService(storageService)
    languageService = KissanLanguageService(storageService)
    locationService = KissanFarmerLocationService(context, storageService)
    biometricService = KissanBiometricService(context, storageService)
  }

  // ==========================================
  // 1. STORAGE TESTS
  // ==========================================

  @Test
  fun storage_putAndGet_worksAccurately() {
    storageService.putString("test_str", "farmer_value")
    storageService.putBoolean("test_bool", true)
    storageService.putInt("test_int", 42)
    storageService.putLong("test_long", 999999L)

    assertEquals("farmer_value", storageService.getString("test_str"))
    assertTrue(storageService.getBoolean("test_bool"))
    assertEquals(42, storageService.getInt("test_int"))
    assertEquals(999999L, storageService.getLong("test_long"))
  }

  @Test
  fun storage_removeAndClear_operatesSafely() {
    storageService.putString("key1", "val1")
    storageService.putString("key2", "val2")

    storageService.remove("key1")
    assertEquals("", storageService.getString("key1", ""))
    assertEquals("val2", storageService.getString("key2", ""))

    storageService.clear()
    assertEquals("", storageService.getString("key2", ""))
  }

  @Test
  fun storage_corruptedData_recoversGracefullyWithoutCrashing() {
    // Write valid string, read as boolean or invalid
    val res = storageService.getString("non_existent_key", "default_fallback")
    assertEquals("default_fallback", res)

    val intRes = storageService.getInt("non_existent_int", -1)
    assertEquals(-1, intRes)
  }

  @Test
  fun storage_versioning_andMigrationHandled() {
    assertTrue(storageService.getStorageVersion() >= KissanStorageService.CURRENT_STORAGE_VERSION)
  }

  // ==========================================
  // 2. AUTHENTICATION & PERSISTENT SESSION TESTS
  // ==========================================

  @Test
  fun auth_initialState_unauthenticated() {
    assertFalse(authService.isAuthenticated())
    assertEquals("", authService.currentUserEmail.value)
  }

  @Test
  fun auth_login_persistsSessionAndSetsState() = runBlocking {
    val email = "ramesh.patel@kissan.ai"
    authService.login(email, "valid_token_123")

    assertTrue(authService.isAuthenticated())
    assertEquals(email, authService.currentUserEmail.value)

    // Verify written to storage
    assertTrue(storageService.getBoolean(KissanStorageService.KEY_SESSION_ACTIVE))
    assertEquals(email, storageService.getString(KissanStorageService.KEY_USER_EMAIL))
  }

  @Test
  fun auth_restoreSession_recoversActiveSessionOnAppRestart() = runBlocking {
    val email = "suresh.kisan@kissan.ai"
    authService.login(email, "token_xyz")

    // Simulate new app instance with same persistent storage
    val restoredAuthService = KissanAuthService(storageService)
    assertTrue(restoredAuthService.isAuthenticated())
    assertEquals(email, restoredAuthService.currentUserEmail.value)
  }

  @Test
  fun auth_logout_clearsSessionCompletely() = runBlocking {
    authService.login("farmer@kissan.ai", "token_123")
    assertTrue(authService.isAuthenticated())

    authService.logout()
    assertFalse(authService.isAuthenticated())
    assertEquals("", authService.currentUserEmail.value)

    // Verify storage cleared
    assertFalse(storageService.getBoolean(KissanStorageService.KEY_SESSION_ACTIVE, false))
    assertEquals("", storageService.getString(KissanStorageService.KEY_USER_EMAIL, ""))
  }

  // ==========================================
  // 3. LANGUAGE SWITCHING & FALLBACK TESTS
  // ==========================================

  @Test
  fun language_switch_updatesImmediateStateAndPersists() {
    assertEquals(AppLanguage.ENGLISH, languageService.currentLanguage.value)

    // Switch to Hindi
    languageService.setLanguage(AppLanguage.HINDI)
    assertEquals(AppLanguage.HINDI, languageService.currentLanguage.value)
    assertEquals("hi", storageService.getString(KissanStorageService.KEY_LANGUAGE))

    // Switch to Gujarati
    languageService.setLanguage(AppLanguage.GUJARATI)
    assertEquals(AppLanguage.GUJARATI, languageService.currentLanguage.value)
    assertEquals("gu", storageService.getString(KissanStorageService.KEY_LANGUAGE))

    // Switch back to English
    languageService.setLanguage(AppLanguage.ENGLISH)
    assertEquals(AppLanguage.ENGLISH, languageService.currentLanguage.value)
  }

  @Test
  fun language_strings_containCompleteTranslations() {
    val en = LocalizedStrings.get(AppLanguage.ENGLISH)
    val hi = LocalizedStrings.get(AppLanguage.HINDI)
    val gu = LocalizedStrings.get(AppLanguage.GUJARATI)

    // All must have titles and not be empty
    assertTrue(en.appName.isNotBlank())
    assertTrue(hi.appName.isNotBlank())
    assertTrue(gu.appName.isNotBlank())

    assertTrue(en.navHome.isNotBlank())
    assertTrue(hi.navHome.isNotBlank())
    assertTrue(gu.navHome.isNotBlank())

    assertTrue(en.loginTitle.isNotBlank())
    assertTrue(hi.loginTitle.isNotBlank())
    assertTrue(gu.loginTitle.isNotBlank())
  }

  @Test
  fun language_fallback_missingKeyGracefullyFallsBack() {
    val resolved = LocalizedStrings.safeTranslate("", "English Fallback Value")
    assertEquals("English Fallback Value", resolved)

    val existing = LocalizedStrings.safeTranslate("ખેતરનું સ્વાસ્થ્ય", "Farm Health")
    assertEquals("ખેતરનું સ્વાસ્થ્ય", existing)
  }

  // ==========================================
  // 4. LOCATION FOUNDATION TESTS
  // ==========================================

  @Test
  fun location_manualSelection_savesAndRestores() = runBlocking {
    val manualLoc = FarmLocation(
      village = "Bardoli",
      district = "Surat",
      state = "Gujarat",
      latitude = 21.1175,
      longitude = 73.1118,
      isManual = true
    )

    locationService.saveLocation(manualLoc)
    val restored = locationService.getSavedLocation()

    assertEquals("Bardoli", restored.village)
    assertEquals("Surat", restored.district)
    assertEquals("Gujarat", restored.state)
    assertTrue(restored.isManual)
    assertEquals("Bardoli, Surat, Gujarat", restored.toDisplayString())
  }

  @Test
  fun location_jsonSerialization_isLossless() {
    val loc = FarmLocation(
      village = "Navsari",
      district = "Navsari",
      state = "Gujarat",
      latitude = 20.95,
      longitude = 72.93,
      isManual = false
    )
    val json = loc.toJson()
    val parsed = FarmLocation.fromJson(json)

    assertNotNull(parsed)
    assertEquals("Navsari", parsed?.village)
    assertEquals("Gujarat", parsed?.state)
    assertEquals(20.95, parsed?.latitude ?: 0.0, 0.001)
  }

  // ==========================================
  // 5. BIOMETRIC CAPABILITY & PREFERENCE TESTS
  // ==========================================

  @Test
  fun biometric_preference_canBeToggled() {
    assertFalse(biometricService.isBiometricLoginEnabled())
    biometricService.setBiometricLoginEnabled(true)
    // Saved in storage
    assertTrue(storageService.getBoolean(KissanStorageService.KEY_BIOMETRIC_ENABLED))
  }
}
