package com.example.ai

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs

/**
 * Image Quality Assessment result.
 */
sealed class ImageQualityAssessment {
    object HighQuality : ImageQualityAssessment()
    
    data class Insufficient(
        val reasons: List<String>,
        val recommendations: List<String>,
        val metrics: QualityMetrics
    ) : ImageQualityAssessment()
}

/**
 * Quantitative quality metrics extracted from the leaf bitmap.
 */
data class QualityMetrics(
    val averageLuminance: Float,
    val greenPixelRatio: Float,
    val sharpnessVariance: Float,
    val width: Int,
    val height: Int,
    val isOverexposed: Boolean,
    val isUnderexposed: Boolean,
    val isLowResolution: Boolean,
    val isBlurry: Boolean,
    val isFoliageMissing: Boolean
)

/**
 * ImageQualityModel
 *
 * Pre-inference validation for crop leaf scans.
 * Detects blur, low-light, overexposure, low resolution, and lack of foliar matter.
 * Prevents confident false diagnoses on poor quality images.
 */
object ImageQualityModel {

    private const val MIN_DIMENSION = 200
    private const val MIN_LUMINANCE = 35f // Below this is underexposed/too dark
    private const val MAX_LUMINANCE = 230f // Above this is overexposed/glare
    private const val MIN_GREEN_CHLOROPHYLL_RATIO = 0.12f // At least 12% pixels should have vegetation green hue
    private const val MIN_SHARPNESS_SCORE = 12f // Proxy threshold for high-frequency edge variance

    fun assessQuality(bitmap: Bitmap?): ImageQualityAssessment {
        if (bitmap == null) {
            return ImageQualityAssessment.Insufficient(
                reasons = listOf("No image provided"),
                recommendations = listOf("Please capture or select a photo of the crop leaf."),
                metrics = QualityMetrics(0f, 0f, 0f, 0, 0, false, true, true, true, true)
            )
        }

        val width = bitmap.width
        val height = bitmap.height
        val isLowResolution = width < MIN_DIMENSION || height < MIN_DIMENSION

        // Sample down for fast CPU inspection if image is large
        val sampleSize = 100
        val sampledBitmap = if (width > sampleSize || height > sampleSize) {
            Bitmap.createScaledBitmap(bitmap, sampleSize, sampleSize, false)
        } else {
            bitmap
        }

        val sampledW = sampledBitmap.width
        val sampledH = sampledBitmap.height
        val totalPixels = sampledW * sampledH
        val pixels = IntArray(totalPixels)
        sampledBitmap.getPixels(pixels, 0, sampledW, 0, 0, sampledW, sampledH)

        if (sampledBitmap != bitmap) {
            sampledBitmap.recycle()
        }

        var totalLuminance = 0L
        var greenPixelCount = 0
        var edgeContrastAccumulator = 0L

        for (i in 0 until totalPixels) {
            val pixel = pixels[i]
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            // Perceived luminance formula (ITU-R BT.601)
            val lum = (0.299f * r + 0.587f * g + 0.114f * b).toInt()
            totalLuminance += lum

            // Foliar green check: green exceeds red & blue significantly, or typical leaf chlorosis/lesion tone
            val isGreenLeaf = (g > r * 0.9f && g > b * 0.95f && g > 40) ||
                    (r > 80 && g > 60 && b < 70) // Brown/yellow lesion pixels
            if (isGreenLeaf) {
                greenPixelCount++
            }

            // Neighbor horizontal difference for high-frequency edge proxy
            if (i % sampledW < sampledW - 1) {
                val nextPixel = pixels[i + 1]
                val nextLum = (0.299f * Color.red(nextPixel) + 0.587f * Color.green(nextPixel) + 0.114f * Color.blue(nextPixel)).toInt()
                edgeContrastAccumulator += abs(lum - nextLum)
            }
        }

        val avgLuminance = totalLuminance.toFloat() / totalPixels
        val greenRatio = greenPixelCount.toFloat() / totalPixels
        val edgeSharpness = edgeContrastAccumulator.toFloat() / totalPixels

        val isUnderexposed = avgLuminance < MIN_LUMINANCE
        val isOverexposed = avgLuminance > MAX_LUMINANCE
        val isBlurry = edgeSharpness < MIN_SHARPNESS_SCORE
        val isFoliageMissing = greenRatio < MIN_GREEN_CHLOROPHYLL_RATIO

        val reasons = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        if (isLowResolution) {
            reasons.add("Image resolution is too low ($width x $height).")
            recommendations.add("Move closer to the crop leaf so it fills the screen.")
        }

        if (isUnderexposed) {
            reasons.add("Image is too dark / low light (luminance: ${avgLuminance.toInt()}).")
            recommendations.add("Use natural daylight or avoid capturing in heavy shadows.")
        }

        if (isOverexposed) {
            reasons.add("Image has severe glare or overexposure (luminance: ${avgLuminance.toInt()}).")
            recommendations.add("Angle the camera away from direct harsh sunlight glare.")
        }

        if (isFoliageMissing) {
            reasons.add("Crop leaf not clearly visible (only ${(greenRatio * 100).toInt()}% foliage detected).")
            recommendations.add("Center the leaf directly in the frame and avoid capturing excess soil or background objects.")
        }

        if (isBlurry && !isUnderexposed) {
            reasons.add("Image appears blurry or out of focus.")
            recommendations.add("Hold the camera steady and tap the screen to focus on the leaf lesion.")
        }

        val metrics = QualityMetrics(
            averageLuminance = avgLuminance,
            greenPixelRatio = greenRatio,
            sharpnessVariance = edgeSharpness,
            width = width,
            height = height,
            isOverexposed = isOverexposed,
            isUnderexposed = isUnderexposed,
            isLowResolution = isLowResolution,
            isBlurry = isBlurry,
            isFoliageMissing = isFoliageMissing
        )

        return if (reasons.isEmpty()) {
            ImageQualityAssessment.HighQuality
        } else {
            // Add standard actionable guidance
            if (!recommendations.contains("Capture both healthy and affected leaf areas for accurate severity estimation.")) {
                recommendations.add("Capture both healthy and affected leaf areas for accurate severity estimation.")
            }
            ImageQualityAssessment.Insufficient(
                reasons = reasons,
                recommendations = recommendations,
                metrics = metrics
            )
        }
    }
}
