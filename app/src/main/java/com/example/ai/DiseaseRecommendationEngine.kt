package com.example.ai

import com.example.data.local.ScanRecordEntity
import com.example.data.model.CropDisease
import com.example.data.model.DiseaseSeverity
import com.example.data.model.WeatherInfo

/**
 * Confidence level categorization for disease detection.
 */
enum class ConfidenceGrade(val label: String, val thresholdRange: String) {
    HIGH("High Confidence", "> 85%"),
    MEDIUM("Medium Confidence", "60% – 85%"),
    LOW("Low Confidence", "< 60%")
}

/**
 * Disease Progression Analysis result across chronological scans.
 */
data class DiseaseProgressionAnalysis(
    val status: String, // "Improving", "Stable", "Worsening", "Insufficient Data"
    val summary: String,
    val previousSeverity: String?,
    val currentSeverity: String,
    val recommendation: String,
    val scansComparedCount: Int
)

/**
 * Structured diagnostic recommendation output.
 */
data class ActionableDiseaseRecommendation(
    val diseaseName: String,
    val confidenceGrade: ConfidenceGrade,
    val confidenceScore: Float,
    val severityLabel: String,
    val immediateActions: List<String>,
    val monitoringPlan: List<String>,
    val preventiveGuidance: List<String>,
    val escalationNotice: String,
    val safetyCautions: List<String>,
    val isConfidentDiagnosis: Boolean
)

/**
 * DiseaseRecommendationEngine
 *
 * Synthesizes model classification, confidence evaluation, severity estimation,
 * environmental factors (temperature, humidity, rain forecast), and crop stage
 * to generate safe, explainable, actionable disease recommendations.
 */
object DiseaseRecommendationEngine {

    // Configurable confidence thresholds
    var HIGH_CONFIDENCE_THRESHOLD = 0.85f
    var MEDIUM_CONFIDENCE_THRESHOLD = 0.60f

    fun evaluateConfidence(confidence: Float): ConfidenceGrade {
        return when {
            confidence >= HIGH_CONFIDENCE_THRESHOLD -> ConfidenceGrade.HIGH
            confidence >= MEDIUM_CONFIDENCE_THRESHOLD -> ConfidenceGrade.MEDIUM
            else -> ConfidenceGrade.LOW
        }
    }

    /**
     * Estimates severity from lesion coverage and visual indicators.
     * Returns "Severity assessment unavailable" if reliable evidence is lacking.
     */
    fun estimateSeverity(
        isHealthy: Boolean,
        symptomCoveragePercent: Float?,
        detectedLeafCount: Int = 1
    ): String {
        if (isHealthy) return "None (Healthy Tissue)"
        if (symptomCoveragePercent == null) return "Severity assessment unavailable"

        return when {
            symptomCoveragePercent < 15f -> "Mild (Low Coverage < 15%)"
            symptomCoveragePercent <= 40f -> "Moderate (Coverage 15% – 40%)"
            else -> "Severe (Heavy Coverage > 40%)"
        }
    }

    fun generateRecommendation(
        detectedDisease: CropDisease,
        farmState: DigitalFarmState
    ): ActionableDiseaseRecommendation {
        return generateRecommendation(
            diseaseId = detectedDisease.id,
            rawConfidence = detectedDisease.confidence,
            severity = detectedDisease.severity,
            cropName = detectedDisease.cropName,
            growthStage = farmState.activeCropContext.currentStage,
            weather = null,
            soilMoisturePercent = farmState.soil.moisturePercent,
            recentScans = farmState.recentScans
        )
    }

    /**
     * Generates comprehensive disease recommendation incorporating microclimate and crop stage.
     */
    fun generateRecommendation(
        diseaseId: String,
        rawConfidence: Float,
        severity: DiseaseSeverity,
        cropName: String,
        growthStage: String,
        weather: WeatherInfo?,
        soilMoisturePercent: Int?,
        recentScans: List<ScanRecordEntity> = emptyList()
    ): ActionableDiseaseRecommendation {
        val confidenceGrade = evaluateConfidence(rawConfidence)
        val isConfident = confidenceGrade != ConfidenceGrade.LOW
        val knowledge = DiseaseKnowledgeBase.getKnowledge(diseaseId)

        val immediate = mutableListOf<String>()
        val monitoring = mutableListOf<String>()
        val prevention = mutableListOf<String>()
        val cautions = mutableListOf<String>()

        cautions.add("Strictly adhere to registered CIBRC product labels and recommended dilution rates.")
        cautions.add("Never mix unverified chemical formulations or spray under intense afternoon sun.")
        cautions.add("Wear protective mask and gloves; keep children and livestock away from treated plots.")

        if (!isConfident) {
            return ActionableDiseaseRecommendation(
                diseaseName = "Possible issue detected, but confidence is low (${(rawConfidence * 100).toInt()}%)",
                confidenceGrade = confidenceGrade,
                confidenceScore = rawConfidence,
                severityLabel = "Severity assessment unavailable (Low Certainty)",
                immediateActions = listOf(
                    "Do NOT apply broad-spectrum chemical sprays on low confidence detection.",
                    "Scout 10-15 random plants across the plot to see if symptoms are localized or widespread.",
                    "Take a clearer photo in natural morning daylight, holding the leaf flat."
                ),
                monitoringPlan = listOf(
                    "Re-scan symptomatic leaves in 24-48 hours.",
                    "Inspect underside of leaves with a magnifying glass for mite webbing or fungal spores."
                ),
                preventiveGuidance = listOf(
                    "Maintain standard drip irrigation without wetting upper leaves.",
                    "Ensure adequate drainage and eliminate standing water puddles."
                ),
                escalationNotice = "If symptoms spread visibly across rows within 2 days, consult the local Krishi Vigyan Kendra (KVK) or Block Agriculture Officer.",
                safetyCautions = cautions,
                isConfidentDiagnosis = false
            )
        }

        // Confident diagnosis: synthesize with weather and crop stage
        val isHumid = (weather?.humidityPercent ?: 50) > 75
        val isRainLikely = (weather?.rainProbabilityPercent ?: 0) > 40
        val isFlowering = growthStage.contains("Flower", ignoreCase = true) || growthStage.contains("Set", ignoreCase = true)

        if (severity == DiseaseSeverity.HIGH) {
            immediate.add("Isolate and rogue out severely damaged foliage; do NOT compost infected leaves on field borders.")
            if (isRainLikely) {
                immediate.add("Rainfall predicted within 48h: Delay foliar spraying until leaves are dry to avoid chemical wash-off.")
            } else {
                immediate.add("Apply targeted agronomic remedy: ${knowledge.organicRemedies}")
                immediate.add("If infection is advancing rapidly: ${knowledge.chemicalManagementGuideline} at ${knowledge.dosageFormula}")
            }
        } else {
            immediate.add("Prune isolated infected leaves touching the soil surface.")
            immediate.add("Preventive foliar treatment: ${knowledge.organicRemedies}")
        }

        if (isHumid) {
            immediate.add("High ambient humidity (${weather?.humidityPercent}%): Prune lower suckers to maximize canopy airflow.")
        }

        if (isFlowering) {
            immediate.add("Crop is in critical flowering stage: Avoid spraying during morning hours to protect honeybee pollinators.")
        }

        monitoring.add("Monitor leaf undersides every morning for fresh sporulation beads or lesions.")
        monitoring.add("Re-scan treated plants in 3-5 days to track disease arrest and remission.")

        prevention.addAll(knowledge.preventionPractices)

        val escalation = if (severity == DiseaseSeverity.HIGH) {
            "CRITICAL: ${knowledge.expertEscalationCriteria}"
        } else {
            knowledge.expertEscalationCriteria
        }

        return ActionableDiseaseRecommendation(
            diseaseName = knowledge.diseaseName,
            confidenceGrade = confidenceGrade,
            confidenceScore = rawConfidence,
            severityLabel = severity.label,
            immediateActions = immediate,
            monitoringPlan = monitoring,
            preventiveGuidance = prevention,
            escalationNotice = escalation,
            safetyCautions = cautions,
            isConfidentDiagnosis = true
        )
    }

    /**
     * Compares multiple chronological scans of the same crop to determine progression.
     */
    fun analyzeProgression(scans: List<ScanRecordEntity>): DiseaseProgressionAnalysis {
        if (scans.size < 2) {
            return DiseaseProgressionAnalysis(
                status = "Insufficient Data",
                summary = "At least 2 historical scans are required to compute disease progression trajectory.",
                previousSeverity = null,
                currentSeverity = scans.firstOrNull()?.severity ?: "Unknown",
                recommendation = "Capture sequential scans 3-4 days apart to monitor treatment efficacy.",
                scansComparedCount = scans.size
            )
        }

        val sorted = scans.sortedByDescending { it.id }
        val latest = sorted[0]
        val previous = sorted[1]

        val latestSeverityRank = rankSeverity(latest.severity)
        val prevSeverityRank = rankSeverity(previous.severity)

        return when {
            latest.isHealthy && !previous.isHealthy -> {
                DiseaseProgressionAnalysis(
                    status = "Improving",
                    summary = "Crop has recovered from previous ${previous.diseaseName} infection. Foliage shows clear healthy regeneration.",
                    previousSeverity = previous.severity,
                    currentSeverity = latest.severity,
                    recommendation = "Continue balanced maintenance; maintain scouting routine.",
                    scansComparedCount = scans.size
                )
            }
            latestSeverityRank < prevSeverityRank -> {
                DiseaseProgressionAnalysis(
                    status = "Improving",
                    summary = "Pathology severity has decreased from ${previous.severity} to ${latest.severity}. Lesion expansion appears arrested.",
                    previousSeverity = previous.severity,
                    currentSeverity = latest.severity,
                    recommendation = "Current management practice is working. Complete scheduled spray cycle and observe new growth.",
                    scansComparedCount = scans.size
                )
            }
            latestSeverityRank > prevSeverityRank -> {
                DiseaseProgressionAnalysis(
                    status = "Worsening",
                    summary = "Pathology severity has escalated from ${previous.severity} to ${latest.severity}. Symptoms are expanding.",
                    previousSeverity = previous.severity,
                    currentSeverity = latest.severity,
                    recommendation = "Immediate escalation recommended. Switch to secondary active ingredient or consult local KVK agronomist.",
                    scansComparedCount = scans.size
                )
            }
            else -> {
                DiseaseProgressionAnalysis(
                    status = "Stable",
                    summary = "Pathology symptoms remain stable at ${latest.severity}. No significant expansion or regression observed.",
                    previousSeverity = previous.severity,
                    currentSeverity = latest.severity,
                    recommendation = "Continue monitoring microclimate conditions and re-evaluate in 48 hours.",
                    scansComparedCount = scans.size
                )
            }
        }
    }

    private fun rankSeverity(label: String): Int {
        val l = label.lowercase()
        return when {
            l.contains("severe") || l.contains("high") -> 3
            l.contains("moderate") || l.contains("medium") -> 2
            l.contains("mild") || l.contains("low") -> 1
            else -> 0
        }
    }
}
