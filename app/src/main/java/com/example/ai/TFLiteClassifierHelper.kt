package com.example.ai

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp

/**
 * Result of a single classification inference.
 *
 * @param label The textual category/disease label mapped from the model output.
 * @param confidence Confidence score between 0.0 and 1.0 (probability percentage).
 * @param categoryIndex The index of the prediction in the output tensor.
 * @param inferenceTimeMs Total time taken in milliseconds to execute model inference.
 */
data class ClassificationResult(
  val label: String,
  val confidence: Float,
  val categoryIndex: Int = -1,
  val inferenceTimeMs: Long = 0L
)

/**
 * TFLiteClassifierHelper
 *
 * Helper class for TensorFlow Lite image classification inference.
 * - Loads a `.tflite` model file from the Android assets directory as a memory-mapped buffer.
 * - Parses associated class labels from assets (e.g. `labels.txt`).
 * - Configures TFLite Interpreter options (CPU threads, delegates).
 * - Dynamically adapts to input tensor shape (e.g., 224x224x3, 299x299x3) and data type (FLOAT32 vs UINT8).
 * - Preprocesses incoming CameraX capture Bitmaps (scaling, pixel extraction, normalization).
 * - Executes inference and maps tensor probabilities to human-readable classification labels.
 */
class TFLiteClassifierHelper(
  private val context: Context,
  val modelFileName: String = "crop_disease_model.tflite",
  val labelFileName: String = "labels.txt",
  numThreads: Int = 4,
  val imageMean: Float = 0.0f,
  val imageStd: Float = 255.0f
) : Closeable {

  companion object {
    private const val TAG = "TFLiteClassifierHelper"
    private const val DEFAULT_INPUT_SIZE = 224
    private const val NUM_CHANNELS = 3 // RGB
  }

  private var interpreter: Interpreter? = null
  private val labels: MutableList<String> = mutableListOf()

  var isModelLoaded: Boolean = false
    private set

  // Model input tensor dimensions
  var inputImageWidth: Int = DEFAULT_INPUT_SIZE
    private set
  var inputImageHeight: Int = DEFAULT_INPUT_SIZE
    private set
  var isQuantized: Boolean = false
    private set
  private var outputClassesCount: Int = 0

  init {
    initClassifier(numThreads)
  }

  /**
   * Initializes the TFLite interpreter and loads the label dictionary from assets.
   */
  private fun initClassifier(numThreads: Int) {
    try {
      // 1. Load labels if available
      loadLabels()

      // 2. Load model from assets
      val modelBuffer = loadModelFile(context, modelFileName)

      // 3. Configure Interpreter options
      val options = Interpreter.Options().apply {
        setNumThreads(numThreads)
      }

      // 4. Instantiate Interpreter
      val tfInterpreter = Interpreter(modelBuffer, options)
      interpreter = tfInterpreter

      // 5. Inspect input and output tensor dimensions
      val inputTensor = tfInterpreter.getInputTensor(0)
      val inputShape = inputTensor.shape() // Typically [1, height, width, 3]
      if (inputShape.size == 4) {
        inputImageHeight = inputShape[1]
        inputImageWidth = inputShape[2]
      }
      isQuantized = inputTensor.dataType() == DataType.UINT8

      val outputTensor = tfInterpreter.getOutputTensor(0)
      val outputShape = outputTensor.shape() // Typically [1, num_classes]
      outputClassesCount = if (outputShape.size >= 2) outputShape[1] else labels.size

      isModelLoaded = true
      Log.i(
        TAG,
        "Successfully initialized TFLite model '$modelFileName': input=(${inputImageWidth}x${inputImageHeight}x$NUM_CHANNELS, quantized=$isQuantized), classes=$outputClassesCount"
      )
    } catch (e: Exception) {
      Log.w(TAG, "TFLite model initialization deferred or asset not found: ${e.message}")
      isModelLoaded = false
    }
  }

  /**
   * Loads class labels from the assets folder.
   */
  private fun loadLabels() {
    try {
      context.assets.open(labelFileName).bufferedReader().useLines { lines ->
        labels.clear()
        labels.addAll(lines.map { it.trim() }.filter { it.isNotEmpty() })
      }
      Log.d(TAG, "Loaded ${labels.size} labels from assets/$labelFileName")
    } catch (e: IOException) {
      Log.w(TAG, "Labels file '$labelFileName' not found in assets, will use class indices: ${e.message}")
    }
  }

  /**
   * Memory-maps the .tflite model file directly from the assets folder without copying.
   */
  @Throws(IOException::class)
  fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
    val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelPath)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel: FileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
  }

  /**
   * Runs classification inference on a CameraX captured [Bitmap].
   *
   * @param bitmap The source image bitmap from CameraX image capture or gallery.
   * @return The highest-scoring [ClassificationResult], or null if inference failed or model not loaded.
   */
  fun classify(bitmap: Bitmap): ClassificationResult? {
    val results = classifyTopK(bitmap, topK = 1)
    return results.firstOrNull()
  }

  /**
   * Runs inference on the input bitmap and returns the top K most confident predictions.
   *
   * @param bitmap The source CameraX capture bitmap.
   * @param topK Number of top results to return (default 3).
   * @return Ordered list of [ClassificationResult] sorted from highest to lowest confidence.
   */
  fun classifyTopK(bitmap: Bitmap, topK: Int = 3): List<ClassificationResult> {
    val activeInterpreter = interpreter
    if (activeInterpreter == null || !isModelLoaded) {
      Log.e(TAG, "Cannot run inference: Interpreter is not initialized or model is not loaded.")
      return emptyList()
    }

    try {
      // 1. Preprocess Bitmap to match TFLite input dimensions
      val inputBuffer = preprocessBitmap(bitmap)

      // 2. Prepare output probability buffer
      val classesCount = if (outputClassesCount > 0) outputClassesCount else maxOf(labels.size, 1)
      val outputBuffer = Array(1) { FloatArray(classesCount) }

      // 3. Execute inference and benchmark timing
      val startTime = SystemClock.uptimeMillis()
      activeInterpreter.run(inputBuffer, outputBuffer)
      val inferenceTime = SystemClock.uptimeMillis() - startTime

      // 4. Post-process probabilities
      val probabilities = outputBuffer[0]
      val normalizedScores = ensureSoftmax(probabilities)

      // 5. Build and rank results
      val results = normalizedScores.mapIndexed { index, score ->
        val label = if (index < labels.size) labels[index] else "Class #$index"
        ClassificationResult(
          label = label,
          confidence = score,
          categoryIndex = index,
          inferenceTimeMs = inferenceTime
        )
      }
        .sortedByDescending { it.confidence }
        .take(topK)

      return results
    } catch (e: Exception) {
      Log.e(TAG, "Inference failed on input bitmap", e)
      return emptyList()
    }
  }

  /**
   * Prepares and normalizes the input bitmap into a direct [ByteBuffer] expected by the model.
   */
  fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
    val bytesPerChannel = if (isQuantized) 1 else 4
    val byteBuffer = ByteBuffer.allocateDirect(1 * inputImageWidth * inputImageHeight * NUM_CHANNELS * bytesPerChannel).apply {
      order(ByteOrder.nativeOrder())
    }

    // Resize input bitmap to target model dimensions
    val resizedBitmap = if (bitmap.width == inputImageWidth && bitmap.height == inputImageHeight) {
      bitmap
    } else {
      Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true)
    }

    val pixels = IntArray(inputImageWidth * inputImageHeight)
    resizedBitmap.getPixels(pixels, 0, inputImageWidth, 0, 0, inputImageWidth, inputImageHeight)

    // Clean up temporary scaled bitmap if created
    if (resizedBitmap != bitmap) {
      resizedBitmap.recycle()
    }

    // Populate byte buffer with RGB color channels
    for (pixel in pixels) {
      val r = (pixel shr 16) and 0xFF
      val g = (pixel shr 8) and 0xFF
      val b = pixel and 0xFF

      if (isQuantized) {
        byteBuffer.put(r.toByte())
        byteBuffer.put(g.toByte())
        byteBuffer.put(b.toByte())
      } else {
        // Normalize pixel channel: (pixel - mean) / std (e.g. 0.0f to 1.0f)
        byteBuffer.putFloat((r - imageMean) / imageStd)
        byteBuffer.putFloat((g - imageMean) / imageStd)
        byteBuffer.putFloat((b - imageMean) / imageStd)
      }
    }

    byteBuffer.rewind()
    return byteBuffer
  }

  /**
   * Applies numerically stable Softmax normalization if raw logits are returned by the model.
   */
  private fun ensureSoftmax(scores: FloatArray): FloatArray {
    val sum = scores.sum()
    // If output already resembles probabilities summing to ~1.0, return as-is
    if (sum in 0.95f..1.05f && scores.all { it in 0.0f..1.0f }) {
      return scores
    }

    // Otherwise apply softmax over raw logits
    val maxScore = scores.maxOrNull() ?: 0f
    val expScores = scores.map { exp((it - maxScore).toDouble()).toFloat() }
    val expSum = expScores.sum()
    return if (expSum > 0f) {
      expScores.map { it / expSum }.toFloatArray()
    } else {
      scores
    }
  }

  /**
   * Releases TFLite native interpreter resources.
   */
  override fun close() {
    interpreter?.close()
    interpreter = null
    isModelLoaded = false
    Log.d(TAG, "TFLite interpreter closed and resources released.")
  }
}
