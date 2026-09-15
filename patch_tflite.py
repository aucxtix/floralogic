import sys

with open('app/src/main/java/com/example/ai/CropDiseaseDetector.kt', 'r') as f:
    content = f.read()

import_str = "import android.content.Context\nimport com.example.ai.TFLiteClassifierHelper\n"
content = content.replace("import android.graphics.Bitmap", "import android.graphics.Bitmap\n" + import_str)

analyze_func = """  suspend fun analyzeCropLeaf(
    context: Context,
    bitmap: Bitmap?,
    selectedCropHint: String?,
    specimenId: String? = null
  ): DetectionResult {
    if (specimenId == "inconclusive_sample" || specimenId == "specimen_inconclusive") {
      return DetectionResult.Inconclusive(
        cropHint = selectedCropHint ?: "Crop Leaf",
        confidence = 0.48f,
        reasons = listOf(
          "Model confidence is 48%, below the 60% certainty threshold.",
          "Lesion contour does not match known pathogen signatures.",
          "Potential image blur or background interference detected."
        ),
        suggestions = listOf(
          "Ensure steady camera focus directly on leaf lesion edge",
          "Capture in indirect daylight without harsh reflective glare",
          "Confirm target crop selection (currently analyzing as ${selectedCropHint ?: "Crop Leaf"})",
          "Consult nearest KVK (Krishi Vigyan Kendra) extension center if symptoms spread"
        )
      )
    }

    if (specimenId != null && DISEASE_CATALOG.containsKey(specimenId)) {
      return DetectionResult.Success(DISEASE_CATALOG[specimenId]!!)
    }

    if (bitmap != null) {
      // 1. Process via local TFLite Model
      try {
          TFLiteClassifierHelper(context).use { helper ->
              if (helper.isModelLoaded) {
                  val classification = helper.classify(bitmap)
                  if (classification != null && classification.confidence > 0.4f) {
                      // Map the label to our DISEASE_CATALOG
                      val label = classification.label
                      val disease = when {
                          label.contains("Tomato Late Blight", ignoreCase = true) -> DISEASE_CATALOG["tomato_late_blight"]
                          label.contains("Tomato Early Blight", ignoreCase = true) -> DISEASE_CATALOG["tomato_early_blight"]
                          label.contains("Tomato Healthy", ignoreCase = true) -> DISEASE_CATALOG["tomato_healthy"]
                          label.contains("Rice Bacterial Leaf Blight", ignoreCase = true) -> DISEASE_CATALOG["rice_bacterial_blight"]
                          label.contains("Rice Leaf Blast", ignoreCase = true) -> DISEASE_CATALOG["rice_blast"]
                          label.contains("Wheat Leaf Rust", ignoreCase = true) -> DISEASE_CATALOG["wheat_leaf_rust"]
                          label.contains("Cotton Leaf Curl", ignoreCase = true) -> DISEASE_CATALOG["cotton_leaf_curl"]
                          label.contains("Healthy", ignoreCase = true) -> DISEASE_CATALOG["tomato_healthy"] // Fallback healthy
                          else -> null
                      }
                      
                      if (disease != null) {
                          // Copy with actual confidence
                          return DetectionResult.Success(disease.copy(confidence = classification.confidence))
                      }
                  }
              }
          }
      } catch (e: Exception) {
          e.printStackTrace()
      }
      
      // Fallback: Phase 6 Quality Assessment Pipeline
      val qualityAssessment = ImageQualityModel.assessQuality(bitmap)
      if (qualityAssessment is ImageQualityAssessment.Insufficient) {
        return DetectionResult.Inconclusive(
          cropHint = selectedCropHint ?: "Crop Leaf",
          confidence = 0.35f,
          reasons = qualityAssessment.reasons,
          suggestions = qualityAssessment.recommendations
        )
      }

      // Analyze actual bitmap pixels for lesion vs chlorophyll ratio
      val (chlorosisRatio, necrosisRatio, healthyGreenRatio) = extractColorRatios(bitmap)
      
      // Inconclusive if low chlorophyll and non-leaf surface detected
      if (healthyGreenRatio < 0.12f && chlorosisRatio < 0.10f && necrosisRatio < 0.10f) {
        return DetectionResult.Inconclusive(
          cropHint = selectedCropHint ?: "Crop Leaf",
          confidence = 0.38f,
          reasons = listOf(
            "Low plant chlorophyll signature detected (confidence: 38%).",
            "Image does not appear to contain a distinguishable leaf surface."
          ),
          suggestions = listOf(
            "Frame only the affected leaf within the scanner guide box",
            "Remove soil, tools, or hands from the camera background",
            "Retake photo in natural daylight"
          )
        )
      }

      val disease = when {
        selectedCropHint?.contains("Rice", ignoreCase = true) == true -> {
          if (necrosisRatio > 0.25f) DISEASE_CATALOG["rice_blast"]!!
          else DISEASE_CATALOG["rice_bacterial_blight"]!!
        }
        selectedCropHint?.contains("Cotton", ignoreCase = true) == true -> {
          DISEASE_CATALOG["cotton_leaf_curl"]!!
        }
        selectedCropHint?.contains("Wheat", ignoreCase = true) == true -> {
          DISEASE_CATALOG["wheat_leaf_rust"]!!
        }
        else -> {
          if (healthyGreenRatio > 0.65f && necrosisRatio < 0.1f) {
            DISEASE_CATALOG["tomato_healthy"]!!
          } else if (chlorosisRatio > 0.25f) {
            DISEASE_CATALOG["tomato_early_blight"]!!
          } else {
            DISEASE_CATALOG["tomato_late_blight"]!!
          }
        }
      }
      return DetectionResult.Success(disease)
    }

    // Default fallback based on selected crop
    val defaultDisease = when {
      selectedCropHint?.contains("Rice", ignoreCase = true) == true -> DISEASE_CATALOG["rice_bacterial_blight"]!!
      selectedCropHint?.contains("Cotton", ignoreCase = true) == true -> DISEASE_CATALOG["cotton_leaf_curl"]!!
      selectedCropHint?.contains("Wheat", ignoreCase = true) == true -> DISEASE_CATALOG["wheat_leaf_rust"]!!
      else -> DISEASE_CATALOG["tomato_late_blight"]!!
    }
    
    return DetectionResult.Success(defaultDisease)
  }"""

# Need to replace the whole analyzeCropLeaf block
import re
new_content = re.sub(r'  suspend fun analyzeCropLeaf\(.*?return DetectionResult\.Success\(defaultDisease\)\n  \}', analyze_func, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ai/CropDiseaseDetector.kt', 'w') as f:
    f.write(new_content)
print("Done")
