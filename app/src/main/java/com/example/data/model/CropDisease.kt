package com.example.data.model

enum class DiseaseSeverity(val label: String, val level: Int) {
  NONE("Healthy", 0),
  LOW("Mild (0-15%)", 1),
  MEDIUM("Moderate (15-40%)", 2),
  HIGH("Severe (>40%)", 3)
}

data class CropDisease(
  val id: String,
  val cropName: String,
  val diseaseName: String,
  val scientificName: String,
  val isHealthy: Boolean = false,
  val confidence: Float,
  val severity: DiseaseSeverity,
  val symptoms: List<String>,
  val organicTreatment: String,
  val chemicalTreatment: String,
  val dosage: String,
  val estimatedCostInr: String,
  val preventiveMeasures: List<String>,
  val adviceHindi: String,
  val adviceGujarati: String,
  val modelSource: String = "Gemini 3.5 Flash Vision AI"
)

data class SampleSpecimen(
  val id: String,
  val cropName: String,
  val diseaseName: String,
  val description: String,
  val simulatedDiseaseId: String
)
