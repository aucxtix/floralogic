package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.ai.ClassificationResult
import com.example.ai.TFLiteClassifierHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TFLiteClassifierHelperTest {

  @Test
  fun `preprocessBitmap correctly converts Bitmap to direct ByteBuffer with expected dimensions`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val helper = TFLiteClassifierHelper(context, modelFileName = "non_existent.tflite")

    // Verify fallback properties
    assertFalse(helper.isModelLoaded)
    assertEquals(224, helper.inputImageWidth)
    assertEquals(224, helper.inputImageHeight)
    assertFalse(helper.isQuantized)

    // Create a 100x100 RGB Bitmap
    val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    val buffer = helper.preprocessBitmap(testBitmap)

    // Expected bytes for float32: 1 * 224 * 224 * 3 * 4 = 602,112 bytes
    val expectedBytes = 1 * 224 * 224 * 3 * 4
    assertEquals(expectedBytes, buffer.capacity())
    assertEquals(0, buffer.position())
    assertEquals(expectedBytes, buffer.remaining())

    helper.close()
  }

  @Test
  fun `classification result data class preserves properties correctly`() {
    val result = ClassificationResult(
      label = "Tomato Early Blight",
      confidence = 0.94f,
      categoryIndex = 0,
      inferenceTimeMs = 18L
    )

    assertEquals("Tomato Early Blight", result.label)
    assertEquals(0.94f, result.confidence, 0.001f)
    assertEquals(0, result.categoryIndex)
    assertEquals(18L, result.inferenceTimeMs)
  }

  @Test
  fun `classify returns null and empty list gracefully when model is not loaded`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val helper = TFLiteClassifierHelper(context, modelFileName = "non_existent.tflite")

    val testBitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
    val result = helper.classify(testBitmap)
    val topResults = helper.classifyTopK(testBitmap, topK = 3)

    assertEquals(null, result)
    assertTrue(topResults.isEmpty())

    helper.close()
  }
}
