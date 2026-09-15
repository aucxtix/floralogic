package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CropDiseaseDetector
import com.example.core.location.FarmLocation
import com.example.ai.CombinedIntelligence
import com.example.ai.CropHealthAnalysis
import com.example.ai.CropHealthEngine
import com.example.ai.DailyFarmBriefingData
import com.example.ai.DailyFarmBriefingEngine
import com.example.ai.DigitalFarmState
import com.example.ai.ActionableDiseaseRecommendation
import com.example.ai.DiseaseRecommendationEngine
import com.example.ai.FarmContextEngine
import com.example.ai.FarmHealthEngine
import com.example.ai.FarmRecommendationEngine
import com.example.ai.FarmRiskEngine
import com.example.ai.FarmRiskProfileResult
import com.example.ai.FarmSimulationEngine
import com.example.ai.FarmerIntelligenceEngine
import com.example.ai.GeminiCopilotService
import com.example.data.local.CopilotMessageEntity

import com.example.ai.GroundedRecommendation
import com.example.ai.IoTAnalyticsEngine
import com.example.ai.IoTDiagnosticsResult
import com.example.ai.IrrigationAdviceResult
import com.example.ai.IrrigationDecisionEngine
import com.example.ai.MarketForecastEngine
import com.example.ai.MarketForecastResult
import com.example.ai.SatelliteAnalysisEngine
import com.example.ai.SatelliteAnalysisResult
import com.example.ai.SellingDecisionResult
import com.example.ai.SimulationComparisonResult
import com.example.ai.SimulationScenario
import com.example.ai.YieldPredictionEngine
import com.example.ai.YieldPredictionResult
import com.example.ai.GeminiGroundingService
import com.example.ai.GroundedResponse
import com.example.ai.MapsGroundedResponse
import com.example.ai.AudioTranscriptionService
import com.example.core.auth.DummyFarmerAccount
import com.example.core.auth.DummyFarmerAccounts
import com.example.core.auth.FirebaseManager
import java.io.File
import com.example.data.local.FarmCropEntity
import com.example.data.local.ScanRecordEntity
import com.example.data.location.Coordinates
import com.example.data.location.LocationResult
import com.example.data.location.LocationService
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.CropDisease
import com.example.data.model.FarmerProfile
import com.example.data.model.LocalizedStrings
import com.example.data.model.WeatherInfo
import com.example.data.repository.KisanRepository
import com.example.ui.components.DrawerDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RealtimeFarmAlert(
  val id: String,
  val title: String,
  val message: String,
  val category: String, // "IRRIGATION", "DISEASE", "MARKET", "IOT"
  val severity: String, // "CRITICAL", "HIGH", "INFO"
  val timestampMs: Long = System.currentTimeMillis(),
  val actionText: String = "View Action",
  val destination: DrawerDestination = DrawerDestination.IRRIGATION_ADVISOR
)

sealed interface ScanUiState {
  object Idle : ScanUiState
  data class Analyzing(val cropHint: String) : ScanUiState
  data class Success(
    val disease: CropDisease,
    val recommendation: ActionableDiseaseRecommendation? = null,
    val imageUri: String? = null,
    val capturedBitmap: Bitmap? = null,
    val isSaved: Boolean = false,
    val savedId: Long? = null,
    val feedbackRating: Int = 0 // 0 = none, 1 = thumbs up, -1 = thumbs down
  ) : ScanUiState
  data class Inconclusive(
    val cropHint: String,
    val confidence: Float,
    val reasons: List<String>,
    val suggestions: List<String>,
    val imageUri: String? = null,
    val capturedBitmap: Bitmap? = null
  ) : ScanUiState
  data class Error(val message: String) : ScanUiState
}
class KisanViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = KisanRepository(application)
  private val database = com.example.data.local.KisanDatabase.getDatabase(application)
  val marketPrices = MutableStateFlow<List<com.example.data.api.MarketPriceDto>>(emptyList())
  val marketForecast = MutableStateFlow<com.example.data.api.MarketForecastDto?>(null)
  private val ioTRepository = com.example.data.repository.IoTRepository(database.kisanDao())
  private val marketRepository = com.example.data.repository.MarketRepository(database.kisanDao())

  val deviceHealth = MutableStateFlow<com.example.data.api.DeviceHealthDto?>(null)
  val liveSensorReading = MutableStateFlow<com.example.data.local.SensorReadingEntity?>(null)

  private val _activeRealtimeAlert = MutableStateFlow<RealtimeFarmAlert?>(null)
  val activeRealtimeAlert: StateFlow<RealtimeFarmAlert?> = _activeRealtimeAlert.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeDefaultsIfEmpty()
    }
    viewModelScope.launch {
      deviceHealth.value = ioTRepository.getDeviceHealth("FARM_001").firstOrNull()
      marketPrices.value = marketRepository.getLivePrices("Cotton")
      marketForecast.value = marketRepository.getPriceForecast("Cotton")
    }
    viewModelScope.launch {
      ioTRepository.observeLiveSensorData("ESP32_001").collect { reading ->
        liveSensorReading.value = reading
      }
    }
    viewModelScope.launch {
      delay(1500)
      if (isLoggedIn.value) {
        triggerLoginRealtimeAlerts()
      }
    }
  }

  val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
  val farmerProfile: StateFlow<FarmerProfile> = repository.farmerProfile
  val weather: StateFlow<WeatherInfo> = repository.weather
  val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
  val currentUserEmail: StateFlow<String> = repository.currentUserEmail
  val isOnline: StateFlow<Boolean> = repository.isOnline
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = true
    )

  // Optimistic tracking flows for instantaneous UI updates and graceful rollbacks
  private val _optimisticDeletedScanIds = MutableStateFlow<Set<Long>>(emptySet())
  val optimisticDeletedScanIds: StateFlow<Set<Long>> = _optimisticDeletedScanIds.asStateFlow()

  private val _optimisticFeedbackUpdates = MutableStateFlow<Map<Long, Int>>(emptyMap())
  val optimisticFeedbackUpdates: StateFlow<Map<Long, Int>> = _optimisticFeedbackUpdates.asStateFlow()

  private val _optimisticDeletedCropIds = MutableStateFlow<Set<Long>>(emptySet())
  val optimisticDeletedCropIds: StateFlow<Set<Long>> = _optimisticDeletedCropIds.asStateFlow()

  private val _optimisticAddedCrops = MutableStateFlow<List<FarmCropEntity>>(emptyList())
  val optimisticAddedCrops: StateFlow<List<FarmCropEntity>> = _optimisticAddedCrops.asStateFlow()

  val scans: StateFlow<List<ScanRecordEntity>> = combine(
    repository.allScans,
    _optimisticDeletedScanIds,
    _optimisticFeedbackUpdates
  ) { dbScans, deletedIds, feedbackUpdates ->
    dbScans
      .filterNot { it.id in deletedIds }
      .map { scan ->
        if (feedbackUpdates.containsKey(scan.id)) {
          scan.copy(feedbackRating = feedbackUpdates[scan.id] ?: scan.feedbackRating)
        } else {
          scan
        }
      }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = emptyList()
  )

  val crops: StateFlow<List<FarmCropEntity>> = combine(
    repository.allCrops,
    _optimisticDeletedCropIds,
    _optimisticAddedCrops
  ) { dbCrops, deletedIds, addedCrops ->
    val filteredDb = dbCrops.filterNot { it.id in deletedIds }
    addedCrops + filteredDb
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = emptyList()
  )

  // Dummy Farmer Accounts & Selection
  val dummyAccounts: List<DummyFarmerAccount> = DummyFarmerAccounts.accounts
  private val _selectedDummyAccount = MutableStateFlow<DummyFarmerAccount?>(DummyFarmerAccounts.defaultAccount)
  val selectedDummyAccount: StateFlow<DummyFarmerAccount?> = _selectedDummyAccount.asStateFlow()

  fun selectDummyAccount(account: DummyFarmerAccount) {
    _selectedDummyAccount.value = account
  }

  fun loginWithDummyAccount(account: DummyFarmerAccount, onComplete: () -> Unit = {}) {
    viewModelScope.launch {
      _selectedDummyAccount.value = account
      repository.updateProfile(account.toFarmerProfile())
      repository.loginUser(account.email, "kisan_auth_${account.id}")
      
      val (lat, lng) = when (account.state) {
        "Gujarat" -> 21.1702 to 72.8311
        "Madhya Pradesh" -> 22.7196 to 75.8577
        "Maharashtra" -> 19.9975 to 73.7898
        "Punjab" -> 30.9010 to 75.8573
        "Uttar Pradesh" -> 25.3176 to 82.9739
        "Andhra Pradesh" -> 16.3067 to 80.4365
        else -> 21.1702 to 72.8311
      }
      repository.setManualLocation(account.village, account.state, lat, lng)
      
      // Persist profile to Firestore
      FirebaseManager.getInstance(getApplication()).persistProfileToFirestore(account.id, account.toFarmerProfile())
      
      repository.seedComprehensiveDemoData(force = false)
      triggerLoginRealtimeAlerts()
      onComplete()
    }
  }

  fun loginWithGoogleAuth(activityContext: android.content.Context, onComplete: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
      val fbManager = FirebaseManager.getInstance(getApplication())
      val result = fbManager.signInWithGoogle(activityContext)
      result.onSuccess { profile ->
        repository.updateProfile(profile)
        repository.loginUser("google.farmer@kisan.ai", "google_oauth_token_${System.currentTimeMillis()}")
        repository.seedComprehensiveDemoData(force = false)
        triggerLoginRealtimeAlerts()
        onComplete(true, null)
      }.onFailure { err ->
        onComplete(false, err.message)
      }
    }
  }

  fun registerAndLoginFarmer(
    name: String,
    email: String,
    password: String,
    village: String,
    state: String,
    acres: Double,
    crop: String,
    mobile: String,
    onComplete: (Boolean, String?) -> Unit
  ) {
    viewModelScope.launch {
      val profile = FarmerProfile(
        name = name,
        village = village,
        state = state,
        totalLandAcres = acres,
        farmName = "$name Farm",
        primaryCrop = crop,
        mobileNumber = mobile.ifBlank { "+91 98765 43210" },
        language = repository.currentLanguage.value
      )
      val fbManager = FirebaseManager.getInstance(getApplication())
      val res = fbManager.createAccountWithEmail(email, password, profile)
      res.onSuccess { uid ->
        repository.updateProfile(profile)
        repository.loginUser(email, uid)
        repository.seedComprehensiveDemoData(force = false)
        triggerLoginRealtimeAlerts()
        onComplete(true, null)
      }.onFailure { err ->
        onComplete(false, err.message)
      }
    }
  }

  // Maps Grounding for nearby Mandis & Agri Centers (gemini-3.5-flash with googleMaps tool)
  private val _mapsGroundingState = MutableStateFlow<MapsGroundedResponse?>(null)
  val mapsGroundingState: StateFlow<MapsGroundedResponse?> = _mapsGroundingState.asStateFlow()
  private val _isMapsGroundingLoading = MutableStateFlow(false)
  val isMapsGroundingLoading: StateFlow<Boolean> = _isMapsGroundingLoading.asStateFlow()

  fun fetchNearbyAgriPlacesWithMaps(category: String = "APMC Mandi Yard and Fertilizer Center") {
    viewModelScope.launch {
      _isMapsGroundingLoading.value = true
      val loc = farmLocation.value.let { "${it.village}, ${it.state}" }
      val res = GeminiGroundingService.findAgriPlacesWithMapsGrounding(loc, category)
      _mapsGroundingState.value = res
      _isMapsGroundingLoading.value = false
    }
  }

  // Search Grounding for live Mandi trends & MSP updates (gemini-3.5-flash with googleSearch tool)
  private val _searchGroundingState = MutableStateFlow<GroundedResponse?>(null)
  val searchGroundingState: StateFlow<GroundedResponse?> = _searchGroundingState.asStateFlow()
  private val _isSearchGroundingLoading = MutableStateFlow(false)
  val isSearchGroundingLoading: StateFlow<Boolean> = _isSearchGroundingLoading.asStateFlow()

  fun fetchLiveMandiTrendsWithSearch(topic: String = "Latest APMC Mandi commodity prices and MSP rates") {
    viewModelScope.launch {
      _isSearchGroundingLoading.value = true
      val loc = farmLocation.value.let { "${it.village}, ${it.state}" }
      val prompt = "$topic for crops in $loc, India."
      val res = GeminiGroundingService.askWithSearchGrounding(prompt)
      _searchGroundingState.value = res
      _isSearchGroundingLoading.value = false
    }
  }

  // Audio transcription using gemini-3.5-transcribe
  fun transcribeVoiceAudio(audioFile: File, onResult: (String) -> Unit) {
    viewModelScope.launch {
      val res = AudioTranscriptionService.transcribeAudio(audioFile)
      val text = res.getOrDefault("Meri fasal me kya samasya hai?")
      onResult(text)
    }
  }

  fun loginUser(email: String, token: String = "kisan_auth_${System.currentTimeMillis()}") {
    viewModelScope.launch {
      repository.loginUser(email, token)
      repository.seedComprehensiveDemoData(force = false)
      triggerLoginRealtimeAlerts()
    }
  }

  fun triggerLoginRealtimeAlerts() {
    viewModelScope.launch {
      delay(700)
      sendDummySystemNotification("WATER_STRESS")
    }
  }

  fun sendDummySystemNotification(
    type: String = "WATER_STRESS",
    customTitle: String? = null,
    customMessage: String? = null
  ) {
    viewModelScope.launch {
      val notificationService = com.example.core.notification.KissanSystemNotificationService(getApplication())
      val title: String
      val message: String
      val channel: String
      val destination: String

      when (type) {
        "MARKET" -> {
          title = customTitle ?: "📈 MANDI PRICE SURGE (+6.4%)"
          message = customMessage ?: "Tomato prices reached ₹2,450/qtl at Surat APMC. Favorable selling window detected."
          channel = com.example.core.notification.KissanSystemNotificationService.CHANNEL_MARKET_UPDATES
          destination = DrawerDestination.MARKET_INTELLIGENCE.name
        }
        "PEST" -> {
          title = customTitle ?: "⚠️ PEST EARLY WARNING (APHIDS)"
          message = customMessage ?: "Atmospheric humidity is 78%. Favorable conditions for aphid multiplication detected on lower foliage."
          channel = com.example.core.notification.KissanSystemNotificationService.CHANNEL_FARM_ADVISORY
          destination = DrawerDestination.SCAN_CROP.name
        }
        else -> {
          title = customTitle ?: "🚨 CRITICAL: WATER STRESS (TOMATO PLOT 1)"
          message = customMessage ?: "Soil moisture dropped to 22% (Target: 40%). Drip irrigation recommended for 45 min before 11:30 AM."
          channel = com.example.core.notification.KissanSystemNotificationService.CHANNEL_CRITICAL_ALERTS
          destination = DrawerDestination.IRRIGATION_ADVISOR.name
        }
      }

      val posted = notificationService.postSystemNotification(
        title = title,
        message = message,
        channelId = channel,
        destinationRoute = destination
      )

      if (posted) {
        _snackbarMessage.value = "🔔 Notification posted to phone notification panel! Pull down top status bar."
      } else {
        _snackbarMessage.value = "🔔 Notification dispatched. (Check phone notification settings if not shown)"
      }
    }
  }

  fun triggerSimulatedAlert(type: String = "MARKET") {
    sendDummySystemNotification(type)
  }

  fun dismissRealtimeAlert() {
    _activeRealtimeAlert.value = null
  }

  fun seedComprehensiveDemoData(force: Boolean = true) {
    viewModelScope.launch {
      repository.seedComprehensiveDemoData(force = force)
      _snackbarMessage.value = "Demo data loaded with 5 crops and diagnostic scans."
      triggerLoginRealtimeAlerts()
    }
  }

  fun logoutUser() {
    viewModelScope.launch {
      repository.logoutUser()
      _snackbarMessage.value = "You have been logged out safely."
    }
  }

  val isBiometricHardwareSupported: Boolean get() = repository.biometricService.isHardwareSupported()
  val isBiometricLoginEnabled: Boolean get() = repository.biometricService.isBiometricLoginEnabled()
  fun setBiometricLoginEnabled(enabled: Boolean) {
    repository.biometricService.setBiometricLoginEnabled(enabled)
  }

  val farmLocation: StateFlow<FarmLocation> = repository.farmerLocationService.currentLocation

  val currentCoordinates: StateFlow<Coordinates?> = repository.currentCoordinates
  val locationService: LocationService = repository.locationService

  private val _isFetchingLocation = MutableStateFlow(false)
  val isFetchingLocation: StateFlow<Boolean> = _isFetchingLocation.asStateFlow()

  private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
  val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

  private val _selectedCropContext = MutableStateFlow("Wheat")
  val selectedCropContext: StateFlow<String> = _selectedCropContext.asStateFlow()

  fun setSelectedCropContext(crop: String) {
    _selectedCropContext.value = crop
  }

  private val intelligenceEngine = FarmerIntelligenceEngine()
  private val copilotService = GeminiCopilotService()
  
  private val _combinedIntelligence = MutableStateFlow<CombinedIntelligence?>(null)
  val combinedIntelligence: StateFlow<CombinedIntelligence?> = _combinedIntelligence.asStateFlow()
  
  private val _isIntelligenceLoading = MutableStateFlow(false)
  val isIntelligenceLoading: StateFlow<Boolean> = _isIntelligenceLoading.asStateFlow()

  private data class FarmInputs(
      val profile: FarmerProfile,
      val location: FarmLocation,
      val weather: WeatherInfo,
      val crops: List<FarmCropEntity>,
      val scans: List<ScanRecordEntity>
  )

  val digitalFarmState: StateFlow<DigitalFarmState> = combine(
      combine(farmerProfile, farmLocation, weather, crops, scans) { profile, loc, wx, c, s ->
          FarmInputs(profile, loc, wx, c, s)
      },
      liveSensorReading
  ) { inputs, sensor ->
      FarmContextEngine.buildDigitalFarmState(
          profile = inputs.profile,
          location = inputs.location,
          weather = inputs.weather,
          crops = inputs.crops,
          scans = inputs.scans,
          latestSensorReading = sensor
      )
  }.stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      FarmContextEngine.buildDigitalFarmState(
          profile = farmerProfile.value,
          location = farmLocation.value,
          weather = weather.value,
          crops = crops.value,
          scans = scans.value,
          latestSensorReading = liveSensorReading.value
      )
  )

  val dailyBriefing: StateFlow<DailyFarmBriefingData> = digitalFarmState.map {
    DailyFarmBriefingEngine.generateBriefing(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, DailyFarmBriefingEngine.generateBriefing(digitalFarmState.value))

  val farmRecommendations: StateFlow<List<GroundedRecommendation>> = digitalFarmState.map {
    FarmRecommendationEngine.generateRecommendations(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, FarmRecommendationEngine.generateRecommendations(digitalFarmState.value))

  val cropHealthAnalysis: StateFlow<CropHealthAnalysis> = digitalFarmState.map {
    CropHealthEngine.evaluateCropHealth(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, CropHealthEngine.evaluateCropHealth(digitalFarmState.value))

  val yieldPrediction: StateFlow<YieldPredictionResult> = digitalFarmState.map {
    YieldPredictionEngine.predictYield(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, YieldPredictionEngine.predictYield(digitalFarmState.value))

  val irrigationAdvice: StateFlow<IrrigationAdviceResult> = digitalFarmState.map {
    IrrigationDecisionEngine.evaluateIrrigation(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, IrrigationDecisionEngine.evaluateIrrigation(digitalFarmState.value))

  val farmRiskProfile: StateFlow<FarmRiskProfileResult> = digitalFarmState.map {
    FarmRiskEngine.evaluateFarmRisks(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, FarmRiskEngine.evaluateFarmRisks(digitalFarmState.value))

  val marketForecastResult: StateFlow<MarketForecastResult> = digitalFarmState.map {
    MarketForecastEngine.forecastPrices(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, MarketForecastEngine.forecastPrices(digitalFarmState.value))

  val sellingDecision: StateFlow<SellingDecisionResult> = digitalFarmState.map {
    MarketForecastEngine.evaluateSellingDecision(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, MarketForecastEngine.evaluateSellingDecision(digitalFarmState.value))

  val satelliteAnalysis: StateFlow<SatelliteAnalysisResult> = digitalFarmState.map {
    SatelliteAnalysisEngine.analyzeSatelliteData(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, SatelliteAnalysisEngine.analyzeSatelliteData(digitalFarmState.value))

  val iotDiagnostics: StateFlow<IoTDiagnosticsResult> = digitalFarmState.map {
    IoTAnalyticsEngine.diagnoseSensors(it)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, IoTAnalyticsEngine.diagnoseSensors(digitalFarmState.value))

  private val _recommendationFeedback = MutableStateFlow<Map<String, String>>(emptyMap())
  val recommendationFeedback: StateFlow<Map<String, String>> = _recommendationFeedback.asStateFlow()

  fun recordRecommendationFeedback(recommendationId: String, status: String) {
    _recommendationFeedback.value = _recommendationFeedback.value + (recommendationId to status)
    _snackbarMessage.value = "Action logged: $status"
  }

  fun runSimulation(scenario: SimulationScenario): SimulationComparisonResult {
    return FarmSimulationEngine.runSimulation(digitalFarmState.value, scenario)
  }

  fun refreshIntelligence() {
    viewModelScope.launch {
      _isIntelligenceLoading.value = true
      try {
        val result = intelligenceEngine.generateComprehensiveIntelligence(digitalFarmState.value)
        if (result != null) {
            _combinedIntelligence.value = result
        }
      } catch (e: Exception) {
          e.printStackTrace()
      } finally {
          _isIntelligenceLoading.value = false
      }
    }
  }

  fun askCopilot(question: String, onResponse: (String) -> Unit) {
      viewModelScope.launch {
          val userMsgId = "usr_${System.currentTimeMillis()}"
          val userEntity = CopilotMessageEntity(id = userMsgId, sender = "farmer", text = question)
          repository.insertCopilotMessage(userEntity)

          val answer = copilotService.askQuestion(question, digitalFarmState.value)
          
          val aiMsgId = "ai_${System.currentTimeMillis()}"
          val aiEntity = CopilotMessageEntity(id = aiMsgId, sender = "kissan_ai", text = answer)
          repository.insertCopilotMessage(aiEntity)
          
          onResponse(answer)
      }
  }

  fun clearCopilotHistory() {
      viewModelScope.launch {
          repository.clearCopilotHistory()
      }
  }



  val copilotHistory: StateFlow<List<CopilotMessageEntity>> = repository.copilotHistory
    .stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeDefaultsIfEmpty()
    }
  }

  fun getStrings(language: AppLanguage): AppStrings {
    return LocalizedStrings.get(language)
  }

  fun switchLanguage(language: AppLanguage) {
    repository.setLanguage(language)
  }

  fun updateProfile(profile: FarmerProfile) {
    repository.updateProfile(profile)
    _snackbarMessage.value = "Farmer profile updated"
  }

  fun scanLeaf(
    bitmap: Bitmap?,
    cropHint: String,
    specimenId: String? = null,
    imageUri: String? = null
  ) {
    viewModelScope.launch {
      _scanState.value = ScanUiState.Analyzing(cropHint)
      try {
        when (val result = CropDiseaseDetector.analyzeCropLeaf(getApplication(), bitmap, cropHint, specimenId)) {
          is com.example.ai.DetectionResult.Success -> {
            val report = DiseaseRecommendationEngine.generateRecommendation(
              detectedDisease = result.disease,
              farmState = digitalFarmState.value
            )
            _scanState.value = ScanUiState.Success(
              disease = result.disease,
              recommendation = report,
              imageUri = imageUri,
              capturedBitmap = bitmap,
              isSaved = false,
              savedId = null,
              feedbackRating = 0
            )
          }
          is com.example.ai.DetectionResult.Inconclusive -> {
            _scanState.value = ScanUiState.Inconclusive(
              cropHint = result.cropHint,
              confidence = result.confidence,
              reasons = result.reasons,
              suggestions = result.suggestions,
              imageUri = imageUri,
              capturedBitmap = bitmap
            )
          }
        }
      } catch (e: Exception) {
        _scanState.value = ScanUiState.Error(e.localizedMessage ?: "Analysis failed")
      }
    }
  }

  fun saveCurrentScanResult() {
    val currentState = _scanState.value
    if (currentState is ScanUiState.Success && !currentState.isSaved) {
      // 1. Optimistic UI update immediately
      _scanState.value = currentState.copy(isSaved = true)
      viewModelScope.launch {
        try {
          val disease = currentState.disease
          val entity = ScanRecordEntity(
            cropName = disease.cropName,
            diseaseName = disease.diseaseName,
            scientificName = disease.scientificName,
            isHealthy = disease.isHealthy,
            confidence = disease.confidence,
            severity = disease.severity.label,
            symptoms = disease.symptoms.joinToString("\n• ", prefix = "• "),
            organicTreatment = disease.organicTreatment,
            chemicalTreatment = disease.chemicalTreatment,
            dosage = disease.dosage,
            estimatedCostInr = disease.estimatedCostInr,
            imageUri = currentState.imageUri,
            notes = "Diagnosed via ${disease.modelSource}"
          )
          val newId = repository.saveScan(entity)
          // Reconcile with persisted ID
          _scanState.value = currentState.copy(isSaved = true, savedId = newId)
          val strings = getStrings(currentLanguage.value)
          _snackbarMessage.value = strings.savedSuccess
        } catch (e: Exception) {
          // Graceful Rollback
          _scanState.value = currentState.copy(isSaved = false, savedId = null)
          _snackbarMessage.value = "⚠️ Could not save scan to history. Action rolled back."
        }
      }
    }
  }

  fun submitScanFeedback(rating: Int, reason: String = "") {
    val currentState = _scanState.value
    if (currentState is ScanUiState.Success) {
      val prevRating = currentState.feedbackRating
      // 1. Optimistic UI update immediately
      _scanState.value = currentState.copy(feedbackRating = rating)
      viewModelScope.launch {
        try {
          val id = currentState.savedId
          if (id != null) {
            repository.recordScanFeedback(id, rating, reason)
          }
          val msg = if (rating > 0) {
            "Feedback recorded: Diagnosis confirmed accurate. Thank you!"
          } else {
            "Feedback recorded: Thank you for helping calibrate our on-device model."
          }
          _snackbarMessage.value = msg
        } catch (e: Exception) {
          // Graceful Rollback
          _scanState.value = currentState.copy(feedbackRating = prevRating)
          _snackbarMessage.value = "⚠️ Failed to save feedback. Restored previous rating."
        }
      }
    }
  }

  fun submitHistoryFeedback(scanId: Long, rating: Int, reason: String = "") {
    val currentUpdates = _optimisticFeedbackUpdates.value
    val previousRating = currentUpdates[scanId]
    // 1. Optimistic UI update immediately
    _optimisticFeedbackUpdates.value = currentUpdates + (scanId to rating)
    viewModelScope.launch {
      try {
        repository.recordScanFeedback(scanId, rating, reason)
        _snackbarMessage.value = if (rating > 0) "Feedback recorded: Accurate diagnosis" else "Feedback recorded: Flagged for review"
      } catch (e: Exception) {
        // Graceful Rollback
        val reverted = _optimisticFeedbackUpdates.value.toMutableMap()
        if (previousRating != null) {
          reverted[scanId] = previousRating
        } else {
          reverted.remove(scanId)
        }
        _optimisticFeedbackUpdates.value = reverted
        _snackbarMessage.value = "⚠️ Failed to record feedback. Rolled back."
      }
    }
  }

  fun setManualLocation(cityName: String, stateName: String, latitude: Double, longitude: Double) {
    viewModelScope.launch {
      _isFetchingLocation.value = true
      try {
        repository.setManualLocation(cityName, stateName, latitude, longitude)
        _snackbarMessage.value = "Weather & Irrigation updated for $cityName, $stateName"
      } finally {
        _isFetchingLocation.value = false
      }
    }
  }

  fun resetScanState() {
    _scanState.value = ScanUiState.Idle
  }

  fun deleteScan(id: Long) {
    // 1. Optimistic removal: UI reflects deletion instantly
    _optimisticDeletedScanIds.value = _optimisticDeletedScanIds.value + id
    viewModelScope.launch {
      try {
        repository.deleteScan(id)
        // Reconcile after persistence completes
        _optimisticDeletedScanIds.value = _optimisticDeletedScanIds.value - id
      } catch (e: Exception) {
        // Graceful Rollback: restore scan to screen
        _optimisticDeletedScanIds.value = _optimisticDeletedScanIds.value - id
        _snackbarMessage.value = "⚠️ Failed to delete scan. Action rolled back."
      }
    }
  }

  fun addNewCrop(
    cropName: String,
    variety: String,
    areaAcres: Double,
    sowingDate: String,
    growthStage: String,
    soilType: String,
    notes: String
  ) {
    val optimisticCrop = FarmCropEntity(
      id = -System.currentTimeMillis(), // Temporary optimistic ID
      cropName = cropName,
      variety = variety.ifBlank { "Standard Hybrid" },
      areaAcres = areaAcres,
      sowingDate = sowingDate,
      growthStage = growthStage,
      soilType = soilType.ifBlank { "Black / Loam" },
      healthStatus = "Good (Active Monitoring)",
      lastWateredDate = "Today",
      notes = notes
    )
    // 1. Optimistic addition: user sees new crop immediately
    _optimisticAddedCrops.value = _optimisticAddedCrops.value + optimisticCrop

    viewModelScope.launch {
      try {
        val cropToSave = optimisticCrop.copy(id = 0)
        repository.saveCrop(cropToSave)
        // Reconcile: remove optimistic item as Room emits real entity
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "New crop added to farm"
      } catch (e: Exception) {
        // Graceful Rollback: remove optimistic item and notify user
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "⚠️ Failed to save crop: ${e.message}. Rolled back."
      }
    }
  }

  fun addFarmCrop(crop: FarmCropEntity) {
    val optimisticCrop = if (crop.id == 0L) crop.copy(id = -System.currentTimeMillis()) else crop
    // 1. Optimistic addition immediately
    _optimisticAddedCrops.value = _optimisticAddedCrops.value + optimisticCrop

    viewModelScope.launch {
      try {
        val toSave = if (optimisticCrop.id < 0) optimisticCrop.copy(id = 0) else optimisticCrop
        repository.saveCrop(toSave)
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "New crop added to farm"
      } catch (e: Exception) {
        _optimisticAddedCrops.value = _optimisticAddedCrops.value.filterNot { it.id == optimisticCrop.id }
        _snackbarMessage.value = "⚠️ Failed to save crop. Rolled back."
      }
    }
  }

  fun deleteCrop(id: Long) {
    // 1. Optimistic removal: UI reflects crop removal instantly
    _optimisticDeletedCropIds.value = _optimisticDeletedCropIds.value + id
    viewModelScope.launch {
      try {
        repository.deleteCrop(id)
        // Reconcile
        _optimisticDeletedCropIds.value = _optimisticDeletedCropIds.value - id
      } catch (e: Exception) {
        // Graceful Rollback: restore crop to farm list
        _optimisticDeletedCropIds.value = _optimisticDeletedCropIds.value - id
        _snackbarMessage.value = "⚠️ Failed to delete crop. Action rolled back."
      }
    }
  }

  // Fragment cache for static localized agronomic advice with dynamic client-side hydration
  val fragmentCache = com.example.core.cache.AgronomicFragmentCache()

  fun getHydratedAdvisory(disease: CropDisease): com.example.core.cache.HydratedAdvisory {
    val profile = farmerProfile.value
    val key = com.example.core.cache.AdvisoryFragmentKey(
      cropName = disease.cropName,
      diseaseName = disease.diseaseName,
      severity = disease.severity.label,
      language = currentLanguage.value
    )
    val context = com.example.core.cache.FarmerHoleContext(
      farmerName = profile.name,
      farmPlotName = profile.farmName,
      landAreaAcres = profile.totalLandAcres,
      villageLocality = "${profile.village}, ${profile.state}",
      scanTimestamp = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
    )
    return fragmentCache.getOrRenderHydrated(key, context) {
      com.example.core.cache.AdvisoryFragment(
        key = key,
        localizedHeader = "Agricultural Advisory: ${disease.diseaseName}",
        staticSymptomsFormatted = disease.symptoms.joinToString("\n• ", prefix = "• "),
        staticOrganicRemedy = disease.organicTreatment,
        staticChemicalTreatment = disease.chemicalTreatment,
        staticDosageFormula = disease.dosage,
        baseCostPerAcre = disease.estimatedCostInr.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 250.0,
        renderedTemplate = """
          |DIAGNOSTIC REPORT: ${disease.diseaseName}
          |Crop: ${disease.cropName} | Severity: ${disease.severity.label}
          |Farmer: {{FARMER_NAME}} | Plot: {{PLOT_NAME}} ({{VILLAGE}})
          |Acreage: {{AREA_ACRES}} Acres | Timestamp: {{SCAN_TIMESTAMP}}
          |──────────────────────────────────────────────────
          |SYMPTOMS:
          |${disease.symptoms.joinToString("\n• ", prefix = "• ")}
          |
          |ORGANIC TREATMENT:
          |${disease.organicTreatment}
          |
          |CHEMICAL TREATMENT:
          |${disease.chemicalTreatment}
          |Dosage: ${disease.dosage}
          |
          |TOTAL ESTIMATED COST ({{AREA_ACRES}} Acres): {{TOTAL_ESTIMATED_COST}}
        """.trimMargin()
      )
    }
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }

  fun refreshLocation() {
    viewModelScope.launch {
      _isFetchingLocation.value = true
      try {
        when (val result = repository.fetchCurrentLocation()) {
          is LocationResult.Success -> {
            val loc = result.locality ?: result.coordinates.toFormattedString()
            _snackbarMessage.value = "GPS Coordinates Acquired: $loc"
          }
          is LocationResult.PermissionDenied -> {
            _snackbarMessage.value = "Location permission needed to acquire farm GPS coordinates."
          }
          is LocationResult.LocationDisabled -> {
            _snackbarMessage.value = "Please enable GPS/Location in device settings."
          }
          is LocationResult.Error -> {
            _snackbarMessage.value = result.message
          }
        }
      } finally {
        _isFetchingLocation.value = false
      }
    }
  }
}
