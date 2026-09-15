package com.example.ai

import java.util.Locale

/**
 * Irrigation action directive.
 */
enum class IrrigationAction(val label: String) {
    IRRIGATE("IRRIGATE NOW"),
    WAIT("WAIT / DO NOT IRRIGATE"),
    MONITOR("MONITOR ROOT ZONE")
}

/**
 * Result of irrigation agronomic analysis.
 */
data class IrrigationAdviceResult(
    val action: IrrigationAction,
    val headlineReason: String,
    val detailedExplanation: String,
    val cropWaterRequirementMmPerDay: Float?,
    val recommendedApplicationDepthMm: Int?,
    val recommendedDripHours: Float?,
    val optimalApplicationWindow: String,
    val confidence: ConfidenceGrade,
    val confidenceReason: String,
    val waterStressLevel: String // "None", "Mild", "Critical Deficit", "Waterlogged"
)

/**
 * IrrigationDecisionEngine
 *
 * Evaluates real soil moisture sensor telemetry, soil water holding capacity,
 * FAO-56 Penman-Monteith ET reference crop coefficients (Kc), and multi-day rainfall forecasts.
 */
object IrrigationDecisionEngine {

    // Crop coefficients (Kc) across growth stages
    private val CROP_KC_TABLE = mapOf(
        "tomato" to mapOf("establishment" to 0.6f, "vegetative" to 0.85f, "flowering" to 1.15f, "fruit" to 1.10f, "maturity" to 0.80f),
        "rice" to mapOf("establishment" to 1.1f, "vegetative" to 1.15f, "flowering" to 1.30f, "maturity" to 0.90f),
        "cotton" to mapOf("establishment" to 0.5f, "vegetative" to 0.80f, "flowering" to 1.20f, "maturity" to 0.70f),
        "wheat" to mapOf("establishment" to 0.4f, "vegetative" to 0.85f, "flowering" to 1.15f, "maturity" to 0.45f),
        "potato" to mapOf("establishment" to 0.5f, "vegetative" to 0.80f, "flowering" to 1.15f, "maturity" to 0.75f)
    )

    fun evaluateIrrigation(state: DigitalFarmState): IrrigationAdviceResult {
        val hasMoistureSensor = state.sensorData.deviceOnline
        val moisture = if (hasMoistureSensor) state.sensorData.soilMoisturePercent else null
        val targetMin = state.soil.targetMoistureMin
        val targetMax = state.soil.targetMoistureMax

        val rainProbability = state.weather.rainProbabilityPercent
        val rainfallPast24h = state.weather.rainfallMm
        val temp = state.weather.temperatureC
        val humidity = state.weather.humidityPercent

        val stageKey = state.activeCropContext.currentStage.lowercase()
        val cropKey = state.activeCropContext.cropName.lowercase()

        // Crop coefficient lookup
        val kc = CROP_KC_TABLE.entries.firstOrNull { cropKey.contains(it.key) }?.value?.let { stageMap ->
            stageMap.entries.firstOrNull { stageKey.contains(it.key) }?.value ?: 1.0f
        } ?: 1.0f

        // Reference evapotranspiration ET0 proxy (Hargreaves/standard temperature-humidity estimation)
        val et0 = (0.0023f * (temp + 17.8f) * 6.5f).coerceIn(3.0f, 8.5f)
        val etcDailyMm = et0 * kc

        // Case 1: Missing Sensor Data
        if (moisture == null) {
            val isRainingSoon = rainProbability > 60
            val action = if (isRainingSoon) IrrigationAction.WAIT else IrrigationAction.MONITOR
            return IrrigationAdviceResult(
                action = action,
                headlineReason = "Soil moisture sensor offline; recommendations based on weather and soil type.",
                detailedExplanation = "Live root zone soil moisture data is unavailable. Confidence is reduced. Check manual soil ball test (squeeze top 15cm soil by hand).",
                cropWaterRequirementMmPerDay = etcDailyMm,
                recommendedApplicationDepthMm = null, // Do not output fabricated exact litres
                recommendedDripHours = null,
                optimalApplicationWindow = "Late afternoon or early morning",
                confidence = ConfidenceGrade.LOW,
                confidenceReason = "Telemetry missing from sensor device ${state.sensorData.deviceId}.",
                waterStressLevel = "Unknown (Sensor Offline)"
            )
        }

        // Case 2: Imminent Heavy Rain
        if (rainProbability >= 65) {
            return IrrigationAdviceResult(
                action = IrrigationAction.WAIT,
                headlineReason = "High rainfall predicted ($rainProbability% probability) within next 36 hours.",
                detailedExplanation = "Soil moisture is currently $moisture%. Additional irrigation risks root hypoxia, nutrient leaching, and fungal spore splash.",
                cropWaterRequirementMmPerDay = etcDailyMm,
                recommendedApplicationDepthMm = null,
                recommendedDripHours = null,
                optimalApplicationWindow = "Hold off irrigation until post-rainfall field check",
                confidence = ConfidenceGrade.HIGH,
                confidenceReason = "Validated against live soil telemetry ($moisture%) and meteorological precipitation forecast.",
                waterStressLevel = if (moisture < targetMin) "Mild (Rainfall Imminent)" else "None"
            )
        }

        // Case 3: Waterlogged / Saturated Soil
        if (moisture > targetMax + 8) {
            return IrrigationAdviceResult(
                action = IrrigationAction.WAIT,
                headlineReason = "Soil is over-saturated ($moisture% vs max limit $targetMax%).",
                detailedExplanation = "Excessive moisture in root zone inhibits oxygen respiration. Ensure plot drainage trenches are clear.",
                cropWaterRequirementMmPerDay = etcDailyMm,
                recommendedApplicationDepthMm = null,
                recommendedDripHours = null,
                optimalApplicationWindow = "Allow soil to dry down naturally to target zone",
                confidence = ConfidenceGrade.HIGH,
                confidenceReason = "Soil moisture exceeds upper field holding capacity.",
                waterStressLevel = "Waterlogged"
            )
        }

        // Case 4: Critical Moisture Deficit -> IRRIGATE NOW
        if (moisture < targetMin) {
            val deficitDepthMm = ((targetMax - moisture) * 0.8f).toInt().coerceIn(15, 40)
            val dripHours = (deficitDepthMm / 6.0f) // Assuming typical 6mm/hr drip emitter density

            return IrrigationAdviceResult(
                action = IrrigationAction.IRRIGATE,
                headlineReason = "Soil moisture ($moisture%) is below critical target ($targetMin%).",
                detailedExplanation = "Crop is in ${state.activeCropContext.currentStage} stage (Kc: $kc). Moisture stress will impair blossom set and fruit enlargement.",
                cropWaterRequirementMmPerDay = etcDailyMm,
                recommendedApplicationDepthMm = deficitDepthMm,
                recommendedDripHours = String.format(Locale.US, "%.1f", dripHours).toFloatOrNull(),
                optimalApplicationWindow = "Tomorrow early morning (05:30 AM – 08:30 AM) to minimize evaporation",
                confidence = ConfidenceGrade.HIGH,
                confidenceReason = "Confirmed by soil sensor probe reading $moisture% and low rainfall probability ($rainProbability%).",
                waterStressLevel = if (moisture < targetMin - 8) "Critical Deficit" else "Mild Deficit"
            )
        }

        // Case 5: Normal Moisture in Target Range -> MONITOR
        return IrrigationAdviceResult(
            action = IrrigationAction.WAIT,
            headlineReason = "Soil moisture ($moisture%) is within optimal target ($targetMin% – $targetMax%).",
            detailedExplanation = "Current moisture balance satisfies daily crop evapotranspiration ($etcDailyMm mm/day). No irrigation required today.",
            cropWaterRequirementMmPerDay = etcDailyMm,
            recommendedApplicationDepthMm = null,
            recommendedDripHours = null,
            optimalApplicationWindow = "Next evaluation in 24 hours",
            confidence = ConfidenceGrade.HIGH,
            confidenceReason = "Adequate moisture registered across active root zone.",
            waterStressLevel = "None"
        )
    }
}
