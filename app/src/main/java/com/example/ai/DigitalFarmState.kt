package com.example.ai

import com.example.core.location.FarmLocation
import com.example.data.local.FarmCropEntity
import com.example.data.local.ScanRecordEntity
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import kotlinx.serialization.Serializable

/**
 * Metadata tracking the origin, freshness, and quality of specific farm data points.
 */
@Serializable
data class DataSourceMeta(
    val sourceName: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isLive: Boolean = true,
    val isFresh: Boolean = true,
    val missingReason: String? = null
) {
    fun getAgeFormatted(): String {
        val diffMs = System.currentTimeMillis() - timestampMs
        val mins = diffMs / (1000 * 60)
        return when {
            mins < 1 -> "Just now"
            mins < 60 -> "${mins}m ago"
            mins < 1440 -> "${mins / 60}h ago"
            else -> "${mins / 1440}d ago"
        }
    }
}

/**
 * Computed active crop context including stage, duration, and growth trajectory.
 */
@Serializable
data class ActiveCropContext(
    val cropName: String = "Tomato",
    val variety: String = "Abhinav Hybrid",
    val areaAcres: Double = 2.5,
    val sowingDate: String = "2026-01-28",
    val daysAfterSowing: Int = 48,
    val currentStage: String = "Flowering",
    val nextStage: String = "Fruit Development",
    val daysToNextStage: Int = 12,
    val stageProgressPercent: Int = 65,
    val expectedHarvestWindow: String = "15 Mar - 30 Mar 2026",
    val soilType: String = "Medium Black Loam"
)

/**
 * Soil parameters and lab/sensor readings.
 */
@Serializable
data class SoilContext(
    val soilType: String = "Medium Black Loam",
    val moisturePercent: Int = 28,
    val targetMoistureMin: Int = 30,
    val targetMoistureMax: Int = 45,
    val ph: Float = 6.8f,
    val nitrogenKgPerHa: Int = 180,
    val phosphorusKgPerHa: Int = 42,
    val potassiumKgPerHa: Int = 210,
    val organicCarbonPercent: Float = 0.65f,
    val lastTestedDate: String = "Nov 2025"
)

/**
 * IoT / ESP32 Sensor telemetry context.
 */
@Serializable
data class SensorContext(
    val deviceId: String = "ESP32_FARM_001",
    val deviceOnline: Boolean = true,
    val soilMoisturePercent: Int = 28,
    val soilTempC: Float = 24.2f,
    val airTempC: Float = 29.5f,
    val airHumidityPercent: Int = 68,
    val ambientLightLux: Int = 45000,
    val batteryPercent: Int = 92,
    val isAnomalyDetected: Boolean = false,
    val anomalyDescription: String? = null,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Satellite / Remote sensing context (Sentinel-2 / Landsat).
 */
@Serializable
data class SatelliteContext(
    val ndvi: Float = 0.68f, // 0.0 to 1.0 Normalized Difference Vegetation Index
    val evi: Float = 0.54f,  // Enhanced Vegetation Index
    val vegetationHealth: String = "Moderate Stress", // "Optimal", "Good", "Moderate Stress", "Severe Stress"
    val anomalyDetected: Boolean = true,
    val anomalyDetail: String = "Canopy moisture deficit in southwest quadrant",
    val cloudCoverPercent: Int = 8,
    val lastPassDate: String = "12 Feb 2026"
)

/**
 * Local Mandi and market pricing context.
 */
@Serializable
data class MarketContext(
    val commodity: String = "Tomato",
    val mandiName: String = "Nashik APMC",
    val modalPricePerQ: Int = 2450,
    val minPricePerQ: Int = 2100,
    val maxPricePerQ: Int = 2800,
    val change7dPercent: Float = 6.2f,
    val forecast7dRange: String = "₹2,480 – ₹2,650",
    val forecast14dRange: String = "₹2,550 – ₹2,800",
    val trend: String = "Increasing", // "Increasing", "Decreasing", "Stable"
    val isLive: Boolean = true,
    val lastUpdatedDate: String = "Today, 06:00 AM"
)

/**
 * Farmer action feedback record.
 */
@Serializable
data class FarmerActionRecord(
    val id: String,
    val actionType: String, // "IRRIGATION", "SPRAY", "FERTILIZER", "SCOUTING"
    val description: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val status: String = "DONE", // "DONE", "SKIPPED", "NOT_HELPFUL"
    val feedbackNotes: String? = null
)

/**
 * Historical yield record for past harvest benchmarking.
 */
@Serializable
data class YieldHistoryRecord(
    val season: String = "Rabi 2024-25",
    val cropName: String = "Tomato",
    val variety: String = "Abhinav Hybrid",
    val actualYieldTonnesPerAcre: Double = 2.95,
    val benchmarkDistrictYield: Double = 2.80
)

/**
 * DigitalFarmState
 *
 * Centralized, unified digital representation of a farmer's farm.
 * Consumed across all AI and decision support modules.
 */
data class DigitalFarmState(
    val farmerProfile: FarmerProfile,
    val farmLocation: FarmLocation?,
    val weather: WeatherInfo,
    val crops: List<FarmCropEntity>,
    val recentScans: List<ScanRecordEntity>,
    val activeCropContext: ActiveCropContext = ActiveCropContext(),
    val soil: SoilContext = SoilContext(),
    val sensorData: SensorContext = SensorContext(),
    val satelliteData: SatelliteContext = SatelliteContext(),
    val marketData: MarketContext = MarketContext(),
    val farmerActions: List<FarmerActionRecord> = emptyList(),
    val yieldHistory: List<YieldHistoryRecord> = emptyList(),
    val dataProvenance: Map<String, DataSourceMeta> = emptyMap()
) {
    fun toContextString(): String {
        val sb = StringBuilder()
        sb.append("FARM CONTEXT:\n")
        sb.append("Farmer: ${farmerProfile.name}\n")
        sb.append("Farm: ${farmerProfile.farmName} (${farmerProfile.village}, ${farmerProfile.state})\n")
        sb.append("Total Acreage: ${farmerProfile.totalLandAcres} acres\n")
        sb.append("Soil Type: ${soil.soilType}, pH: ${soil.ph}\n\n")

        sb.append("ACTIVE CROP:\n")
        sb.append("Crop: ${activeCropContext.cropName} (${activeCropContext.variety})\n")
        sb.append("Acreage: ${activeCropContext.areaAcres} acres\n")
        sb.append("Sowing Date: ${activeCropContext.sowingDate} (DAS: ${activeCropContext.daysAfterSowing} days)\n")
        sb.append("Current Stage: ${activeCropContext.currentStage} (${activeCropContext.stageProgressPercent}% progress)\n")
        sb.append("Next Stage: ${activeCropContext.nextStage} in ${activeCropContext.daysToNextStage} days\n")
        sb.append("Expected Harvest: ${activeCropContext.expectedHarvestWindow}\n\n")

        sb.append("CURRENT WEATHER & CLIMATE:\n")
        sb.append("Temperature: ${weather.temperatureC}°C (Range: ${weather.temperatureC - 4}°C to ${weather.temperatureC + 3}°C)\n")
        sb.append("Condition: ${weather.condition}\n")
        sb.append("Humidity: ${weather.humidityPercent}%\n")
        sb.append("Rain Probability (24-48h): ${weather.rainProbabilityPercent}%\n")
        sb.append("Rainfall (past 24h): ${weather.rainfallMm} mm\n\n")

        sb.append("IOT SENSOR TELEMETRY (${sensorData.deviceId}):\n")
        sb.append("Device Status: ${if (sensorData.deviceOnline) "ONLINE" else "OFFLINE"}\n")
        sb.append("Soil Moisture: ${sensorData.soilMoisturePercent}% (Target: ${soil.targetMoistureMin}-${soil.targetMoistureMax}%)\n")
        sb.append("Soil Temp: ${sensorData.soilTempC}°C | Air Temp: ${sensorData.airTempC}°C\n")
        sb.append("Air Humidity: ${sensorData.airHumidityPercent}%\n")
        if (sensorData.isAnomalyDetected) {
            sb.append("Sensor Anomaly Alert: ${sensorData.anomalyDescription}\n")
        }
        sb.append("\n")

        sb.append("SATELLITE VEGETATION INTELLIGENCE:\n")
        sb.append("NDVI Index: ${sensorData.soilMoisturePercent} (Health: ${satelliteData.vegetationHealth})\n")
        sb.append("Canopy Observation: ${satelliteData.anomalyDetail}\n")
        sb.append("Last Observation: ${satelliteData.lastPassDate}\n\n")

        sb.append("DISEASE SCAN HISTORY:\n")
        if (recentScans.isEmpty()) {
            sb.append("No active crop diseases recorded.\n")
        } else {
            recentScans.take(3).forEach { scan ->
                val health = if (scan.isHealthy) "Healthy" else "Infected (${scan.diseaseName}, Severity: ${scan.severity})"
                sb.append("- ${scan.cropName}: $health (${scan.timestamp})\n")
            }
        }
        sb.append("\n")

        sb.append("MARKET INTELLIGENCE:\n")
        sb.append("Mandi: ${marketData.mandiName} | Commodity: ${marketData.commodity}\n")
        sb.append("Modal Price: ₹${marketData.modalPricePerQ}/quintal (Range: ₹${marketData.minPricePerQ} - ₹${marketData.maxPricePerQ})\n")
        sb.append("7-Day Trend: ${marketData.trend} (+${marketData.change7dPercent}%)\n")
        sb.append("7-Day Forecast: ${marketData.forecast7dRange}\n")

        return sb.toString()
    }
}
