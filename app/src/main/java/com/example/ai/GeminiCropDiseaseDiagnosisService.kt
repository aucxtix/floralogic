package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CropDisease
import com.example.data.model.DiseaseSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object GeminiCropDiseaseDiagnosisService {
  private const val TAG = "GeminiCropDiagnosis"
  private const val MODEL_NAME = "gemini-3.5-flash"

  /**
   * Diagnoses leaf diseases using Gemini 3.5 Flash Vision AI.
   * Takes a captured bitmap from CameraX and the selected crop context.
   */
  suspend fun diagnoseLeafWithGemini(
    bitmap: Bitmap,
    cropHint: String?,
    apiKey: String = BuildConfig.GEMINI_API_KEY
  ): DetectionResult? = withContext(Dispatchers.IO) {
    if (apiKey.isBlank()) {
      Log.w(TAG, "GEMINI_API_KEY is blank. Falling back to local classifier.")
      return@withContext null
    }

    try {
      // Scale bitmap if needed to conserve memory and network
      val scaledBitmap = scaleBitmapIfNeeded(bitmap, maxDimension = 1024)

      val targetCrop = cropHint?.takeIf { it.isNotBlank() } ?: "Crop Leaf"

      val systemInstruction = """
        You are an expert AI Agricultural Plant Pathologist and Senior Agronomist at Kisan AI.
        Your task is to analyze close-up photos of crop leaves captured by farmers in the field.
        Diagnose whether the leaf exhibits any plant disease, fungal or bacterial blight, virus, pest infestation, nutrient chlorosis, or is healthy.
        Always provide practical, scientifically accurate, and farmer-friendly advice.
        Return your diagnosis strictly as a single JSON object.
      """.trimIndent()

      val prompt = """
        Analyze this leaf image for $targetCrop.
        Identify any plant disease, pathogen, or confirm if the leaf is healthy.
        Respond ONLY with a JSON object in this exact schema without markdown formatting or code fences:
        {
          "cropName": "$targetCrop",
          "diseaseName": "Name of disease in English (Common Hindi name in parentheses)",
          "scientificName": "Scientific binomial name of pathogen or botanical name if healthy",
          "isHealthy": false,
          "confidence": 0.92,
          "severity": "HIGH",
          "symptoms": [
            "Water-soaked dark lesions",
            "Chlorotic halo surrounding brown spots"
          ],
          "organicTreatment": "Bio-fungicide or botanical remedy (e.g., Trichoderma viride 5g/L or 5% Neem Seed Kernel Extract)",
          "chemicalTreatment": "Approved chemical fungicide/bactericide (e.g., Mancozeb 75% WP or Copper Oxychloride 50% WP)",
          "dosage": "Recommended dosage in grams/ml per Liter of water and per acre",
          "estimatedCostInr": "Estimated treatment cost range per acre in INR (e.g., ₹180 - ₹260 / acre)",
          "preventiveMeasures": [
            "Improve drainage and avoid waterlogging",
            "Use certified disease-resistant seeds",
            "Maintain crop rotation"
          ],
          "adviceHindi": "किसान के लिए स्पष्ट और सरल सलाह हिंदी में",
          "adviceGujarati": "ખેડૂત માટે સ્પષ્ટ અને સરળ સલાહ ગુજરાતીમાં"
        }
        Allowed values for severity: "NONE", "LOW", "MEDIUM", "HIGH".
      """.trimIndent()

      Log.d(TAG, "Calling Gemini 3.5 Flash Vision for crop: $targetCrop")
      val rawResponse = GeminiApiClient.generateContentWithImage(
        apiKey = apiKey,
        prompt = prompt,
        bitmap = scaledBitmap,
        systemInstruction = systemInstruction,
        temperature = 0.15f
      )

      if (rawResponse.isNullOrBlank()) {
        Log.w(TAG, "Gemini returned empty response")
        return@withContext null
      }

      Log.d(TAG, "Received Gemini response: ${rawResponse.take(150)}...")
      parseGeminiResponse(rawResponse, targetCrop)
    } catch (e: Exception) {
      Log.e(TAG, "Gemini leaf diagnosis failed", e)
      null
    }
  }

  private fun parseGeminiResponse(rawText: String, fallbackCrop: String): DetectionResult? {
    try {
      val jsonString = cleanJsonFences(rawText)
      val json = JSONObject(jsonString)

      val cropName = json.optString("cropName", fallbackCrop)
      val diseaseName = json.optString("diseaseName", "Leaf Disease Detected")
      val scientificName = json.optString("scientificName", "Pathogen sp.")
      val isHealthy = json.optBoolean("isHealthy", false)
      val confidence = json.optDouble("confidence", 0.91).toFloat().coerceIn(0.5f, 0.99f)

      val severityStr = json.optString("severity", "MEDIUM").uppercase()
      val severity = when {
        isHealthy -> DiseaseSeverity.NONE
        severityStr.contains("HIGH") || severityStr.contains("SEVERE") -> DiseaseSeverity.HIGH
        severityStr.contains("LOW") || severityStr.contains("MILD") -> DiseaseSeverity.LOW
        else -> DiseaseSeverity.MEDIUM
      }

      val symptomsList = mutableListOf<String>()
      val symptomsArr = json.optJSONArray("symptoms")
      if (symptomsArr != null) {
        for (i in 0 until symptomsArr.length()) {
          symptomsList.add(symptomsArr.getString(i))
        }
      }
      if (symptomsList.isEmpty()) {
        symptomsList.add("Visual foliar discoloration and pathogen lesions observed on leaf.")
      }

      val preventiveList = mutableListOf<String>()
      val prevArr = json.optJSONArray("preventiveMeasures")
      if (prevArr != null) {
        for (i in 0 until prevArr.length()) {
          preventiveList.add(prevArr.getString(i))
        }
      }
      if (preventiveList.isEmpty()) {
        preventiveList.add("Maintain balanced soil nutrition and regular farm monitoring.")
      }

      val organicTreatment = json.optString(
        "organicTreatment",
        "Apply Trichoderma viride bio-agent @ 5g/L + Neem oil 5ml/L."
      )
      val chemicalTreatment = json.optString(
        "chemicalTreatment",
        "Spray Mancozeb 75% WP or Copper Oxychloride 50% WP."
      )
      val dosage = json.optString("dosage", "2.0 - 2.5 g/L water (400-500g / acre in 200L water)")
      val estimatedCostInr = json.optString("estimatedCostInr", "₹180 - ₹250 / acre")
      val adviceHindi = json.optString(
        "adviceHindi",
        "लक्षणों वाले पत्तों को अलग करें और अनुशंसित दवा का तुरंत छिड़काव करें।"
      )
      val adviceGujarati = json.optString(
        "adviceGujarati",
        "રોગગ્રસ્ત પાંદડા દૂર કરો અને ભલામણ કરેલ દવાનો વહેલી સવારે છંટકાવ કરો."
      )

      val disease = CropDisease(
        id = "gemini_${System.currentTimeMillis()}",
        cropName = cropName,
        diseaseName = diseaseName,
        scientificName = scientificName,
        isHealthy = isHealthy,
        confidence = confidence,
        severity = severity,
        symptoms = symptomsList,
        organicTreatment = organicTreatment,
        chemicalTreatment = chemicalTreatment,
        dosage = dosage,
        estimatedCostInr = estimatedCostInr,
        preventiveMeasures = preventiveList,
        adviceHindi = adviceHindi,
        adviceGujarati = adviceGujarati,
        modelSource = "Gemini 3.5 Flash Vision AI"
      )

      return DetectionResult.Success(disease)
    } catch (e: Exception) {
      Log.e(TAG, "Error parsing Gemini diagnosis JSON", e)
      return null
    }
  }

  private fun cleanJsonFences(text: String): String {
    var trimmed = text.trim()
    if (trimmed.startsWith("```json", ignoreCase = true)) {
      trimmed = trimmed.substring(7)
    } else if (trimmed.startsWith("```")) {
      trimmed = trimmed.substring(3)
    }
    if (trimmed.endsWith("```")) {
      trimmed = trimmed.substring(0, trimmed.length - 3)
    }
    return trimmed.trim()
  }

  private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= maxDimension && height <= maxDimension) return bitmap

    val ratio = width.toFloat() / height.toFloat()
    val targetWidth: Int
    val targetHeight: Int
    if (width > height) {
      targetWidth = maxDimension
      targetHeight = (maxDimension / ratio).toInt()
    } else {
      targetHeight = maxDimension
      targetWidth = (maxDimension * ratio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
  }
}
