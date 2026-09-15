package com.example.ai

/**
 * Parameter overrides for a hypothetical simulation scenario.
 */
data class SimulationScenario(
    val id: String,
    val title: String,
    val description: String,
    val soilMoistureDeltaPercent: Int = 0,
    val temperatureDeltaC: Int = 0,
    val rainfallMmDelta: Double = 0.0,
    val diseaseCleared: Boolean = false,
    val marketPriceDeltaPercent: Int = 0
)

/**
 * Result comparing baseline vs simulated outcome.
 */
data class SimulationComparisonResult(
    val scenarioTitle: String,
    val baselineHealthScore: Int,
    val simulatedHealthScore: Int,
    val healthScoreDelta: Int,
    val baselineYieldTonnes: Double,
    val simulatedYieldTonnes: Double,
    val yieldDeltaTonnes: Double,
    val baselineRiskLevel: RiskLevel,
    val simulatedRiskLevel: RiskLevel,
    val keyImpactExplanation: String,
    val actionableFarmerTakeaway: String,
    val simulationNotice: String = "SIMULATION ONLY — ESTIMATED AGRICULTURAL OUTCOME, NOT GUARANTEED"
)

/**
 * FarmSimulationEngine
 *
 * Simulates the agronomic and financial impact of hypothetical interventions or climate anomalies.
 */
object FarmSimulationEngine {

    val PRESET_SCENARIOS = listOf(
        SimulationScenario(
            id = "scenario_optimal_drip",
            title = "Scenario 1: Optimal Drip Irrigation",
            description = "Apply targeted drip irrigation to bring root zone soil moisture from current level to optimal 42%.",
            soilMoistureDeltaPercent = 14
        ),
        SimulationScenario(
            id = "scenario_proactive_fungicide",
            title = "Scenario 2: Preventive Biological Fungicide",
            description = "Apply Trichoderma viride preventive spray to clear early fungal lesions before flowering.",
            diseaseCleared = true
        ),
        SimulationScenario(
            id = "scenario_heatwave",
            title = "Scenario 3: Severe Heatwave (+4°C)",
            description = "Simulate 5 consecutive days of 38°C ambient heat without cloud cover.",
            temperatureDeltaC = 4,
            soilMoistureDeltaPercent = -8
        ),
        SimulationScenario(
            id = "scenario_heavy_rain",
            title = "Scenario 4: Heavy Unseasonal Rain (+50mm)",
            description = "Simulate 50mm sudden downpour causing standing water and saturated soil.",
            rainfallMmDelta = 50.0,
            soilMoistureDeltaPercent = 25
        ),
        SimulationScenario(
            id = "scenario_market_surge",
            title = "Scenario 5: Mandi Price Surge (+15%)",
            description = "Simulate festive demand driving APMC prices up by 15% during harvest window.",
            marketPriceDeltaPercent = 15
        )
    )

    fun runSimulation(
        baselineState: DigitalFarmState,
        scenario: SimulationScenario
    ): SimulationComparisonResult {
        val baseHealth = FarmHealthEngine.evaluateFarmHealth(baselineState)
        val baseYield = YieldPredictionEngine.predictYield(baselineState)
        val baseRisk = FarmRiskEngine.evaluateFarmRisks(baselineState)

        // Build simulated DigitalFarmState
        val simMoisture = (baselineState.sensorData.soilMoisturePercent + scenario.soilMoistureDeltaPercent).coerceIn(10, 80)
        val simTemp = baselineState.weather.temperatureC + scenario.temperatureDeltaC
        val simRainfall = baselineState.weather.rainfallMm + scenario.rainfallMmDelta
        val simScans = if (scenario.diseaseCleared) emptyList() else baselineState.recentScans
        val simModalPrice = (baselineState.marketData.modalPricePerQ * (1f + scenario.marketPriceDeltaPercent / 100f)).toInt()

        val simulatedState = baselineState.copy(
            sensorData = baselineState.sensorData.copy(soilMoisturePercent = simMoisture),
            weather = baselineState.weather.copy(
                temperatureC = simTemp,
                rainfallMm = simRainfall
            ),
            recentScans = simScans,
            marketData = baselineState.marketData.copy(modalPricePerQ = simModalPrice)
        )

        val simHealth = FarmHealthEngine.evaluateFarmHealth(simulatedState)
        val simYield = YieldPredictionEngine.predictYield(simulatedState)
        val simRisk = FarmRiskEngine.evaluateFarmRisks(simulatedState)

        val healthDelta = simHealth.farmHealthScore - baseHealth.farmHealthScore
        val yieldDelta = simYield.expectedYieldMidpoint - baseYield.expectedYieldMidpoint

        val keyImpact = when {
            yieldDelta > 0.1 -> "Increases expected harvest by +${String.format("%.2f", yieldDelta)} t/acre (+${((yieldDelta / baseYield.expectedYieldMidpoint) * 100).toInt()}%). Overall farm health improves to ${simHealth.farmHealthScore}/100."
            yieldDelta < -0.1 -> "Yield penalty of ${String.format("%.2f", yieldDelta)} t/acre due to compounding climate stress. Overall risk escalates to ${simRisk.overallRiskLevel.label}."
            scenario.marketPriceDeltaPercent > 0 -> "Boosts gross revenue potential by +${scenario.marketPriceDeltaPercent}% at ₹$simModalPrice/q with stable agronomic yield."
            else -> "Farm health adjusts by $healthDelta points (${simHealth.farmHealthScore}/100). Risk level: ${simRisk.overallRiskLevel.label}."
        }

        val takeaway = when (scenario.id) {
            "scenario_optimal_drip" -> "Timely irrigation protects flowering set and delivers highest return on water investment."
            "scenario_proactive_fungicide" -> "Preventive bio-control arrests spore multiplication before leaf damage becomes irreversible."
            "scenario_heatwave" -> "Heatwaves require afternoon cooling micro-drip cycles and potassium foliar sprays to limit floral abortion."
            "scenario_heavy_rain" -> "Adequate drainage furrows are critical to prevent standing water hypoxia."
            "scenario_market_surge" -> "Grading and sorting produce into Grade A crates maximizes premium auction capture."
            else -> "Simulated scenario highlights agronomic leverage points for active farm management."
        }

        return SimulationComparisonResult(
            scenarioTitle = scenario.title,
            baselineHealthScore = baseHealth.farmHealthScore,
            simulatedHealthScore = simHealth.farmHealthScore,
            healthScoreDelta = healthDelta,
            baselineYieldTonnes = baseYield.expectedYieldMidpoint,
            simulatedYieldTonnes = simYield.expectedYieldMidpoint,
            yieldDeltaTonnes = yieldDelta,
            baselineRiskLevel = baseRisk.overallRiskLevel,
            simulatedRiskLevel = simRisk.overallRiskLevel,
            keyImpactExplanation = keyImpact,
            actionableFarmerTakeaway = takeaway
        )
    }
}
