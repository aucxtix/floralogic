package com.example.ai

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 7-Day scheduled agronomic action item.
 */
data class PlannedFarmAction(
    val dayOffset: Int, // 0 = Today, 1 = Tomorrow, 2 = Day 3...
    val dayLabel: String, // "TODAY", "TOMORROW", "THU, 19 FEB"
    val dateString: String,
    val category: String, // "IRRIGATION", "SPRAY", "NUTRITION", "SCOUTING", "HARVEST"
    val actionHeadline: String,
    val agronomicRationale: String,
    val priority: RecommendationPriority,
    val isCompleted: Boolean = false
)

/**
 * Complete structured Daily AI Farm Briefing.
 */
data class DailyFarmBriefingData(
    val dateFormatted: String,
    val farmHealthScore: Int,
    val cropHealthStatus: String,
    val primaryCropName: String,
    val currentGrowthStage: String,
    val daysAfterSowing: Int,
    val weatherOverview: String,
    val temperatureC: Int,
    val rainfallChancePercent: Int,
    val irrigationStatus: String,
    val diseaseRiskAlert: String,
    val marketPriceHighlight: String,
    val yieldForecastMidpoint: Double,
    val topPriorities: List<GroundedRecommendation>,
    val sevenDayPlan: List<PlannedFarmAction>
)

/**
 * DailyFarmBriefingEngine & FarmPlanEngine
 *
 * Synthesizes all AI modules into an executive morning briefing and a realistic 7-day agronomic calendar.
 */
object DailyFarmBriefingEngine {

    fun generateBriefing(state: DigitalFarmState): DailyFarmBriefingData {
        val todayFmt = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(java.util.Date())

        val farmHealth = FarmHealthEngine.evaluateFarmHealth(state)
        val cropHealth = CropHealthEngine.evaluateCropHealth(state)
        val irrigation = IrrigationDecisionEngine.evaluateIrrigation(state)
        val risks = FarmRiskEngine.evaluateFarmRisks(state)
        val market = MarketForecastEngine.forecastPrices(state)
        val yield = YieldPredictionEngine.predictYield(state)
        val topRecs = FarmRecommendationEngine.generateRecommendations(state).take(3)
        val plan = generate7DayPlan(state, topRecs, irrigation)

        val weatherOverview = "${state.weather.condition}, ${state.weather.temperatureC}°C (${state.weather.humidityPercent}% RH, ${state.weather.rainProbabilityPercent}% Rain)"
        val irrigationStatus = "${irrigation.action.label}: ${irrigation.headlineReason}"
        val diseaseAlert = "${risks.diseaseRisk.level.label} Risk: ${risks.diseaseRisk.primaryReason}"
        val marketHighlight = "₹${market.currentPricePerQ}/q (${market.projectedTrend}, 7d forecast: ₹${market.forecast7dMin}–₹${market.forecast7dMax})"

        return DailyFarmBriefingData(
            dateFormatted = todayFmt,
            farmHealthScore = farmHealth.farmHealthScore,
            cropHealthStatus = "${cropHealth.healthScore}/100 (${cropHealth.healthCategory})",
            primaryCropName = state.activeCropContext.cropName,
            currentGrowthStage = state.activeCropContext.currentStage,
            daysAfterSowing = state.activeCropContext.daysAfterSowing,
            weatherOverview = weatherOverview,
            temperatureC = state.weather.temperatureC,
            rainfallChancePercent = state.weather.rainProbabilityPercent,
            irrigationStatus = irrigationStatus,
            diseaseRiskAlert = diseaseAlert,
            marketPriceHighlight = marketHighlight,
            yieldForecastMidpoint = yield.expectedYieldMidpoint,
            topPriorities = topRecs,
            sevenDayPlan = plan
        )
    }

    private fun generate7DayPlan(
        state: DigitalFarmState,
        topRecs: List<GroundedRecommendation>,
        irrigation: IrrigationAdviceResult
    ): List<PlannedFarmAction> {
        val plan = mutableListOf<PlannedFarmAction>()
        val cal = Calendar.getInstance()
        val dayNameFmt = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())

        // Day 0 (Today)
        val todayRec = topRecs.firstOrNull()
        plan.add(
            PlannedFarmAction(
                dayOffset = 0,
                dayLabel = "TODAY",
                dateString = dayNameFmt.format(cal.time),
                category = todayRec?.category ?: "MONITORING",
                actionHeadline = todayRec?.what ?: "Inspect active root zone moisture and record plant vigor.",
                agronomicRationale = todayRec?.why ?: "Routine morning farm assessment.",
                priority = todayRec?.priority ?: RecommendationPriority.MEDIUM
            )
        )

        // Day 1 (Tomorrow)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowAction = if (irrigation.action == IrrigationAction.IRRIGATE) {
            PlannedFarmAction(
                dayOffset = 1,
                dayLabel = "TOMORROW",
                dateString = dayNameFmt.format(cal.time),
                category = "IRRIGATION",
                actionHeadline = "Run drip irrigation cycle (${irrigation.recommendedDripHours ?: 2.5} hrs) early morning.",
                agronomicRationale = "Recharge root zone before afternoon peak temperature.",
                priority = RecommendationPriority.HIGH
            )
        } else {
            PlannedFarmAction(
                dayOffset = 1,
                dayLabel = "TOMORROW",
                dateString = dayNameFmt.format(cal.time),
                category = "CROP CARE",
                actionHeadline = "Inspect lower leaf canopy for early lesion spots or whitefly vectors.",
                agronomicRationale = "High relative humidity (${state.weather.humidityPercent}%) creates favorable spore window.",
                priority = RecommendationPriority.MEDIUM
            )
        }
        plan.add(tomorrowAction)

        // Day 2 (Day 3)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        plan.add(
            PlannedFarmAction(
                dayOffset = 2,
                dayLabel = "DAY 3",
                dateString = dayNameFmt.format(cal.time),
                category = "NUTRITION",
                actionHeadline = "Foliar nutrient booster (Potassium Nitrate 13:0:45 @ 5g/L).",
                agronomicRationale = "Support flower retention and fruit cell division during ${state.activeCropContext.currentStage}.",
                priority = RecommendationPriority.MEDIUM
            )
        )

        // Day 3 (Day 4)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        plan.add(
            PlannedFarmAction(
                dayOffset = 3,
                dayLabel = "DAY 4",
                dateString = dayNameFmt.format(cal.time),
                category = "HARDWARE",
                actionHeadline = "Wipe ESP32 solar collector and check sensor probe cable seating.",
                agronomicRationale = "Ensure zero telemetry dropouts during weekly irrigation cycle.",
                priority = RecommendationPriority.LOW
            )
        )

        // Day 4 (Day 5)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        plan.add(
            PlannedFarmAction(
                dayOffset = 4,
                dayLabel = "DAY 5",
                dateString = dayNameFmt.format(cal.time),
                category = "SCOUTING",
                actionHeadline = "Scan 5 symptomatic leaves with Kisan AI on-device scanner.",
                agronomicRationale = "Track disease remission and update disease progression timeline.",
                priority = RecommendationPriority.MEDIUM
            )
        )

        // Day 5 (Day 6)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        plan.add(
            PlannedFarmAction(
                dayOffset = 5,
                dayLabel = "DAY 6",
                dateString = dayNameFmt.format(cal.time),
                category = "MARKET",
                actionHeadline = "Review wholesale APMC auction price arrival trends.",
                agronomicRationale = "Compare ${state.marketData.mandiName} modal prices ahead of harvest planning.",
                priority = RecommendationPriority.LOW
            )
        )

        // Day 6 (Day 7)
        cal.add(Calendar.DAY_OF_YEAR, 1)
        plan.add(
            PlannedFarmAction(
                dayOffset = 6,
                dayLabel = "DAY 7",
                dateString = dayNameFmt.format(cal.time),
                category = "IRRIGATION",
                actionHeadline = "Secondary moisture assessment and soil ball plasticity check.",
                agronomicRationale = "Verify root moisture saturation after weekly irrigation.",
                priority = RecommendationPriority.MEDIUM
            )
        )

        return plan
    }
}
