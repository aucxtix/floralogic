package com.example.ai

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Stage definition within a crop growth cycle.
 */
data class GrowthStageDefinition(
    val stageName: String,
    val startDay: Int,
    val endDay: Int,
    val waterSensitivity: Float, // 1.0 = normal, 1.5 = high (flowering), 0.8 = low
    val description: String
)

/**
 * Configurable crop stage lifecycle profile.
 */
data class CropLifecycleProfile(
    val cropName: String,
    val defaultVariety: String,
    val totalDurationDays: Int,
    val stages: List<GrowthStageDefinition>,
    val harvestWindowDays: Int = 15
)

/**
 * Calculated result from CropStageEngine.
 */
data class CropStageAnalysis(
    val cropName: String,
    val variety: String,
    val sowingDate: String,
    val daysAfterSowing: Int,
    val currentStage: String,
    val stageDescription: String,
    val stageProgressPercent: Int,
    val nextStage: String,
    val daysToNextStage: Int,
    val expectedHarvestWindow: String,
    val waterSensitivityFactor: Float
)

/**
 * CropStageEngine
 *
 * Computes growth stage, days after sowing (DAS), next stage countdown, and harvest timeline.
 * Supports configurable crop profiles rather than hardcoded global ranges.
 */
object CropStageEngine {

    private val profiles: MutableMap<String, CropLifecycleProfile> = mutableMapOf()

    init {
        registerDefaultProfiles()
    }

    private fun registerDefaultProfiles() {
        // Tomato Profile
        registerProfile(
            CropLifecycleProfile(
                cropName = "Tomato",
                defaultVariety = "Abhinav Hybrid",
                totalDurationDays = 120,
                stages = listOf(
                    GrowthStageDefinition("Establishment / Nursery", 0, 15, 0.9f, "Seedling rooting and establishment"),
                    GrowthStageDefinition("Vegetative Growth", 16, 35, 1.1f, "Canopy and stem expansion"),
                    GrowthStageDefinition("Flowering & Fruit Set", 36, 60, 1.5f, "Peak water & nutrient sensitivity; blossom set"),
                    GrowthStageDefinition("Fruit Development", 61, 90, 1.3f, "Fruit sizing, green fruit accumulation"),
                    GrowthStageDefinition("Maturity & Harvest", 91, 120, 0.8f, "Color break, ripening, and picking")
                ),
                harvestWindowDays = 20
            )
        )

        // Rice / Paddy Profile
        registerProfile(
            CropLifecycleProfile(
                cropName = "Rice / Paddy",
                defaultVariety = "Basmati / PR 126",
                totalDurationDays = 135,
                stages = listOf(
                    GrowthStageDefinition("Nursery & Transplanting", 0, 20, 1.2f, "Seedling growth in puddled nursery"),
                    GrowthStageDefinition("Tillering Stage", 21, 50, 1.3f, "Active tiller emergence and root anchor"),
                    GrowthStageDefinition("Panicle Initiation & Booting", 51, 80, 1.6f, "Stem elongation and reproductive onset"),
                    GrowthStageDefinition("Flowering & Heading", 81, 105, 1.7f, "Anthesis, pollination, high drought sensitivity"),
                    GrowthStageDefinition("Grain Filling & Ripening", 106, 135, 0.9f, "Milky to dough stage, golden drying")
                ),
                harvestWindowDays = 15
            )
        )

        // Cotton Profile
        registerProfile(
            CropLifecycleProfile(
                cropName = "Cotton",
                defaultVariety = "Bt Cotton RCH-659",
                totalDurationDays = 160,
                stages = listOf(
                    GrowthStageDefinition("Emergence & Seedling", 0, 25, 0.8f, "Initial taproot growth and first true leaves"),
                    GrowthStageDefinition("Squaring (Budding)", 26, 60, 1.2f, "Square formation and vegetative branch framing"),
                    GrowthStageDefinition("Peak Flowering", 61, 95, 1.6f, "White and pink flowers, peak water requirement"),
                    GrowthStageDefinition("Boll Development", 96, 135, 1.4f, "Boll enlargement and fiber filling"),
                    GrowthStageDefinition("Boll Bursting & Picking", 136, 160, 0.6f, "Dry sunny weather required for clean cotton lint")
                ),
                harvestWindowDays = 25
            )
        )

        // Wheat Profile
        registerProfile(
            CropLifecycleProfile(
                cropName = "Wheat",
                defaultVariety = "HD-2967 / Lokwan",
                totalDurationDays = 125,
                stages = listOf(
                    GrowthStageDefinition("CRI (Crown Root Initiation)", 0, 25, 1.5f, "Most critical irrigation stage"),
                    GrowthStageDefinition("Tillering & Jointing", 26, 60, 1.2f, "Tillers and internode elongation"),
                    GrowthStageDefinition("Booting & Flowering", 61, 85, 1.4f, "Ear emergence and grain fertilization"),
                    GrowthStageDefinition("Milking & Dough Stage", 86, 110, 1.1f, "Grain fattening, starch storage"),
                    GrowthStageDefinition("Maturity & Harvest", 111, 125, 0.5f, "Golden dry grain, moisture < 12%")
                ),
                harvestWindowDays = 14
            )
        )

        // Potato Profile
        registerProfile(
            CropLifecycleProfile(
                cropName = "Potato",
                defaultVariety = "Kufri Jyoti / Pukhraj",
                totalDurationDays = 95,
                stages = listOf(
                    GrowthStageDefinition("Sprout Development", 0, 15, 0.8f, "Eyes emerge from tuber seed pieces"),
                    GrowthStageDefinition("Vegetative Growth", 16, 35, 1.1f, "Stem and leafy canopy spreading"),
                    GrowthStageDefinition("Tuber Initiation", 36, 55, 1.5f, "Stolon tips swell into tubers; critical moisture"),
                    GrowthStageDefinition("Tuber Bulking", 56, 80, 1.4f, "Rapid starch deposition and tuber expansion"),
                    GrowthStageDefinition("Maturation & Skin Set", 81, 95, 0.7f, "Haulm cutting/senescence for skin hardening")
                ),
                harvestWindowDays = 12
            )
        )

        // Maize Profile
        registerProfile(
            CropLifecycleProfile(
                cropName = "Maize",
                defaultVariety = "Pioneer P3396",
                totalDurationDays = 110,
                stages = listOf(
                    GrowthStageDefinition("Seedling & Vegetative (V4-V8)", 0, 30, 1.0f, "Knee-high growth and nodal root development"),
                    GrowthStageDefinition("Tasseling & Silking", 31, 55, 1.8f, "Critical pollination window; high drought sensitivity"),
                    GrowthStageDefinition("Blister & Milk Stage", 56, 75, 1.3f, "Kernel fluid development"),
                    GrowthStageDefinition("Dough & Dent Stage", 76, 95, 1.0f, "Starch compaction in kernels"),
                    GrowthStageDefinition("Black Layer Maturity", 96, 110, 0.6f, "Physiological maturity and cob dry-down")
                ),
                harvestWindowDays = 15
            )
        )
    }

    fun registerProfile(profile: CropLifecycleProfile) {
        profiles[profile.cropName.lowercase().trim()] = profile
    }

    /**
     * Calculates current stage and trajectory from crop parameters.
     */
    fun analyzeCropStage(
        cropName: String,
        variety: String,
        sowingDateStr: String,
        expectedDurationDays: Int? = null
    ): CropStageAnalysis {
        val normalizedName = normalizeCropKey(cropName)
        val profile = profiles[normalizedName] ?: createGenericProfile(cropName, expectedDurationDays ?: 110)

        val daysAfterSowing = calculateDaysAfterSowing(sowingDateStr)
        val clampedDas = maxOf(0, daysAfterSowing)

        // Find matching growth stage
        val currentStageDef = profile.stages.firstOrNull { clampedDas in it.startDay..it.endDay }
            ?: profile.stages.last()

        val stageIndex = profile.stages.indexOf(currentStageDef)
        val nextStageDef = profile.stages.getOrNull(stageIndex + 1)

        val stageSpan = (currentStageDef.endDay - currentStageDef.startDay).coerceAtLeast(1)
        val stageElapsed = (clampedDas - currentStageDef.startDay).coerceIn(0, stageSpan)
        val progressPercent = ((stageElapsed.toFloat() / stageSpan) * 100).toInt().coerceIn(0, 100)

        val daysToNext = if (nextStageDef != null) {
            (currentStageDef.endDay - clampedDas).coerceAtLeast(1)
        } else {
            0
        }

        val harvestWindow = calculateHarvestWindow(sowingDateStr, profile.totalDurationDays, profile.harvestWindowDays)

        return CropStageAnalysis(
            cropName = cropName,
            variety = variety.ifBlank { profile.defaultVariety },
            sowingDate = sowingDateStr,
            daysAfterSowing = clampedDas,
            currentStage = currentStageDef.stageName,
            stageDescription = currentStageDef.description,
            stageProgressPercent = progressPercent,
            nextStage = nextStageDef?.stageName ?: "Harvest Complete",
            daysToNextStage = daysToNext,
            expectedHarvestWindow = harvestWindow,
            waterSensitivityFactor = currentStageDef.waterSensitivity
        )
    }

    private fun normalizeCropKey(name: String): String {
        val lower = name.lowercase().trim()
        return when {
            lower.contains("tomato") -> "tomato"
            lower.contains("rice") || lower.contains("paddy") || lower.contains("dhan") -> "rice / paddy"
            lower.contains("cotton") || lower.contains("kapas") -> "cotton"
            lower.contains("wheat") || lower.contains("gehun") -> "wheat"
            lower.contains("potato") || lower.contains("aalu") -> "potato"
            lower.contains("maize") || lower.contains("corn") || lower.contains("makka") -> "maize"
            else -> lower
        }
    }

    private fun createGenericProfile(cropName: String, durationDays: Int): CropLifecycleProfile {
        val d = durationDays.coerceIn(60, 240)
        return CropLifecycleProfile(
            cropName = cropName,
            defaultVariety = "Standard Cultivar",
            totalDurationDays = d,
            stages = listOf(
                GrowthStageDefinition("Establishment", 0, (d * 0.15).toInt(), 1.0f, "Germination and establishment"),
                GrowthStageDefinition("Vegetative Growth", (d * 0.15).toInt() + 1, (d * 0.40).toInt(), 1.1f, "Canopy expansion"),
                GrowthStageDefinition("Flowering & Reproductive", (d * 0.40).toInt() + 1, (d * 0.65).toInt(), 1.5f, "Reproductive flowering window"),
                GrowthStageDefinition("Maturation", (d * 0.65).toInt() + 1, d, 0.8f, "Maturity to harvest")
            )
        )
    }

    private fun calculateDaysAfterSowing(sowingDateStr: String): Int {
        val formats = listOf("yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy", "yyyy/MM/dd")
        var parsedDate: Date? = null
        for (fmt in formats) {
            try {
                parsedDate = SimpleDateFormat(fmt, Locale.getDefault()).parse(sowingDateStr)
                if (parsedDate != null) break
            } catch (_: Exception) {}
        }

        if (parsedDate == null) {
            // Default reasonable DAS when date string is unparseable or relative
            return 45
        }

        val now = Date()
        val diff = now.time - parsedDate.time
        return TimeUnit.MILLISECONDS.toDays(diff).toInt().coerceAtLeast(0)
    }

    private fun calculateHarvestWindow(sowingDateStr: String, durationDays: Int, windowDays: Int): String {
        val formats = listOf("yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy")
        var parsedDate: Date? = null
        for (fmt in formats) {
            try {
                parsedDate = SimpleDateFormat(fmt, Locale.getDefault()).parse(sowingDateStr)
                if (parsedDate != null) break
            } catch (_: Exception) {}
        }

        val baseCal = Calendar.getInstance()
        if (parsedDate != null) {
            baseCal.time = parsedDate
        } else {
            baseCal.add(Calendar.DAY_OF_YEAR, -45)
        }

        baseCal.add(Calendar.DAY_OF_YEAR, durationDays - (windowDays / 2))
        val outputFmt = SimpleDateFormat("dd MMM", Locale.getDefault())
        val startStr = outputFmt.format(baseCal.time)

        baseCal.add(Calendar.DAY_OF_YEAR, windowDays)
        val endYearFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val endStr = endYearFmt.format(baseCal.time)

        return "$startStr – $endStr"
    }
}
