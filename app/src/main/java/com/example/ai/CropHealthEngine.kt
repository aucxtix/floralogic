package com.example.ai

/**
 * Health assessment of the standing crop.
 */
data class CropHealthAnalysis(
    val healthScore: Int, // 0 to 100
    val healthCategory: String, // "Optimal", "Good", "Needs Attention", "Critical Stress"
    val trend: String, // "Improving", "Stable", "Declining"
    val primaryStress: String, // "Water Stress", "Foliar Disease", "Heat Stress", "Nutrient Deficit", "None"
    val contributingFactors: List<HealthFactorBreakdown>,
    val explanationFarmerLanguage: String
)

data class HealthFactorBreakdown(
    val factorName: String,
    val score: Int, // 0 to 100
    val weightPercent: Int,
    val status: String,
    val observation: String
)

/**
 * CropHealthEngine
 *
 * Synthesizes multi-source data: satellite vegetative index (NDVI), on-ground IoT sensors,
 * disease scan logs, weather conditions, and crop growth stage vulnerability.
 */
object CropHealthEngine {

    fun evaluateCropHealth(state: DigitalFarmState): CropHealthAnalysis {
        var cropScore = 80
        val factors = mutableListOf<HealthFactorBreakdown>()

        // 1. Satellite Factor (Weight 25%)
        val ndvi = state.satelliteData.ndvi
        val satelliteScore = when {
            ndvi >= 0.75f -> 95
            ndvi >= 0.65f -> 82
            ndvi >= 0.50f -> 68
            else -> 45
        }
        factors.add(
            HealthFactorBreakdown(
                factorName = "Satellite Canopy Vigor",
                score = satelliteScore,
                weightPercent = 25,
                status = if (satelliteScore >= 80) "Healthy" else "Canopy Stress",
                observation = "Canopy index (NDVI: $ndvi) indicates ${state.satelliteData.vegetationHealth.lowercase()} across plot."
            )
        )

        // 2. Soil Moisture / IoT Factor (Weight 30%)
        val moisture = state.sensorData.soilMoisturePercent
        val targetMin = state.soil.targetMoistureMin
        val targetMax = state.soil.targetMoistureMax
        val moistureScore = when {
            moisture in targetMin..targetMax -> 95
            moisture in (targetMin - 6)..(targetMax + 8) -> 74
            moisture < targetMin - 6 -> 48 // Severe water deficit
            else -> 60 // Saturated/waterlogged
        }
        val moistureObservation = when {
            moisture < targetMin -> "Soil moisture ($moisture%) is below optimal target ($targetMin-$targetMax%). Crop experiences moisture stress."
            moisture > targetMax -> "Soil moisture ($moisture%) is saturated above normal root respiration levels."
            else -> "Soil moisture ($moisture%) is within optimal target root zone range."
        }
        factors.add(
            HealthFactorBreakdown(
                factorName = "Root Zone Moisture",
                score = moistureScore,
                weightPercent = 30,
                status = if (moistureScore >= 80) "Optimal" else "Water Stress",
                observation = moistureObservation
            )
        )

        // 3. Pathology / Disease Factor (Weight 25%)
        val activeInfections = state.recentScans.take(3).filter { !it.isHealthy }
        val diseaseScore = when {
            activeInfections.isEmpty() -> 96
            activeInfections.any { it.severity.contains("Severe", ignoreCase = true) || it.severity.contains("High", ignoreCase = true) } -> 42
            activeInfections.any { it.severity.contains("Moderate", ignoreCase = true) } -> 65
            else -> 80
        }
        val diseaseObs = if (activeInfections.isEmpty()) {
            "No active fungal or bacterial pathogens detected on recent scans."
        } else {
            "Active infection: ${activeInfections.first().diseaseName} (${activeInfections.first().severity}) on foliage."
        }
        factors.add(
            HealthFactorBreakdown(
                factorName = "Foliar Pathology",
                score = diseaseScore,
                weightPercent = 25,
                status = if (diseaseScore >= 80) "Clear" else "Infected",
                observation = diseaseObs
            )
        )

        // 4. Microclimate / Heat Factor (Weight 20%)
        val temp = state.weather.temperatureC
        val weatherScore = when {
            temp in 20..32 -> 92
            temp in 33..37 -> 72
            temp > 37 -> 50 // Heat wave stress
            else -> 65 // Cold stress
        }
        val weatherObs = when {
            temp > 37 -> "Severe ambient heat ($temp°C) causing high transpiration and pollen drying."
            temp in 20..32 -> "Ambient temperature ($temp°C) is highly conducive to photosynthesis."
            else -> "Borderline temperatures ($temp°C) moderately slowing metabolic expansion."
        }
        factors.add(
            HealthFactorBreakdown(
                factorName = "Climate Conduciveness",
                score = weatherScore,
                weightPercent = 20,
                status = if (weatherScore >= 80) "Favorable" else "Weather Stress",
                observation = weatherObs
            )
        )

        // Weighted aggregation
        val finalScore = (
                satelliteScore * 0.25f +
                moistureScore * 0.30f +
                diseaseScore * 0.25f +
                weatherScore * 0.20f
        ).toInt().coerceIn(10, 99)

        val category = when {
            finalScore >= 85 -> "Optimal"
            finalScore >= 70 -> "Good"
            finalScore >= 50 -> "Needs Attention"
            else -> "Critical Stress"
        }

        // Identify primary stress
        val primaryStress = when {
            moistureScore <= 55 -> "Water Stress (Soil Moisture: $moisture%)"
            diseaseScore <= 65 -> "Foliar Pathology (${activeInfections.firstOrNull()?.diseaseName ?: "Infection"})"
            weatherScore <= 60 -> "Heat Stress ($temp°C)"
            satelliteScore <= 60 -> "Vegetation Canopy Deficit"
            else -> "None (Crop in healthy equilibrium)"
        }

        val trend = when {
            finalScore >= 80 -> "Improving"
            finalScore in 65..79 -> "Stable"
            else -> "Declining"
        }

        val explanation = if (primaryStress.startsWith("None")) {
            "Your ${state.activeCropContext.cropName} crop is flourishing at $finalScore/100. Moisture and vegetative vigor are both balanced."
        } else {
            "Your ${state.activeCropContext.cropName} health is rated $finalScore/100 ($category). Main limiting factor: $primaryStress. Immediate attention advised to prevent yield penalty."
        }

        return CropHealthAnalysis(
            healthScore = finalScore,
            healthCategory = category,
            trend = trend,
            primaryStress = primaryStress,
            contributingFactors = factors,
            explanationFarmerLanguage = explanation
        )
    }
}
