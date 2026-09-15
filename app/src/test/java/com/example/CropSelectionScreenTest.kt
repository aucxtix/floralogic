package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.KisanViewModel
import com.example.ui.screens.SUPPORTED_CROPS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CropSelectionScreenTest {

  @Test
  fun `supported crops contains requested crops like Wheat, Tomato, Paddy`() {
    val cropIds = SUPPORTED_CROPS.map { it.id }
    assertTrue("Should include Wheat", cropIds.contains("Wheat"))
    assertTrue("Should include Tomato", cropIds.contains("Tomato"))
    assertTrue("Should include Paddy", cropIds.contains("Paddy"))
  }

  @Test
  fun `viewModel updates and holds selectedCropContext correctly`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = KisanViewModel(application)

    assertEquals("Wheat", viewModel.selectedCropContext.value)

    viewModel.setSelectedCropContext("Tomato")
    assertEquals("Tomato", viewModel.selectedCropContext.value)

    viewModel.setSelectedCropContext("Paddy")
    assertEquals("Paddy", viewModel.selectedCropContext.value)
  }
}
