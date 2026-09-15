package com.example.ai

import kotlinx.serialization.Serializable

@Serializable
data class DailyFarmBriefing(
    val healthScore: Int, // e.g. 82
    val healthTrendMessage: String, // e.g. "Stable"
    val cropName: String,
    val growthStage: String,
    val weatherTemp: Int,
    val rainRisk: String, // "Low", "Medium", "High"
    val soilMoisture: Int,
    val diseaseRisk: String, // "Low", "Medium", "High"
    val diseaseRiskExplanation: String,
    val marketPrice: String,
    val topPriorities: List<String>
)

@Serializable
data class FarmActionPlan(
    val timeline: List<DailyAction>
)

@Serializable
data class DailyAction(
    val dayLabel: String, // "TODAY", "TOMORROW", "DAY 3"
    val actionType: String,
    val description: String
)

@Serializable
data class YieldPrediction(
    val expectedYield: String,
    val potentialYield: String,
    val yieldGapPercent: Int,
    val confidence: String, // "High", "Medium", "Low"
    val risk: String,
    val topLimitingFactors: List<String>
)

@Serializable
data class IrrigationAdvice(
    val recommendation: String, // "RECOMMENDED", "WAIT", "NOT REQUIRED"
    val reason: String,
    val amountEstimate: String,
    val optimalTiming: String
)

@Serializable
data class MarketAdvice(
    val recommendation: String, // "SELL NOW", "WAIT", "MONITOR"
    val reason: String,
    val confidence: String,
    val risks: List<String>
)

@Serializable
data class FarmRiskProfile(
    val overallRisk: String, // "LOW", "MEDIUM", "HIGH"
    val diseaseRisk: String,
    val waterRisk: String,
    val heatRisk: String,
    val rainRisk: String,
    val yieldRisk: String,
    val marketRisk: String,
    val hardwareRisk: String,
    val explanation: String
)

@Serializable
data class CombinedIntelligence(
    val briefing: DailyFarmBriefing,
    val actionPlan: FarmActionPlan,
    val yieldPrediction: YieldPrediction,
    val irrigationAdvice: IrrigationAdvice,
    val marketAdvice: MarketAdvice,
    val riskProfile: FarmRiskProfile
)
