package com.example.data.repository

import android.content.Context
import com.example.core.auth.AuthService
import com.example.core.auth.KissanAuthService
import com.example.core.biometric.BiometricService
import com.example.core.biometric.KissanBiometricService
import com.example.core.i18n.LanguageService
import com.example.core.i18n.KissanLanguageService
import com.example.core.location.FarmLocation
import com.example.core.location.FarmerLocationService
import com.example.core.location.KissanFarmerLocationService
import com.example.core.location.LocationDetectionResult
import com.example.core.network.NetworkMonitor
import com.example.core.session.SessionManager
import com.example.core.storage.KissanStorageService
import com.example.core.storage.StorageService
import com.example.data.local.CopilotMessageEntity
import com.example.data.local.FarmCropEntity
import com.example.data.local.KisanDatabase
import com.example.data.local.ScanRecordEntity
import com.example.data.location.Coordinates
import com.example.data.location.LocationResult
import com.example.data.location.LocationService
import com.example.data.model.AppLanguage
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class KisanRepository(context: Context) {
  private val database = KisanDatabase.getDatabase(context)
  private val dao = database.kisanDao()
  private val copilotDao = database.copilotDao()
  
  // Clean Core Services Foundation
  val storageService: StorageService = KissanStorageService(context)
  val authService: AuthService = KissanAuthService(storageService)
  val biometricService: BiometricService = KissanBiometricService(context, storageService)
  val languageService: LanguageService = KissanLanguageService(storageService)
  val locationService = LocationService(context)
  val farmerLocationService: FarmerLocationService = KissanFarmerLocationService(context, storageService, locationService)
  
  val sessionManager = SessionManager(context)
  val networkMonitor = NetworkMonitor(context)

  val allScans: Flow<List<ScanRecordEntity>> = dao.getAllScans()
  val allCrops: Flow<List<FarmCropEntity>> = dao.getAllCrops()

  // Copilot Methods
  val copilotHistory: Flow<List<CopilotMessageEntity>> = copilotDao.getAllMessages()

  suspend fun insertCopilotMessage(message: CopilotMessageEntity) {
    copilotDao.insertMessage(message)
  }

  suspend fun clearCopilotHistory() {
    copilotDao.clearHistory()
  }

  
  // Expose auth state through AuthService
  val isLoggedIn: StateFlow<Boolean> = authService.isLoggedIn
  val currentUserEmail: StateFlow<String> = authService.currentUserEmail
  val isOnline: Flow<Boolean> = networkMonitor.isOnline

  private val _currentCoordinates = MutableStateFlow<Coordinates?>(
    Coordinates(latitude = 21.1702, longitude = 72.8311)
  )
  val currentCoordinates: StateFlow<Coordinates?> = _currentCoordinates.asStateFlow()

  val currentLanguage: StateFlow<AppLanguage> = languageService.currentLanguage

  private val _farmerProfile = MutableStateFlow(
    FarmerProfile(
      name = "Rudra Patel",
      village = "Surat",
      state = "Gujarat",
      totalLandAcres = 2.5,
      farmName = "Green Valley Farm",
      primaryCrop = "Tomato",
      mobileNumber = "+91 98765 43210",
      language = languageService.currentLanguage.value
    )
  )
  val farmerProfile: StateFlow<FarmerProfile> = _farmerProfile.asStateFlow()

  private val _weather = MutableStateFlow(
    WeatherInfo(
      locationName = "Surat",
      state = "Gujarat",
      temperatureC = 28,
      condition = "Partly Cloudy",
      conditionIcon = "wb_sunny",
      humidityPercent = 72,
      rainProbabilityPercent = 20,
      rainfallMm = 2.0,
      windSpeedKmh = 12,
      irrigationAdvice = "Irrigation recommended - Based on current weather and crop stage.",
      isIrrigationNeeded = true,
      sprayCondition = "Ideal window for spraying: 6:30 AM - 9:30 AM (Gentle breeze: 12 km/h).",
      heatwaveAlert = false,
      riskAlertMessage = null
    )
  )
  val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

  suspend fun seedComprehensiveDemoData(force: Boolean = false) = withContext(Dispatchers.IO) {
    val existingCount = dao.getCropCount()
    if (force || existingCount == 0) {
      if (force && existingCount > 0) {
        dao.clearAllCrops()
        dao.clearAllScans()
      }
      val seedCrops = listOf(
        FarmCropEntity(
          cropName = "Tomato",
          variety = "Hybrid Roma",
          areaAcres = 2.5,
          sowingDate = "15 Oct 2024",
          growthStage = "Flowering",
          soilType = "Loamy Alluvial",
          healthStatus = "Needs Attention",
          lastWateredDate = "Yesterday",
          notes = "Green Valley Farm Plot 1 - Flowering stage, drip irrigation active"
        ),
        FarmCropEntity(
          cropName = "Cotton",
          variety = "Bt Cotton RCH-2",
          areaAcres = 4.0,
          sowingDate = "20 Nov 2024",
          growthStage = "Vegetative",
          soilType = "Medium Black Soil",
          healthStatus = "Healthy",
          lastWateredDate = "2 days ago",
          notes = "Plot 2 - Excellent canopy density, square formation initiated"
        ),
        FarmCropEntity(
          cropName = "Wheat",
          variety = "Sharbati Gold",
          areaAcres = 3.0,
          sowingDate = "01 Dec 2024",
          growthStage = "Grain Filling",
          soilType = "Alluvial Clay",
          healthStatus = "Healthy",
          lastWateredDate = "3 days ago",
          notes = "Plot 3 - Pre-harvest monitoring, uniform earhead development"
        ),
        FarmCropEntity(
          cropName = "Maize",
          variety = "Sweet Corn Pioneer",
          areaAcres = 1.5,
          sowingDate = "10 Nov 2024",
          growthStage = "Tasseling",
          soilType = "Sandy Loam",
          healthStatus = "At Risk",
          lastWateredDate = "5 hours ago",
          notes = "Plot 4 - Irrigation and nutrient watch, low soil moisture"
        ),
        FarmCropEntity(
          cropName = "Soybean",
          variety = "JS 335",
          areaAcres = 3.5,
          sowingDate = "05 Dec 2024",
          growthStage = "Pod Formation",
          soilType = "Deep Black Soil",
          healthStatus = "Healthy",
          lastWateredDate = "Yesterday",
          notes = "Plot 5 - Nitrogen nodulation active, minimal weed pressure"
        )
      )
      dao.insertCrops(seedCrops)

      val seedScans = listOf(
        ScanRecordEntity(
          cropName = "Tomato",
          diseaseName = "Early Blight (अगेती झुलसा)",
          scientificName = "Alternaria solani",
          isHealthy = false,
          confidence = 0.92f,
          severity = "Moderate (15-40%)",
          symptoms = "Concentric target rings on lower leaves, yellow chlorotic halo, lower foliage defoliation.",
          organicTreatment = "Spray Trichoderma viride @ 5g/L. Remove and destroy infected lower leaves.",
          chemicalTreatment = "Azoxystrobin 23% SC @ 1.5 ml/L water or Mancozeb 75% WP @ 2.5 g/L.",
          dosage = "300ml in 200L water per acre",
          estimatedCostInr = "₹220 / acre",
          imageUri = null,
          notes = "Scouted on Plot 1 South corner during morning field inspection"
        ),
        ScanRecordEntity(
          cropName = "Cotton",
          diseaseName = "Whitefly Infestation (सफेद मक्खी)",
          scientificName = "Bemisia tabaci",
          isHealthy = false,
          confidence = 0.88f,
          severity = "Mild (5-15%)",
          symptoms = "Nymphs and adults on leaf underside, mild honeydew secretion with black sooty mold.",
          organicTreatment = "Spray cold-pressed Neem Oil 10,000 ppm @ 3 ml/L. Install 15 yellow sticky traps per acre.",
          chemicalTreatment = "Diafenthiuron 50% WP @ 1.2 g/L or Pyriproxyfen 10% EC @ 2 ml/L.",
          dosage = "250g in 200L water per acre",
          estimatedCostInr = "₹340 / acre",
          imageUri = null,
          notes = "Early nymph cluster detected, immediate sticky trap installation advised"
        ),
        ScanRecordEntity(
          cropName = "Maize",
          diseaseName = "Nitrogen Chlorosis (नाइट्रोजन की कमी)",
          scientificName = "Nutrient Deficiency (N)",
          isHealthy = false,
          confidence = 0.86f,
          severity = "Moderate (15-40%)",
          symptoms = "V-shaped yellowing starting from leaf tip down the midrib on older lower leaves.",
          organicTreatment = "Apply well-decomposed Vermicompost (500 kg/acre) enriched with Jeevamrut @ 200L/acre.",
          chemicalTreatment = "Foliar spray of 2% Urea solution (20g/L water) or Calcium Nitrate @ 5g/L.",
          dosage = "4 kg Urea in 200L water per acre (morning application)",
          estimatedCostInr = "₹180 / acre",
          imageUri = null,
          notes = "Correlates with IoT sensor indicating sandy loam leaching"
        ),
        ScanRecordEntity(
          cropName = "Wheat",
          diseaseName = "Yellow Leaf Rust (पीला रतुआ)",
          scientificName = "Puccinia striiformis",
          isHealthy = false,
          confidence = 0.85f,
          severity = "Mild (5-15%)",
          symptoms = "Linear stripes of yellow-orange powdery pustules along leaf veins.",
          organicTreatment = "Apply Cow urine distillate (5%) + Fermented butter milk spray.",
          chemicalTreatment = "Propiconazole 25% EC (Tilt) @ 1 ml/L water.",
          dosage = "200ml in 200L water per acre",
          estimatedCostInr = "₹280 / acre",
          imageUri = null,
          notes = "Border row monitoring, cold humid conditions favored onset"
        ),
        ScanRecordEntity(
          cropName = "Tomato",
          diseaseName = "Healthy Leaf Structure",
          scientificName = "Solanum lycopersicum",
          isHealthy = true,
          confidence = 0.98f,
          severity = "Healthy (0%)",
          symptoms = "Vibrant dark green coloration, turgid cell structure, no lesion or pest presence.",
          organicTreatment = "Continue preventive Panchagavya spray (3%) every 14 days.",
          chemicalTreatment = "No chemical intervention needed. Maintain balanced drip fertigation.",
          dosage = "N/A",
          estimatedCostInr = "₹0",
          imageUri = null,
          notes = "Control sample benchmark for vegetative growth monitoring"
        )
      )
      dao.insertScans(seedScans)
    }
  }

  suspend fun initializeDefaultsIfEmpty() = withContext(Dispatchers.IO) {
    seedComprehensiveDemoData(force = false)
  }

  suspend fun saveScan(scan: ScanRecordEntity): Long = withContext(Dispatchers.IO) {
    dao.insertScan(scan)
  }

  suspend fun saveScans(scans: List<ScanRecordEntity>, chunkSize: Int = 100): List<Long> = withContext(Dispatchers.IO) {
    dao.insertScansChunked(scans, chunkSize)
  }

  suspend fun deleteScan(id: Long): Unit = withContext(Dispatchers.IO) {
    dao.deleteScanById(id)
  }

  suspend fun deleteScans(ids: List<Long>, chunkSize: Int = 100): Unit = withContext(Dispatchers.IO) {
    dao.deleteScansChunked(ids, chunkSize)
  }

  suspend fun saveCrop(crop: FarmCropEntity): Long = withContext(Dispatchers.IO) {
    dao.insertCrop(crop)
  }

  suspend fun saveCrops(crops: List<FarmCropEntity>, chunkSize: Int = 100): List<Long> = withContext(Dispatchers.IO) {
    dao.insertCropsChunked(crops, chunkSize)
  }

  suspend fun deleteCrop(id: Long): Unit = withContext(Dispatchers.IO) {
    dao.deleteCropById(id)
  }

  suspend fun deleteCrops(ids: List<Long>, chunkSize: Int = 100): Unit = withContext(Dispatchers.IO) {
    dao.deleteCropsChunked(ids, chunkSize)
  }

  suspend fun recordScanFeedback(id: Long, rating: Int, reason: String = ""): Unit = withContext(Dispatchers.IO) {
    dao.updateScanFeedback(id, rating, reason)
  }

  suspend fun loginUser(email: String, token: String = "kisan_auth_${System.currentTimeMillis()}") {
    authService.login(email, token)
    sessionManager.saveSession(email, token)
  }

  suspend fun logoutUser() {
    authService.logout()
    sessionManager.clearSession()
  }

  fun setLanguage(language: AppLanguage) {
    languageService.setLanguage(language)
    _farmerProfile.value = _farmerProfile.value.copy(language = language)
  }

  fun updateProfile(profile: FarmerProfile) {
    _farmerProfile.value = profile
    setLanguage(profile.language)
  }

  suspend fun setManualLocation(
    cityName: String,
    stateName: String,
    latitude: Double,
    longitude: Double
  ) = withContext(Dispatchers.IO) {
    val coords = Coordinates(latitude, longitude)
    _currentCoordinates.value = coords
    val loc = FarmLocation(
      village = cityName,
      district = cityName,
      state = stateName,
      latitude = latitude,
      longitude = longitude,
      isManual = true
    )
    farmerLocationService.saveLocation(loc)

    val crop = _farmerProfile.value.primaryCrop
    val liveWeather = com.example.data.remote.WeatherApiService.fetchRealWeather(
      latitude = latitude,
      longitude = longitude,
      cityName = cityName,
      stateName = stateName,
      cropType = crop
    )
    _weather.value = liveWeather
    _farmerProfile.value = _farmerProfile.value.copy(
      village = cityName,
      state = stateName
    )
  }

  suspend fun fetchCurrentLocation(): LocationResult {
    val result = locationService.getCurrentCoordinates()
    if (result is LocationResult.Success) {
      _currentCoordinates.value = result.coordinates
      val lat = result.coordinates.latitude
      val lon = result.coordinates.longitude
      val locality = result.locality ?: _weather.value.locationName
      val state = result.state ?: _weather.value.state
      val crop = _farmerProfile.value.primaryCrop
      
      val loc = FarmLocation(
        village = locality,
        district = locality,
        state = state,
        latitude = lat,
        longitude = lon,
        isManual = false
      )
      farmerLocationService.saveLocation(loc)

      val liveWeather = com.example.data.remote.WeatherApiService.fetchRealWeather(
        latitude = lat,
        longitude = lon,
        cityName = locality,
        stateName = state,
        cropType = crop
      )
      _weather.value = liveWeather
      _farmerProfile.value = _farmerProfile.value.copy(
        village = locality,
        state = state
      )
    }
    return result
  }
}
