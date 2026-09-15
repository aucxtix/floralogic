package com.example.data.model

enum class CropGrowthStage(val displayName: String, val waterDemand: String) {
  GERMINATION("Germination / Seedling", "Low"),
  VEGETATIVE("Vegetative Growth", "Medium"),
  FLOWERING("Flowering", "High (Critical)"),
  FRUITING("Fruiting / Grain Fill", "High"),
  MATURITY("Maturity / Ripening", "Low")
}

data class FarmCrop(
  val id: Long = 0,
  val cropName: String,
  val variety: String,
  val areaAcres: Double,
  val sowingDate: String,
  val growthStage: CropGrowthStage,
  val soilType: String,
  val healthStatus: String = "Good",
  val lastWateredDate: String = "2 days ago",
  val notes: String = ""
)
