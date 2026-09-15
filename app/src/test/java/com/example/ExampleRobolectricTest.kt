package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.location.Coordinates
import com.example.data.location.LocationService
import com.example.data.model.CropDisease
import com.example.data.model.DiseaseSeverity
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import com.example.ui.screens.AuthErrorMessages
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.WeatherIrrigationScreen
import com.example.presentation.theme.KisanAITheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KisanAI", appName)
  }

  @Test
  fun `results screen renders captured image viewport and ai detection placeholder`() {
    composeTestRule.setContent {
      KisanAITheme {
        ResultsScreen(
          cropName = "Tomato",
          detectionResult = null
        )
      }
    }

    composeTestRule.onNodeWithTag("results_screen").assertExists()
    composeTestRule.onNodeWithTag("results_image_viewport").assertExists()
    composeTestRule.onNodeWithTag("ai_detection_placeholder_card").assertExists()
    composeTestRule.onNodeWithTag("run_ai_detection_button").assertExists()
  }

  @Test
  fun `coordinates formatting and location service initialization`() {
    val coords = Coordinates(latitude = 21.1702, longitude = 72.8311)
    val formatted = coords.toFormattedString()
    assertEquals("21.1702° N, 72.8311° E", formatted)

    val context = ApplicationProvider.getApplicationContext<Context>()
    val locationService = LocationService(context)
    assertNotNull(locationService)
  }

  @Test
  fun `weather screen renders update gps button and coordinates`() {
    val sampleWeather = WeatherInfo(
      locationName = "Surat",
      state = "Gujarat",
      temperatureC = 28,
      condition = "Sunny",
      conditionIcon = "wb_sunny",
      humidityPercent = 70,
      rainProbabilityPercent = 10,
      rainfallMm = 0.0,
      windSpeedKmh = 10,
      irrigationAdvice = "Not needed",
      isIrrigationNeeded = false,
      sprayCondition = "Good",
      heatwaveAlert = false,
      riskAlertMessage = null,
      latitude = 21.1702,
      longitude = 72.8311,
      coordinatesFormatted = "21.1702° N, 72.8311° E"
    )

    composeTestRule.setContent {
      KisanAITheme {
        WeatherIrrigationScreen(
          weather = sampleWeather,
          farmerProfile = FarmerProfile()
        )
      }
    }

    composeTestRule.onNodeWithTag("weather_update_gps_button").assertExists()
  }

  @Test
  fun `biometric approval screen renders fingerprint trigger and action button`() {
    composeTestRule.setContent {
      KisanAITheme {
        com.example.ui.screens.BiometricApprovalScreen(
          farmerProfile = FarmerProfile(),
          onApprovalSuccess = {},
          onUsePassword = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("biometric_approval_screen").assertExists()
    composeTestRule.onNodeWithTag("biometric_sensor_button").assertExists()
    composeTestRule.onNodeWithTag("biometric_approve_button").assertExists()
  }

  @Test
  fun `filter screen renders category chips and apply button`() {
    composeTestRule.setContent {
      KisanAITheme {
        com.example.ui.screens.FilterScreen(
          onApplyFilters = {},
          onDismiss = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("filter_screen").assertExists()
    composeTestRule.onNodeWithTag("filter_apply_button").assertExists()
  }

  @Test
  fun `my account screen renders profile information and logout button`() {
    composeTestRule.setContent {
      KisanAITheme {
        com.example.ui.screens.MyAccountScreen(
          farmerProfile = FarmerProfile(),
          currentLanguage = com.example.data.model.AppLanguage.ENGLISH,
          onLanguageSelected = {},
          onNavigateToBiometric = {},
          onNavigateToHistory = {},
          onNavigateToWeather = {},
          onLogout = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("my_account_screen").assertExists()
    composeTestRule.onNodeWithTag("account_logout_button").assertExists()
  }

  @Test
  fun `inconclusive result view renders safety mode, threshold alert and helpline`() {
    composeTestRule.setContent {
      KisanAITheme {
        com.example.ui.screens.InconclusiveResultView(
          cropHint = "Tomato",
          confidence = 0.52f,
          reasons = listOf("Low image sharpness / motion blur"),
          suggestions = listOf("Hold camera steady at 15-20 cm"),
          capturedBitmap = null,
          imageUri = null,
          onRetake = {},
          onTryDifferentCrop = {},
          onBackClick = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("inconclusive_result_view").assertExists()
    composeTestRule.onNodeWithTag("kisan_call_centre_button").assertExists()
    composeTestRule.onNodeWithTag("inconclusive_retake_button").assertExists()
    composeTestRule.onNodeWithTag("inconclusive_change_crop_button").assertExists()
  }

  @Test
  fun `model transparency screen renders architecture specs and dataset provenance`() {
    composeTestRule.setContent {
      KisanAITheme {
        com.example.ui.screens.ModelTransparencyScreen(
          onBackClick = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("model_transparency_screen").assertExists()
  }

  @Test
  fun `pre-scan crop selection screen renders species grid, context card, and launch camera cta`() {
    var selectedCrop = "Tomato"
    var launchedCamera = false

    composeTestRule.setContent {
      KisanAITheme {
        com.example.ui.screens.PreScanCropSelectionScreen(
          selectedCrop = selectedCrop,
          onCropSelected = { selectedCrop = it },
          onLaunchCamera = { launchedCamera = true },
          onGalleryClick = {},
          onSelectSpecimen = {},
          onBackClick = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("pre_scan_crop_selection_screen").assertExists()
    composeTestRule.onNodeWithTag("crop_search_input").assertExists()
    composeTestRule.onNodeWithTag("launch_camera_button").assertExists()
    composeTestRule.onNodeWithTag("gallery_upload_button").assertExists()
    composeTestRule.onNodeWithTag("crop_card_Tomato").assertExists()
  }

  @Test
  fun `results screen displays inconclusive warning when confidence is below 70 percent`() {
    var retakeClicked = false
    val lowConfidenceDisease = CropDisease(
      id = "tomato_early_blight",
      cropName = "Tomato",
      diseaseName = "Early Blight (अगेती झुलसा)",
      scientificName = "Alternaria solani",
      isHealthy = false,
      confidence = 0.58f, // 58% < 70% threshold
      severity = DiseaseSeverity.MEDIUM,
      symptoms = listOf("Brown concentric spots on leaves"),
      organicTreatment = "Neem oil spray",
      chemicalTreatment = "Mancozeb",
      dosage = "2g / L",
      estimatedCostInr = "₹200",
      preventiveMeasures = listOf("Crop rotation"),
      adviceHindi = "नीम का तेल छिड़कें",
      adviceGujarati = "લીમડાના તેલનો છંટકાવ કરો"
    )

    composeTestRule.setContent {
      KisanAITheme {
        ResultsScreen(
          cropName = "Tomato",
          detectionResult = lowConfidenceDisease,
          onRetake = { retakeClicked = true }
        )
      }
    }

    composeTestRule.onNodeWithTag("results_screen").assertExists()
    // Inconclusive warning banner must be displayed
    composeTestRule.onNodeWithTag("inconclusive_confidence_warning").assertExists()
    // Captured image tag indicates inconclusive
    composeTestRule.onNodeWithTag("captured_image_inconclusive_tag").assertExists()
    // Retake clearer photo CTA button is present and clickable
    composeTestRule.onNodeWithTag("inconclusive_retake_button").assertExists()
    composeTestRule.onNodeWithTag("inconclusive_retake_button").performScrollTo().performClick()
    assertEquals(true, retakeClicked)
  }

  @Test
  fun `results screen does not display inconclusive warning when confidence is at or above 70 percent`() {
    val highConfidenceDisease = CropDisease(
      id = "tomato_late_blight",
      cropName = "Tomato",
      diseaseName = "Late Blight (पछेती झुलसा)",
      scientificName = "Phytophthora infestans",
      isHealthy = false,
      confidence = 0.94f, // 94% >= 70% threshold
      severity = DiseaseSeverity.HIGH,
      symptoms = listOf("Water-soaked dark lesions"),
      organicTreatment = "Trichoderma viride",
      chemicalTreatment = "Metalaxyl + Mancozeb",
      dosage = "2.5g / L",
      estimatedCostInr = "₹240",
      preventiveMeasures = listOf("Drip irrigation"),
      adviceHindi = "मैंकोजेब का छिड़काव करें",
      adviceGujarati = "મેન્કોઝેબનો છંટકાવ કરો"
    )

    composeTestRule.setContent {
      KisanAITheme {
        ResultsScreen(
          cropName = "Tomato",
          detectionResult = highConfidenceDisease
        )
      }
    }

    composeTestRule.onNodeWithTag("results_screen").assertExists()
    // Inconclusive warning banner should NOT exist
    composeTestRule.onNodeWithTag("inconclusive_confidence_warning").assertDoesNotExist()
    // Standard match tag should exist
    composeTestRule.onNodeWithTag("captured_image_match_tag").assertExists()
  }

  @Test
  fun `login failure returns exactly Incorrect email or password and does not reveal lockout or bad email`() {
    var loggedIn = false
    composeTestRule.setContent {
      KisanAITheme {
        LoginScreen(
          onLoginSuccess = { loggedIn = true },
          onNavigateToRegister = {}
        )
      }
    }

    // Set invalid password and click submit
    composeTestRule.onNodeWithTag("login_password_input").performScrollTo().performTextReplacement("invalid")
    composeTestRule.onNodeWithTag("login_submit_button").performScrollTo().performClick()

    // Assert exact error string matches required mandate
    composeTestRule.onNodeWithTag("auth_error_banner").performScrollTo().assertExists()
    composeTestRule.onNodeWithText(AuthErrorMessages.INCORRECT_CREDENTIALS).assertExists()
    assertEquals("Incorrect email or password", AuthErrorMessages.INCORRECT_CREDENTIALS)
    assertEquals(false, loggedIn)

    // Set locked account simulation
    composeTestRule.onNodeWithTag("login_password_input").performScrollTo().performTextReplacement("locked")
    composeTestRule.onNodeWithTag("login_submit_button").performScrollTo().performClick()

    // Locked accounts must also return exact same message without mentioning lockout
    composeTestRule.onNodeWithText(AuthErrorMessages.INCORRECT_CREDENTIALS).assertExists()
    composeTestRule.onNodeWithText("locked").assertDoesNotExist()
  }

  @Test
  fun `password reset says exactly If that email is registered you will receive a reset link`() {
    composeTestRule.setContent {
      KisanAITheme {
        LoginScreen(
          onLoginSuccess = {},
          onNavigateToRegister = {}
        )
      }
    }

    // Open forgot password dialog with scroll
    composeTestRule.onNodeWithTag("login_forgot_password_link").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("forgot_password_dialog").assertExists()

    // Enter email and submit
    composeTestRule.onNodeWithTag("forgot_password_email_input").performTextReplacement("farmer@example.com")
    composeTestRule.onNodeWithTag("forgot_password_submit_button").performClick()

    // Confirmation message must match exact string
    composeTestRule.onNodeWithTag("forgot_password_result_text").assertExists()
    composeTestRule.onNodeWithText(AuthErrorMessages.PASSWORD_RESET_SENT).assertExists()
    assertEquals("If that email is registered, you'll receive a reset link", AuthErrorMessages.PASSWORD_RESET_SENT)
  }

  @Test
  fun `onboarding screen does not use Already registered phrasing`() {
    composeTestRule.setContent {
      KisanAITheme {
        OnboardingScreen(
          onGetStarted = {},
          onSignInClick = {}
        )
      }
    }

    composeTestRule.onNodeWithText("Have an account? Sign In").assertExists()
    composeTestRule.onNodeWithText("Already registered? Sign In").assertDoesNotExist()
  }

  @Test
  fun `login screen provides Google and GitHub social login options`() {
    var socialProvider: String? = null
    composeTestRule.setContent {
      KisanAITheme {
        LoginScreen(
          onLoginSuccess = {},
          onNavigateToRegister = {},
          onGoogleSignIn = { socialProvider = "google" },
          onGitHubSignIn = { socialProvider = "github" }
        )
      }
    }

    composeTestRule.onNodeWithTag("google_login_button").performScrollTo().assertExists().performClick()
    assertEquals("google", socialProvider)

    composeTestRule.onNodeWithTag("github_login_button").performScrollTo().assertExists().performClick()
    assertEquals("github", socialProvider)
  }

  @Test
  fun `camera permission denied dialog renders explanation and grant button`() {
    var grantClicked = false
    var dismissClicked = false

    composeTestRule.setContent {
      KisanAITheme {
        com.example.CameraPermissionDeniedDialog(
          onGrantPermission = { grantClicked = true },
          onDismiss = { dismissClicked = true }
        )
      }
    }

    composeTestRule.onNodeWithTag("camera_permission_denied_dialog").assertExists()
    composeTestRule.onNodeWithTag("grant_camera_permission_button").assertExists().performClick()
    assertEquals(true, grantClicked)

    composeTestRule.onNodeWithTag("dismiss_permission_dialog_button").assertExists().performClick()
    assertEquals(true, dismissClicked)
  }

  @Test
  fun `camera permission permanently denied dialog renders settings link and explanation`() {
    var openSettingsClicked = false
    var dismissClicked = false

    composeTestRule.setContent {
      KisanAITheme {
        com.example.CameraPermissionPermanentlyDeniedDialog(
          onOpenSettings = { openSettingsClicked = true },
          onDismiss = { dismissClicked = true }
        )
      }
    }

    composeTestRule.onNodeWithTag("camera_permission_permanently_denied_dialog").assertExists()
    composeTestRule.onNodeWithTag("open_settings_button").assertExists().performClick()
    assertEquals(true, openSettingsClicked)

    composeTestRule.onNodeWithTag("dismiss_permanently_denied_dialog_button").assertExists().performClick()
    assertEquals(true, dismissClicked)
  }
}


