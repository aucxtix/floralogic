package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * FileUploadValidator
 *
 * Implements strict security validations for user-provided files and gallery uploads:
 * 1. Deep content inspection via magic bytes (validates true binary signature, not file extensions or names).
 * 2. Strict file size cap (default 10MB) to mitigate DoS and Out-Of-Memory attacks.
 * 3. Decompression bomb protection via dimension bounding.
 * 4. Isolated sandboxed storage outside web/public roots with non-executable permissions.
 * 5. Deterministic UUID naming to prevent path traversal and script execution.
 */
object FileUploadValidator {
  private const val TAG = "FileUploadValidator"

  // 10 Megabytes maximum upload size cap
  const val DEFAULT_MAX_SIZE_BYTES = 10 * 1024 * 1024L // 10MB
  const val MAX_IMAGE_DIMENSION = 8192 // 8K resolution maximum

  sealed class ValidationResult {
    data class Success(
      val bitmap: Bitmap,
      val sanitizedFile: File,
      val mimeType: String,
      val sizeBytes: Long
    ) : ValidationResult()

    data class Error(val message: String) : ValidationResult()
  }

  enum class DetectedMimeType(val mime: String, val extension: String) {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp")
  }

  /**
   * Validates file size, inspects magic bytes, checks dimensions, and safely stores
   * the file in a sandboxed, non-executable private directory.
   */
  fun validateAndSanitizeImage(
    context: Context,
    uri: Uri,
    maxSizeBytes: Long = DEFAULT_MAX_SIZE_BYTES
  ): ValidationResult {
    try {
      val contentResolver = context.contentResolver

      // 1. File Size Cap Validation
      val fileSize = try {
        contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: -1L
      } catch (_: Exception) {
        -1L
      }

      if (fileSize > maxSizeBytes) {
        return ValidationResult.Error("File exceeds maximum allowed size of ${maxSizeBytes / (1024 * 1024)}MB.")
      }

      // 2. Magic Bytes Content Inspection
      val detectedMime = contentResolver.openInputStream(uri)?.use { stream ->
        detectMagicBytes(stream)
      } ?: return ValidationResult.Error("Unable to read uploaded file header.")

      if (detectedMime == null) {
        return ValidationResult.Error("Invalid or unsupported file format. Only legitimate JPEG, PNG, and WebP images are permitted.")
      }

      // 3. Dimension Check & Decompression Bomb Protection
      val boundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
      }
      contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, boundsOptions)
      }

      if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) {
        return ValidationResult.Error("Corrupt or invalid image content.")
      }

      if (boundsOptions.outWidth > MAX_IMAGE_DIMENSION || boundsOptions.outHeight > MAX_IMAGE_DIMENSION) {
        return ValidationResult.Error("Image resolution exceeds safe limits (${MAX_IMAGE_DIMENSION}x${MAX_IMAGE_DIMENSION}).")
      }

      // 4. Safe Decoding
      val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(boundsOptions, 1920, 1080)
        inPreferredConfig = Bitmap.Config.ARGB_8888
      }
      val decodedBitmap = contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, decodeOptions)
      } ?: return ValidationResult.Error("Failed to decode image data safely.")

      // 5. Isolated Sandboxed Storage (Never in Web Root / Never Executable)
      val uploadDir = File(context.filesDir, "sandboxed_uploads").apply {
        if (!exists()) {
          mkdirs()
          setReadable(true, true)
          setWritable(true, true)
          setExecutable(false, false) // Explicitly non-executable
        }
      }

      // Generate random UUID filename to completely discard user-supplied filenames and path traversal
      val safeFileName = "leaf_${UUID.randomUUID()}.${detectedMime.extension}"
      val destinationFile = File(uploadDir, safeFileName)

      FileOutputStream(destinationFile).use { outStream ->
        when (detectedMime) {
          DetectedMimeType.JPEG -> decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
          DetectedMimeType.PNG -> decodedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
          DetectedMimeType.WEBP -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
              decodedBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, outStream)
            } else {
              @Suppress("DEPRECATION")
              decodedBitmap.compress(Bitmap.CompressFormat.WEBP, 90, outStream)
            }
          }
        }
      }

      // Lock down permissions on the created file
      destinationFile.setReadable(true, true)
      destinationFile.setWritable(true, true)
      destinationFile.setExecutable(false, false) // Explicitly non-executable

      return ValidationResult.Success(
        bitmap = decodedBitmap,
        sanitizedFile = destinationFile,
        mimeType = detectedMime.mime,
        sizeBytes = destinationFile.length()
      )
    } catch (e: Exception) {
      Log.e(TAG, "File validation error", e)
      return ValidationResult.Error("Failed to process uploaded file securely: ${e.localizedMessage}")
    }
  }

  /**
   * Inspects binary magic numbers from the first 12 bytes of an InputStream.
   * Prevents SVG script injection, HTML disguised as image, and executable binaries.
   */
  fun detectMagicBytes(stream: InputStream): DetectedMimeType? {
    val header = ByteArray(12)
    val bytesRead = stream.read(header, 0, 12)
    if (bytesRead < 4) return null

    // JPEG: FF D8 FF
    if (header[0] == 0xFF.toByte() && header[1] == 0xD8.toByte() && header[2] == 0xFF.toByte()) {
      return DetectedMimeType.JPEG
    }

    // PNG: 89 50 4E 47 0D 0A 1A 0A
    if (bytesRead >= 8 &&
      header[0] == 0x89.toByte() && header[1] == 0x50.toByte() &&
      header[2] == 0x4E.toByte() && header[3] == 0x47.toByte() &&
      header[4] == 0x0D.toByte() && header[5] == 0x0A.toByte() &&
      header[6] == 0x1A.toByte() && header[7] == 0x0A.toByte()
    ) {
      return DetectedMimeType.PNG
    }

    // WebP: RIFF .... WEBP
    if (bytesRead >= 12 &&
      header[0] == 'R'.code.toByte() && header[1] == 'I'.code.toByte() &&
      header[2] == 'F'.code.toByte() && header[3] == 'F'.code.toByte() &&
      header[8] == 'W'.code.toByte() && header[9] == 'E'.code.toByte() &&
      header[10] == 'B'.code.toByte() && header[11] == 'P'.code.toByte()
    ) {
      return DetectedMimeType.WEBP
    }

    return null
  }

  private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
  ): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
      val halfHeight = height / 2
      val halfWidth = width / 2
      while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2
      }
    }
    return inSampleSize
  }
}
