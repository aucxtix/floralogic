package com.example.core.services

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface AuthService {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun login(email: String, token: String)
    suspend fun logout()
    suspend fun restoreSession()
}

interface BiometricService {
    val isSupported: Boolean
    suspend fun authenticate(title: String, subtitle: String): Boolean
}

data class FarmProfile(
    val id: String,
    val name: String,
    val location: String,
    val areaAcres: Double,
    val soilType: String,
    val irrigationMethod: String,
    val farmingMethod: String
)

interface FarmService {
    val profile: Flow<FarmProfile?>
    suspend fun updateProfile(profile: FarmProfile)
}

data class CropConfig(
    val id: String,
    val name: String,
    val variety: String,
    val sowingDate: Long,
    val expectedHarvestDate: Long
)

interface CropService {
    fun getCrops(): Flow<List<CropConfig>>
    suspend fun addCrop(crop: CropConfig)
    suspend fun removeCrop(id: String)
    fun getCropStage(sowingDate: Long): String
}

data class DiseaseDiagnosis(
    val diseaseName: String,
    val confidence: Float,
    val severity: String,
    val symptoms: List<String>,
    val causes: List<String>,
    val immediateAction: String,
    val prevention: String,
    val treatment: String,
    val needsExpert: Boolean
)

interface DiseaseDetectionService {
    suspend fun analyzeImage(bitmap: Bitmap, cropType: String): DiseaseDiagnosis
}

data class YieldPrediction(
    val expectedYield: Double,
    val potentialYield: Double,
    val yieldGap: Double,
    val limitingFactors: List<String>
)

interface YieldPredictionService {
    suspend fun predictYield(cropId: String): YieldPrediction
}

data class Recommendation(
    val priority: String, // URGENT, HIGH, MEDIUM, LOW
    val message: String
)

interface RecommendationService {
    suspend fun getRecommendations(): List<Recommendation>
}

data class WeatherData(
    val temperature: Double,
    val humidity: Double,
    val rainfall: Double,
    val windSpeed: Double,
    val warnings: List<String>
)

interface WeatherService {
    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherData
}

data class SatelliteData(
    val vegetationHealth: Double,
    val trend: String,
    val stressLevel: String
)

interface SatelliteService {
    suspend fun getIntelligence(lat: Double, lon: Double): SatelliteData
}

data class HardwareData(
    val soilMoisture: Double,
    val temperature: Double,
    val humidity: Double,
    val lightIntensity: Double,
    val soilPh: Double,
    val waterLevel: Double,
    val battery: Double,
    val status: String
)

interface HardwareService {
    fun monitorHardware(): Flow<HardwareData>
}

data class MarketPrice(
    val crop: String,
    val market: String,
    val current: Double,
    val min: Double,
    val max: Double,
    val modal: Double,
    val updated: String
)

interface MarketService {
    suspend fun getCurrentPrices(crop: String): MarketPrice
}

data class PriceForecast(
    val current: Double,
    val forecast7d: Pair<Double, Double>,
    val trend: String,
    val confidence: String
)

interface MarketForecastService {
    suspend fun getForecast(crop: String): PriceForecast
}

data class AppNotification(
    val id: String,
    val category: String,
    val message: String,
    val isRead: Boolean,
    val timestamp: Long
)

interface NotificationService {
    fun getNotifications(): Flow<List<AppNotification>>
    suspend fun markRead(id: String)
    suspend fun markAllRead()
}

interface HistoryService {
    suspend fun logEvent(category: String, message: String)
}

