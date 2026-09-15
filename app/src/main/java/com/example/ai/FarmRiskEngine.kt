package com.example.ai

/**
 * Risk severity level.
 */
enum class RiskLevel(val label: String, val badgeColorHex: Long) {
    LOW("Low", 0xFF2E7D32),
    MEDIUM("Medium", 0xFFF57F17),
    HIGH("High", 0xFFE65100),
    CRITICAL("Critical", 0xFFC62828)
}

/**
 * Individual evaluated risk aspect.
 */
data class EvaluatedRisk(
    val category: String,
    val level: RiskLevel,
    val riskScore: Int, // 0 to 100
    val confidence: ConfidenceGrade,
    val primaryReason: String,
    val recommendedMitigation: String,
    val correlatedTriggers: List<String>
)

/**
 * FarmRiskProfileResult
 */
data class FarmRiskProfileResult(
    val overallRiskLevel: RiskLevel,
    val overallRiskScore: Int,
    val diseaseRisk: EvaluatedRisk,
    val waterStressRisk: EvaluatedRisk,
    val heatRisk: EvaluatedRisk,
    val rainRisk: EvaluatedRisk,
    val yieldRisk: EvaluatedRisk,
    val marketRisk: EvaluatedRisk,
    val hardwareRisk: EvaluatedRisk,
    val activeCorrelations: List<String>,
    val executiveSummary: String
)

/**
 * FarmRiskEngine
 *
 * Multi-factor risk engine featuring cross-factor correlation rules.
 * Elevates compound risks (e.g. humidity + rainfall + flowering stage => high fungal vector risk).
 */
object FarmRiskEngine {

    fun evaluateFarmRisks(state: DigitalFarmState): FarmRiskProfileResult {
        val correlations = mutableListOf<String>()

        val humidity = state.weather.humidityPercent
        val rainfall = state.weather.rainfallMm
        val rainProb = state.weather.rainProbabilityPercent
        val temp = state.weather.temperatureC
        val moisture = state.sensorData.soilMoisturePercent
        val isFlowering = state.activeCropContext.currentStage.contains("Flower", ignoreCase = true)
        val hasRecentDisease = state.recentScans.any { !it.isHealthy }
        val deviceOnline = state.sensorData.deviceOnline
        val isAnomaly = state.sensorData.isAnomalyDetected
        val marketTrend = state.marketData.trend

        // 1. Disease Risk (with Correlation Layer)
        var diseaseScore = 30
        val diseaseCorrelations = mutableListOf<String>()

        if (humidity > 78) {
            diseaseScore += 25
            diseaseCorrelations.add("Elevated ambient humidity ($humidity%)")
        }
        if (rainfall > 5.0 || rainProb > 50) {
            diseaseScore += 20
            diseaseCorrelations.add("Precipitation moisture on canopy")
        }
        if (isFlowering) {
            diseaseScore += 15
            diseaseCorrelations.add("Vulnerable flowering floral tissue")
        }
        if (hasRecentDisease) {
            diseaseScore += 20
            diseaseCorrelations.add("Residual pathogen spores in plot")
        }

        // Rule: High humidity + rainfall + flowering + disease history => compounding fungal risk
        if (humidity > 75 && (rainProb > 40 || rainfall > 0) && (isFlowering || hasRecentDisease)) {
            diseaseScore = diseaseScore.coerceAtLeast(82)
            correlations.add("Compounding Fungal Vector: High humidity ($humidity%) + precipitation + vulnerable canopy elevates fungal spore germination.")
        }

        val diseaseLevel = scoreToLevel(diseaseScore)
        val diseaseRisk = EvaluatedRisk(
            category = "Foliar Pathology Risk",
            level = diseaseLevel,
            riskScore = diseaseScore.coerceIn(0, 100),
            confidence = ConfidenceGrade.HIGH,
            primaryReason = if (diseaseScore > 60) "Environmental conditions (${humidity}% RH, precipitation) strongly favor fungal sporulation." else "Microclimate conditions are currently unfavorable to pathogen epidemics.",
            recommendedMitigation = if (diseaseScore > 60) "Scout lower canopy in morning and apply preventive bio-agent (Trichoderma viride)." else "Maintain routine weekly scouting.",
            correlatedTriggers = diseaseCorrelations
        )

        // 2. Water Stress Risk (with Correlation Layer)
        var waterScore = 25
        val waterCorrelations = mutableListOf<String>()
        if (moisture < state.soil.targetMoistureMin) {
            waterScore += 40
            waterCorrelations.add("Soil moisture ($moisture%) below target (${state.soil.targetMoistureMin}%)")
        }
        if (temp > 34) {
            waterScore += 20
            waterCorrelations.add("High ambient evapotranspiration ($temp°C)")
        }
        if (rainProb < 20) {
            waterScore += 15
            waterCorrelations.add("No immediate rainfall predicted")
        }

        // Rule: High temp + low moisture + low rain => severe water stress
        if (temp >= 32 && moisture < state.soil.targetMoistureMin && rainProb < 25) {
            waterScore = waterScore.coerceAtLeast(80)
            correlations.add("Compound Evaporative Deficit: Elevated temperature ($temp°C) combined with soil moisture depletion ($moisture%) causes accelerated wilt.")
        }

        val waterLevel = scoreToLevel(waterScore)
        val waterRisk = EvaluatedRisk(
            category = "Root Zone Water Stress",
            level = waterLevel,
            riskScore = waterScore.coerceIn(0, 100),
            confidence = if (deviceOnline) ConfidenceGrade.HIGH else ConfidenceGrade.LOW,
            primaryReason = if (waterScore > 60) "Root zone moisture depleted during critical stage without replenishing rainfall." else "Adequate moisture profile in root zone.",
            recommendedMitigation = if (waterScore > 60) "Irrigate plot within 12-24h to avoid blossom drop." else "Continue standard moisture monitoring.",
            correlatedTriggers = waterCorrelations
        )

        // 3. Heat Risk
        val heatScore = when {
            temp >= 40 -> 90
            temp >= 36 -> 72
            temp >= 32 -> 45
            else -> 20
        }
        val heatRisk = EvaluatedRisk(
            category = "Ambient Heat Stress",
            level = scoreToLevel(heatScore),
            riskScore = heatScore,
            confidence = ConfidenceGrade.HIGH,
            primaryReason = if (heatScore > 65) "Maximum temperatures ($temp°C) inhibit floral pollination." else "Temperatures are within standard photosynthetic tolerance.",
            recommendedMitigation = if (heatScore > 65) "Run short cooling drip cycles during peak afternoon." else "Standard operations.",
            correlatedTriggers = listOf("Ambient temp: $temp°C")
        )

        // 4. Rain / Waterlogging Risk
        val rainScore = when {
            rainfall > 30.0 -> 85
            rainProb >= 70 -> 75
            rainProb >= 40 -> 45
            else -> 15
        }
        val rainRisk = EvaluatedRisk(
            category = "Excess Rain / Waterlogging",
            level = scoreToLevel(rainScore),
            riskScore = rainScore,
            confidence = ConfidenceGrade.HIGH,
            primaryReason = if (rainScore > 60) "High probability ($rainProb%) of rain causing standing water." else "Low probability of disruptive precipitation.",
            recommendedMitigation = if (rainScore > 60) "Inspect drainage bunds to avoid standing puddles." else "Standard plot maintenance.",
            correlatedTriggers = listOf("Rain probability: $rainProb%", "Past 24h: ${rainfall}mm")
        )

        // 5. Yield Risk (Composite)
        val yieldScore = ((diseaseScore * 0.35f) + (waterScore * 0.40f) + (heatScore * 0.25f)).toInt()
        val yieldRisk = EvaluatedRisk(
            category = "Yield Realization Risk",
            level = scoreToLevel(yieldScore),
            riskScore = yieldScore,
            confidence = ConfidenceGrade.HIGH,
            primaryReason = if (yieldScore > 60) "Compound water and microclimate stresses threaten seasonal yield target." else "Crop yield trajectory is on course for optimal harvest.",
            recommendedMitigation = "Address the highest priority stress (water or disease) immediately.",
            correlatedTriggers = listOf("Composite of disease ($diseaseScore) & water ($waterScore)")
        )

        // 6. Market Risk
        val marketScore = when {
            marketTrend.equals("Decreasing", ignoreCase = true) -> 74
            marketTrend.equals("Stable", ignoreCase = true) -> 35
            else -> 20 // Increasing
        }
        val marketRisk = EvaluatedRisk(
            category = "Mandi Price Volatility",
            level = scoreToLevel(marketScore),
            riskScore = marketScore,
            confidence = ConfidenceGrade.MEDIUM,
            primaryReason = if (marketScore > 60) "Market price showing downward pressure in regional APMC mandis." else "Market price is stable or appreciating (+${state.marketData.change7dPercent}%).",
            recommendedMitigation = if (marketScore > 60) "Consider staggered harvest sales or secure local wholesale contract." else "Hold for peak market window if crop maturity allows.",
            correlatedTriggers = listOf("Trend: $marketTrend", "Mandi: ${state.marketData.mandiName}")
        )

        // 7. Hardware / IoT Risk
        val hardwareScore = when {
            !deviceOnline -> 85
            isAnomaly -> 70
            state.sensorData.batteryPercent < 20 -> 60
            else -> 10
        }
        val hardwareRisk = EvaluatedRisk(
            category = "IoT & Sensor Telemetry",
            level = scoreToLevel(hardwareScore),
            riskScore = hardwareScore,
            confidence = ConfidenceGrade.HIGH,
            primaryReason = if (!deviceOnline) "Sensor node ${state.sensorData.deviceId} is currently offline." else if (isAnomaly) "Sensor anomaly detected: ${state.sensorData.anomalyDescription}" else "Telemetry nodes functioning normally with ${state.sensorData.batteryPercent}% battery.",
            recommendedMitigation = if (hardwareScore > 50) "Check solar panel cleaning, battery terminal, or cellular signal." else "System operational.",
            correlatedTriggers = listOf("Device online: $deviceOnline", "Battery: ${state.sensorData.batteryPercent}%")
        )

        // Calculate Overall Risk
        val overallScore = maxOf(diseaseScore, waterScore, yieldScore).coerceIn(0, 100)
        val overallLevel = scoreToLevel(overallScore)

        val summary = when (overallLevel) {
            RiskLevel.CRITICAL -> "CRITICAL RISK: Multiple compounding stresses detected. Immediate farm intervention needed today."
            RiskLevel.HIGH -> "HIGH RISK: Water or disease pressure requires immediate mitigation within 24 hours."
            RiskLevel.MEDIUM -> "MODERATE RISK: Farm conditions require active monitoring, particularly moisture and humidity."
            RiskLevel.LOW -> "LOW RISK: Farm conditions are stable. Continue standard agronomic operations."
        }

        return FarmRiskProfileResult(
            overallRiskLevel = overallLevel,
            overallRiskScore = overallScore,
            diseaseRisk = diseaseRisk,
            waterStressRisk = waterRisk,
            heatRisk = heatRisk,
            rainRisk = rainRisk,
            yieldRisk = yieldRisk,
            marketRisk = marketRisk,
            hardwareRisk = hardwareRisk,
            activeCorrelations = correlations,
            executiveSummary = summary
        )
    }

    private fun scoreToLevel(score: Int): RiskLevel {
        return when {
            score >= 80 -> RiskLevel.CRITICAL
            score >= 60 -> RiskLevel.HIGH
            score >= 40 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }
}
