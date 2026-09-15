package com.example.ai

/**
 * Configurable weights for composite Farm Health score computation.
 */
data class FarmHealthWeights(
    val cropHealthWeight: Float = 0.30f,
    val waterWeight: Float = 0.20f,
    val diseaseWeight: Float = 0.15f,
    val weatherWeight: Float = 0.10f,
    val soilWeight: Float = 0.10f,
    val satelliteWeight: Float = 0.10f,
    val hardwareWeight: Float = 0.05f
)

/**
 * Composite Farm Health result.
 */
data class FarmHealthResult(
    val farmHealthScore: Int, // 0 to 100
    val ratingCategory: String, // "Optimal", "Good", "Needs Attention", "Critical Stressed"
    val subScores: Map<String, Int>,
    val weightsApplied: FarmHealthWeights,
    val topConcern: String,
    val executiveBriefing: String
)

/**
 * FarmHealthEngine
 *
 * Computes an overarching Farm Health Index from 7 weighted pillars with configurable weights.
 */
object FarmHealthEngine {

    var currentWeights: FarmHealthWeights = FarmHealthWeights()

    fun evaluateFarmHealth(
        state: DigitalFarmState,
        weights: FarmHealthWeights = currentWeights
    ): FarmHealthResult {
        // 1. Crop Health Component (0..100)
        val cropHealthAnalysis = CropHealthEngine.evaluateCropHealth(state)
        val cropScore = cropHealthAnalysis.healthScore

        // 2. Water Status Component (0..100)
        val moisture = state.sensorData.soilMoisturePercent
        val targetMin = state.soil.targetMoistureMin
        val targetMax = state.soil.targetMoistureMax
        val waterScore = when {
            moisture in targetMin..targetMax -> 96
            moisture in (targetMin - 5)..(targetMax + 5) -> 75
            moisture < targetMin - 5 -> 45
            else -> 60
        }

        // 3. Disease Status Component (0..100)
        val activeDiseases = state.recentScans.filter { !it.isHealthy }
        val diseaseScore = when {
            activeDiseases.isEmpty() -> 98
            activeDiseases.any { it.severity.contains("Severe", ignoreCase = true) || it.severity.contains("High", ignoreCase = true) } -> 40
            activeDiseases.any { it.severity.contains("Moderate", ignoreCase = true) } -> 68
            else -> 82
        }

        // 4. Weather Component (0..100)
        val temp = state.weather.temperatureC
        val weatherScore = when {
            temp in 20..32 -> 94
            temp in 33..36 -> 74
            temp > 36 -> 52
            else -> 65
        }

        // 5. Soil Component (0..100)
        val ph = state.soil.ph
        val soilScore = when {
            ph in 6.2f..7.5f -> 92
            ph in 5.8f..8.0f -> 78
            else -> 58
        }

        // 6. Satellite Component (0..100)
        val ndvi = state.satelliteData.ndvi
        val satelliteScore = when {
            ndvi >= 0.72f -> 95
            ndvi >= 0.60f -> 80
            ndvi >= 0.45f -> 62
            else -> 40
        }

        // 7. Hardware Component (0..100)
        val hardwareScore = when {
            !state.sensorData.deviceOnline -> 35
            state.sensorData.isAnomalyDetected -> 60
            state.sensorData.batteryPercent < 20 -> 55
            else -> 98
        }

        // Weighted calculation
        val totalWeight = weights.cropHealthWeight + weights.waterWeight + weights.diseaseWeight +
                weights.weatherWeight + weights.soilWeight + weights.satelliteWeight + weights.hardwareWeight

        val rawScore = (
                cropScore * weights.cropHealthWeight +
                waterScore * weights.waterWeight +
                diseaseScore * weights.diseaseWeight +
                weatherScore * weights.weatherWeight +
                soilScore * weights.soilWeight +
                satelliteScore * weights.satelliteWeight +
                hardwareScore * weights.hardwareWeight
        ) / totalWeight

        val finalScore = rawScore.toInt().coerceIn(10, 100)

        val category = when {
            finalScore >= 85 -> "Optimal"
            finalScore >= 72 -> "Good"
            finalScore >= 55 -> "Needs Attention"
            else -> "Critical Stressed"
        }

        val subScoresMap = mapOf(
            "Crop Health" to cropScore,
            "Water Status" to waterScore,
            "Disease Control" to diseaseScore,
            "Weather Conduciveness" to weatherScore,
            "Soil Health" to soilScore,
            "Satellite Vigor" to satelliteScore,
            "IoT Hardware" to hardwareScore
        )

        // Find weakest pillar
        val weakestPillar = subScoresMap.minByOrNull { it.value }
        val topConcern = if ((weakestPillar?.value ?: 100) < 70) {
            "${weakestPillar?.key} (${weakestPillar?.value}/100)"
        } else {
            "No urgent bottlenecks detected."
        }

        val briefing = "Farm Health is rated $finalScore/100 ($category). Primary factor for attention: $topConcern."

        return FarmHealthResult(
            farmHealthScore = finalScore,
            ratingCategory = category,
            subScores = subScoresMap,
            weightsApplied = weights,
            topConcern = topConcern,
            executiveBriefing = briefing
        )
    }
}
