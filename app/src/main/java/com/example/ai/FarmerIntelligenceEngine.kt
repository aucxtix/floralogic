package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class FarmerIntelligenceEngine {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun generateComprehensiveIntelligence(farmState: DigitalFarmState): CombinedIntelligence? = withContext(Dispatchers.IO) {
        try {
            // 1. Synthesize deterministic grounded intelligence from domain engines
            val briefingData = DailyFarmBriefingEngine.generateBriefing(farmState)
            val yieldData = YieldPredictionEngine.predictYield(farmState)
            val irrigationData = IrrigationDecisionEngine.evaluateIrrigation(farmState)
            val sellingData = MarketForecastEngine.evaluateSellingDecision(farmState)
            val riskData = FarmRiskEngine.evaluateFarmRisks(farmState)

            val dailyActions = briefingData.sevenDayPlan.map { planItem ->
                DailyAction(
                    dayLabel = planItem.dayLabel,
                    actionType = planItem.category,
                    description = "${planItem.actionHeadline}: ${planItem.agronomicRationale}"
                )
            }

            val briefing = DailyFarmBriefing(
                healthScore = briefingData.farmHealthScore,
                healthTrendMessage = briefingData.cropHealthStatus,
                cropName = farmState.activeCropContext.cropName,
                growthStage = farmState.activeCropContext.currentStage,
                weatherTemp = farmState.weather.temperatureC,
                rainRisk = if (farmState.weather.rainProbabilityPercent > 60) "High" else if (farmState.weather.rainProbabilityPercent > 30) "Medium" else "Low",
                soilMoisture = farmState.soil.moisturePercent,
                diseaseRisk = riskData.diseaseRisk.level.name,
                diseaseRiskExplanation = riskData.diseaseRisk.primaryReason,
                marketPrice = "₹${farmState.marketData.modalPricePerQ}/Q",
                topPriorities = briefingData.topPriorities.map { "${it.category}: ${it.what} - ${it.why}" }
            )

            val actionPlan = FarmActionPlan(timeline = dailyActions)

            val yieldPrediction = YieldPrediction(
                expectedYield = yieldData.expectedYieldRange,
                potentialYield = yieldData.benchmarkPotentialYield,
                yieldGapPercent = yieldData.yieldGapPercent,
                confidence = yieldData.predictionConfidence.label,
                risk = if (yieldData.yieldGapPercent > 20) "High Gap" else "Manageable",
                topLimitingFactors = yieldData.primaryLimitingFactors
            )

            val irrigationAdvice = IrrigationAdvice(
                recommendation = irrigationData.action.label,
                reason = irrigationData.headlineReason,
                amountEstimate = "${irrigationData.recommendedApplicationDepthMm ?: 0} mm (${irrigationData.recommendedDripHours ?: 0f} hrs drip cycle)",
                optimalTiming = irrigationData.optimalApplicationWindow
            )

            val marketAdvice = MarketAdvice(
                recommendation = sellingData.directive.label,
                reason = sellingData.primaryReason,
                confidence = sellingData.confidenceGrade.label,
                risks = listOf(sellingData.marketRiskWarning, sellingData.priceOutlook)
            )

            val riskProfile = FarmRiskProfile(
                overallRisk = riskData.overallRiskLevel.name,
                diseaseRisk = riskData.diseaseRisk.level.name,
                waterRisk = riskData.waterStressRisk.level.name,
                heatRisk = riskData.heatRisk.level.name,
                rainRisk = riskData.rainRisk.level.name,
                yieldRisk = riskData.yieldRisk.level.name,
                marketRisk = riskData.marketRisk.level.name,
                hardwareRisk = riskData.hardwareRisk.level.name,
                explanation = riskData.executiveSummary
            )

            CombinedIntelligence(
                briefing = briefing,
                actionPlan = actionPlan,
                yieldPrediction = yieldPrediction,
                irrigationAdvice = irrigationAdvice,
                marketAdvice = marketAdvice,
                riskProfile = riskProfile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

