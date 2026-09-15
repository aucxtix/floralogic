package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.core.error.AppError
import com.example.core.network.NetworkResult
import com.example.core.session.SessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CoreArchitectureTest {

  @Test
  fun networkResult_handlesSuccessState() {
    val result = NetworkResult.Success("WeatherData")
    assertTrue(result.isSuccess)
    assertFalse(result.isError)
    assertFalse(result.isLoading)
    assertEquals("WeatherData", result.getOrNull())
  }

  @Test
  fun networkResult_handlesErrorState() {
    val result = NetworkResult.Error("Network Timeout", statusCode = 408)
    assertTrue(result.isError)
    assertFalse(result.isSuccess)
    assertEquals("Network Timeout", result.message)
    assertEquals(408, result.statusCode)
    assertNull(result.getOrNull())
  }

  @Test
  fun appError_hierarchyFormatsCorrectly() {
    val networkError = AppError.Network()
    assertTrue(networkError.userMessage.contains("cached farm data"))

    val authError = AppError.Authentication()
    assertTrue(authError.userMessage.contains("Incorrect email"))

    val validationError = AppError.Validation("Field cannot be blank", "email")
    assertEquals("email", validationError.fieldName)
  }

  @Test
  fun sessionManager_savesAndClearsSessionCorrectly() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val sessionManager = SessionManager(app)

    sessionManager.clearSession()
    assertFalse(sessionManager.isLoggedIn.value)
    assertEquals("", sessionManager.currentUserEmail.value)
    assertNull(sessionManager.getAuthToken())

    sessionManager.saveSession("rudra.patel@kisan.ai", "token_12345")
    assertTrue(sessionManager.isLoggedIn.value)
    assertEquals("rudra.patel@kisan.ai", sessionManager.currentUserEmail.value)
    assertEquals("token_12345", sessionManager.getAuthToken())

    sessionManager.clearSession()
    assertFalse(sessionManager.isLoggedIn.value)
    assertEquals("", sessionManager.currentUserEmail.value)
    assertNull(sessionManager.getAuthToken())
  }
}
