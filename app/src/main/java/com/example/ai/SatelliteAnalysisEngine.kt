package com.example.ai

/**
 * Satellite remote sensing analysis.
 */
data class SatelliteAnalysisResult(
    val ndvi: Float,
    val evi: Float,
    val vegetationVigorCategory: String, // "High Vigor", "Moderate Vigor", "Vegetation Stress", "Bare / Sparse"
    val canopyUniformityPercent: Int,
    val isAnomalyDetected: Boolean,
    val anomalyDescription: String,
    val satelliteSource: String, // "Sentinel-2 MSI", "Landsat-9 OLI-2"
    val resolutionMeters: Int,
    val acquisitionDate: String,
    val groundActionPrompt: String,
    val safetyDisclaimer: String = "Satellite spectral indices detect canopy vigor and chlorophyll reflectance differences. Satellite cannot distinguish specific fungal, bacterial, or pest pathogens; ground scouting is always required to confirm root causes."
)

/**
 * SatelliteAnalysisEngine
 *
 * Grounded remote sensing engine strictly following agricultural safety rules.
 * Never claims "satellite diagnosed disease"; reports vegetation vigor and anomalies for ground inspection.
 */
object SatelliteAnalysisEngine {

    fun analyzeSatelliteData(state: DigitalFarmState): SatelliteAnalysisResult {
        val ndvi = state.satelliteData.ndvi
        val evi = state.satelliteData.evi

        val vigorCategory = when {
            ndvi >= 0.75f -> "High Vigor (Dense Healthy Canopy)"
            ndvi >= 0.60f -> "Moderate Vigor (Normal Development)"
            ndvi >= 0.40f -> "Vegetation Stress (Foliar Thinning)"
            else -> "Sparse / Bare Canopy"
        }

        val uniformity = when {
            ndvi >= 0.70f -> 92
            ndvi >= 0.55f -> 81
            else -> 68
        }

        val isAnomaly = state.satelliteData.anomalyDetected || ndvi < 0.55f
        val anomalyDesc = if (isAnomaly) {
            state.satelliteData.anomalyDetail.ifBlank {
                "Vegetation stress detected in localized sector. Lower NDVI reflectance indicates reduced chlorophyll density."
            }
        } else {
            "Canopy growth is uniform across the entire parcel with no spatial anomalies."
        }

        val actionPrompt = if (isAnomaly) {
            "Vegetation stress detected in satellite imagery. Ground inspection recommended to check for localized moisture deficit or pest hotspots."
        } else {
            "Canopy density is progressing normally according to seasonal NDVI expectations."
        }

        return SatelliteAnalysisResult(
            ndvi = ndvi,
            evi = evi,
            vegetationVigorCategory = vigorCategory,
            canopyUniformityPercent = uniformity,
            isAnomalyDetected = isAnomaly,
            anomalyDescription = anomalyDesc,
            satelliteSource = "Sentinel-2 Multi-Spectral Instrument (ESA)",
            resolutionMeters = 10,
            acquisitionDate = state.satelliteData.lastPassDate,
            groundActionPrompt = actionPrompt
        )
    }
}
