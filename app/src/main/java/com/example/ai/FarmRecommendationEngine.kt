package com.example.ai

import kotlinx.serialization.Serializable

/**
 * Priority levels for actionable recommendations.
 */
enum class RecommendationPriority(val label: String) {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW")
}

/**
 * High-value structured recommendation item strictly adhering to Section 22 format.
 */
@Serializable
data class GroundedRecommendation(
    val id: String,
    val category: String, // "IRRIGATION", "DISEASE", "WEATHER", "NUTRITION", "MARKET", "HARDWARE"
    val what: String,
    val why: String,
    val whenWindow: String,
    val priority: RecommendationPriority,
    val confidence: String, // "High", "Medium", "Low"
    val dataUsed: String,
    val feedbackStatus: String = "PENDING", // "PENDING", "DONE", "SKIPPED", "NOT_HELPFUL"
    val rankScore: Float = 0f
)

/**
 * FarmRecommendationEngine
 *
 * Generates and ranks candidate agricultural actions from DigitalFarmState.
 * Filters down to the top 3-5 highest-leverage actions to prevent cognitive overload and recommendation spam.
 */
object FarmRecommendationEngine {

    fun generateRecommendations(state: DigitalFarmState): List<GroundedRecommendation> {
        val candidates = mutableListOf<GroundedRecommendation>()

        val cropName = state.activeCropContext.cropName
        val cropStage = state.activeCropContext.currentStage
        val moisture = state.sensorData.soilMoisturePercent
        val targetMin = state.soil.targetMoistureMin
        val rainProb = state.weather.rainProbabilityPercent
        val humidity = state.weather.humidityPercent
        val temp = state.weather.temperatureC
        val activeInfections = state.recentScans.filter { !it.isHealthy }

        // Candidate 1: Root zone irrigation
        if (moisture < targetMin && rainProb < 50) {
            candidates.add(
                GroundedRecommendation(
                    id = "rec_irrigation_moisture_deficit",
                    category = "IRRIGATION",
                    what = "Irrigate $cropName field with root-zone drip irrigation.",
                    why = "Soil moisture ($moisture%) is below critical target ($targetMin%) during vulnerable $cropStage stage.",
                    whenWindow = "Within 12 hours (tomorrow early morning)",
                    priority = RecommendationPriority.HIGH,
                    confidence = "High",
                    dataUsed = "ESP32 Soil Probe (${moisture}%) + Weather Forecast (${rainProb}% rain)",
                    rankScore = 95f
                )
            )
        } else if (rainProb >= 65) {
            candidates.add(
                GroundedRecommendation(
                    id = "rec_irrigation_hold_rain",
                    category = "IRRIGATION",
                    what = "Hold off all irrigation and clear plot drainage furrows.",
                    why = "Heavy precipitation ($rainProb% chance) expected within 36 hours. Over-irrigation risks root rot and nutrient runoff.",
                    whenWindow = "Today before 04:00 PM",
                    priority = RecommendationPriority.MEDIUM,
                    confidence = "High",
                    dataUsed = "Meteorological precipitation model + Soil moisture sensor",
                    rankScore = 80f
                )
            )
        }

        // Candidate 2: Foliar pathology management
        if (activeInfections.isNotEmpty()) {
            val infection = activeInfections.first()
            val knowledge = DiseaseKnowledgeBase.getKnowledge(infection.diseaseName)
            candidates.add(
                GroundedRecommendation(
                    id = "rec_disease_spray",
                    category = "DISEASE",
                    what = "Apply targeted foliar spray: ${knowledge.organicRemedies.take(70)}...",
                    why = "Recent leaf scan diagnosed ${infection.diseaseName} with ${infection.severity} severity.",
                    whenWindow = "Tomorrow morning between 06:30 AM – 09:00 AM",
                    priority = if (infection.severity.contains("High", ignoreCase = true)) RecommendationPriority.HIGH else RecommendationPriority.MEDIUM,
                    confidence = "High",
                    dataUsed = "On-device KisanAI Leaf Diagnostic Scanner + Knowledge Base",
                    rankScore = if (infection.severity.contains("High", ignoreCase = true)) 98f else 82f
                )
            )
        } else if (humidity > 78 && (rainProb > 40 || state.weather.rainfallMm > 0)) {
            candidates.add(
                GroundedRecommendation(
                    id = "rec_disease_preventive_scouting",
                    category = "DISEASE",
                    what = "Perform ground scouting on lower leaf canopy and prune foliage touching wet soil.",
                    why = "High humidity ($humidity%) and leaf wetness create peak favorable conditions for fungal spore germination.",
                    whenWindow = "Today morning during routine plot inspection",
                    priority = RecommendationPriority.MEDIUM,
                    confidence = "High",
                    dataUsed = "Ambient relative humidity ($humidity%) + Microclimate telemetry",
                    rankScore = 78f
                )
            )
        }

        // Candidate 3: Critical Stage Nutrition
        if (cropStage.contains("Flower", ignoreCase = true) || cropStage.contains("Fruit", ignoreCase = true)) {
            candidates.add(
                GroundedRecommendation(
                    id = "rec_nutrition_potash_boron",
                    category = "NUTRITION",
                    what = "Apply soluble Potassium Nitrate (13:0:45) @ 5g/L + Boron (20%) @ 1g/L foliar spray.",
                    why = "Crop is in peak $cropStage. Boron and potash prevent flower drop and ensure uniform fruit set.",
                    whenWindow = "Next 48 hours",
                    priority = RecommendationPriority.MEDIUM,
                    confidence = "High",
                    dataUsed = "Crop Stage Engine (${cropStage}, DAS: ${state.activeCropContext.daysAfterSowing})",
                    rankScore = 75f
                )
            )
        }

        // Candidate 4: Mandi Selling Strategy
        if (state.marketData.trend.equals("Increasing", ignoreCase = true) && state.activeCropContext.daysToNextStage <= 15) {
            candidates.add(
                GroundedRecommendation(
                    id = "rec_market_staggered_sale",
                    category = "MARKET",
                    what = "Plan first harvest picking for ${state.marketData.mandiName} early morning lot auction.",
                    why = "Mandi price is appreciating (+${state.marketData.change7dPercent}% this week, currently ₹${state.marketData.modalPricePerQ}/q). 7-day forecast indicates continued firmness.",
                    whenWindow = "Within 5 to 7 days",
                    priority = RecommendationPriority.MEDIUM,
                    confidence = "Medium",
                    dataUsed = "Regional APMC Mandi Price Trends + Time-Series Forecast",
                    rankScore = 70f
                )
            )
        }

        // Candidate 5: Hardware & Sensor Maintenance
        if (!state.sensorData.deviceOnline || state.sensorData.isAnomalyDetected) {
            val issue = if (!state.sensorData.deviceOnline) "Device is offline" else state.sensorData.anomalyDescription ?: "Telemetry anomaly"
            candidates.add(
                GroundedRecommendation(
                    id = "rec_hardware_inspect",
                    category = "HARDWARE",
                    what = "Inspect field sensor node ${state.sensorData.deviceId} battery and wireless antenna.",
                    why = "IoT node warning: $issue. Accurate telemetry is required for automated irrigation triggers.",
                    whenWindow = "Within 24 hours",
                    priority = RecommendationPriority.HIGH,
                    confidence = "High",
                    dataUsed = "IoT Node Heartbeat & Telemetry Monitor",
                    rankScore = 88f
                )
            )
        }

        // Candidate 6: Satellite Canopy Verification
        if (state.satelliteData.anomalyDetected) {
            candidates.add(
                GroundedRecommendation(
                    id = "rec_satellite_ground_scout",
                    category = "SCOUTING",
                    what = "Ground scout southwest sector of plot to verify canopy moisture deficit.",
                    why = "Sentinel-2 satellite NDVI index detected localized canopy stress anomaly.",
                    whenWindow = "Next field visit",
                    priority = RecommendationPriority.LOW,
                    confidence = "Medium",
                    dataUsed = "Sentinel-2 NDVI Multi-spectral Satellite Imagery",
                    rankScore = 65f
                )
            )
        }

        // Rank by urgency/impact score and return top 3 to 5 (avoiding recommendation spam)
        return candidates
            .sortedByDescending { it.rankScore }
            .take(5)
    }
}
