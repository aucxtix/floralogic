package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.CompositionLocalProvider
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.ui.KisanViewModel
import com.example.ui.components.DrawerDestination
import com.example.ui.components.FarmerDrawerContent
import com.example.ui.components.KisanBottomBar
import com.example.ui.components.KisanTab
import com.example.ui.components.OfflineBanner
import com.example.ui.components.RealtimeAlertBanner
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.BiometricApprovalScreen
import com.example.ui.screens.CommandCenterScreen
import com.example.ui.screens.CropHealthScreen
import com.example.ui.screens.CropScannerScreen
import com.example.ui.screens.CropSelectionScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FarmActivityHistoryScreen
import com.example.ui.screens.FarmAnalyticsScreen
import com.example.ui.screens.FarmCopilotScreen
import com.example.ui.screens.FarmPlanScreen
import com.example.ui.screens.FarmScreen
import com.example.ui.screens.FilterCriteria
import com.example.ui.screens.FilterScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MarketScreen
import com.example.ui.screens.ModelTransparencyScreen
import com.example.ui.screens.MyAccountScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.ProblemStatementDemoScreen
import com.example.ui.screens.ProfileDialog
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SatelliteIntelligenceScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartFarmIoTScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WeatherIrrigationScreen
import com.example.ui.screens.WhatIfSimulatorScreen
import com.example.ui.screens.YieldIntelligenceScreen
import com.example.presentation.theme.KisanAITheme
import com.example.presentation.theme.MyApplicationTheme
import com.example.presentation.theme.KisanCharcoal
import com.example.presentation.theme.KisanDeepForest
import com.example.presentation.theme.KisanEmerald
import com.example.presentation.theme.KisanHarvestGold
import com.example.presentation.theme.KisanMutedSage
import com.example.presentation.theme.KisanWarmIvory
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

enum class AppFlowScreen {
  SPLASH,
  ONBOARDING,
  LOGIN,
  BIOMETRIC_APPROVAL,
  MAIN
}

class MainActivity : FragmentActivity() {
  private val _notificationDestination = mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleNotificationIntent(intent)
    enableEdgeToEdge()
    setContent {
      KisanAITheme {
        KisanApp(
          targetDestination = _notificationDestination.value,
          onDestinationHandled = { _notificationDestination.value = null }
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleNotificationIntent(intent)
  }

  private fun handleNotificationIntent(intent: Intent?) {
    val destination = intent?.getStringExtra("TARGET_DESTINATION")
    if (!destination.isNullOrEmpty()) {
      _notificationDestination.value = destination
    }
  }
}

@Composable
fun KisanApp(
    viewModel: KisanViewModel = viewModel(),
    targetDestination: String? = null,
    onDestinationHandled: () -> Unit = {}
) {
  val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
  val strings = viewModel.getStrings(currentLanguage)

  CompositionLocalProvider(
    com.example.data.model.LocalAppLanguage provides currentLanguage,
    com.example.data.model.LocalAppStrings provides strings
  ) {
    KisanAppContent(
      viewModel = viewModel,
      currentLanguage = currentLanguage,
      strings = strings,
      targetDestination = targetDestination,
      onDestinationHandled = onDestinationHandled
    )
  }
}

@Composable
fun KisanAppContent(
    viewModel: KisanViewModel,
    currentLanguage: AppLanguage,
    strings: AppStrings,
    targetDestination: String? = null,
    onDestinationHandled: () -> Unit = {}
) {
  val farmerProfile by viewModel.farmerProfile.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val crops by viewModel.crops.collectAsStateWithLifecycle()
  val scans by viewModel.scans.collectAsStateWithLifecycle()
  val scanState by viewModel.scanState.collectAsStateWithLifecycle()
  val selectedCropContext by viewModel.selectedCropContext.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
  val isFetchingLocation by viewModel.isFetchingLocation.collectAsStateWithLifecycle()
  val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
  
  val combinedIntelligence by viewModel.combinedIntelligence.collectAsStateWithLifecycle()
  val isIntelligenceLoading by viewModel.isIntelligenceLoading.collectAsStateWithLifecycle()

  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val activeRealtimeAlert by viewModel.activeRealtimeAlert.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
      if (combinedIntelligence == null && !isIntelligenceLoading) {
          viewModel.refreshIntelligence()
      }
  }

  var appFlowScreen by remember {
    mutableStateOf(if (isLoggedIn) AppFlowScreen.MAIN else AppFlowScreen.LOGIN)
  }

  LaunchedEffect(isLoggedIn) {
    if (!isLoggedIn && appFlowScreen == AppFlowScreen.MAIN) {
      appFlowScreen = AppFlowScreen.LOGIN
    }
  }
  var currentTab by remember { mutableStateOf(KisanTab.HOME) }
  var activeDrawerDestination by remember { mutableStateOf<DrawerDestination?>(null) }
  var showCropSelectionBeforeScan by remember { mutableStateOf(true) }
  var showCropScanner by remember { mutableStateOf(false) }
  var showProfileDialog by remember { mutableStateOf(false) }
  var showFilterScreen by remember { mutableStateOf(false) }
  var showModelTransparency by remember { mutableStateOf(false) }
  var showWeatherIrrigationDetail by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Runtime Notification Permission handling (Android 13+ / API 33+)
  val context = LocalContext.current
  val activity = context as? Activity
  val lifecycleOwner = LocalLifecycleOwner.current

  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.sendDummySystemNotification("WATER_STRESS")
    }
  }

  fun requestNotificationPermissionAndSendDummy(type: String = "WATER_STRESS") {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
      if (!hasPermission) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      } else {
        viewModel.sendDummySystemNotification(type)
      }
    } else {
      viewModel.sendDummySystemNotification(type)
    }
  }

  // Handle incoming notification click destination routing
  LaunchedEffect(targetDestination) {
    if (!targetDestination.isNullOrEmpty()) {
      try {
        activeDrawerDestination = DrawerDestination.valueOf(targetDestination)
      } catch (_: Exception) {
        currentTab = KisanTab.ALERTS
      }
      onDestinationHandled()
    }
  }

  // Automatically trigger dummy notification to the phone's system notification panel
  LaunchedEffect(isLoggedIn) {
    if (isLoggedIn) {
      requestNotificationPermissionAndSendDummy("WATER_STRESS")
    }
  }

  // Runtime Camera Permission handling
  var showPermissionDeniedDialog by remember { mutableStateOf(false) }
  var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
  var cameraPermissionRequestedOnce by rememberSaveable { mutableStateOf(false) }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      showPermissionDeniedDialog = false
      showPermanentlyDeniedDialog = false
      showCropScanner = true
    } else {
      val shouldShowRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
      } ?: false

      if (!shouldShowRationale && cameraPermissionRequestedOnce) {
        // Permanently denied (Don't ask again selected or device policy)
        showPermanentlyDeniedDialog = true
        showPermissionDeniedDialog = false
      } else {
        // Temporarily denied (rationale can be presented)
        showPermissionDeniedDialog = true
        showPermanentlyDeniedDialog = false
      }
    }
  }

  fun requestCameraPermissionAndNavigate() {
    val isGranted = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (isGranted) {
      showPermissionDeniedDialog = false
      showPermanentlyDeniedDialog = false
      showCropScanner = true
    } else {
      val shouldShowRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
      } ?: false

      if (cameraPermissionRequestedOnce && !shouldShowRationale) {
        showPermanentlyDeniedDialog = true
        showPermissionDeniedDialog = false
      } else {
        cameraPermissionRequestedOnce = true
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }
  }

  // Automatically dismiss permanently denied dialog if permission was granted in App Settings
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        val isGranted = ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
          showPermanentlyDeniedDialog = false
          showPermissionDeniedDialog = false
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbar()
    }
  }

  when (appFlowScreen) {
    AppFlowScreen.SPLASH -> {
      SplashScreen(
        onGetStarted = { appFlowScreen = AppFlowScreen.LOGIN }
      )
    }

    AppFlowScreen.ONBOARDING -> {
      OnboardingScreen(
        onGetStarted = { appFlowScreen = AppFlowScreen.MAIN },
        onSignInClick = { appFlowScreen = AppFlowScreen.LOGIN }
      )
    }

    AppFlowScreen.LOGIN -> {
      LoginScreen(
        viewModel = viewModel,
        onLoginSuccess = {
          appFlowScreen = AppFlowScreen.MAIN
        },
        onNavigateToRegister = {
          appFlowScreen = AppFlowScreen.MAIN
        },
        onNavigateToBiometric = { appFlowScreen = AppFlowScreen.BIOMETRIC_APPROVAL }
      )
    }

    AppFlowScreen.BIOMETRIC_APPROVAL -> {
      BiometricApprovalScreen(
        farmerProfile = farmerProfile,
        onApprovalSuccess = {
          viewModel.loginUser("rudra.patel@kisan.ai")
          appFlowScreen = AppFlowScreen.MAIN
        },
        onUsePassword = { appFlowScreen = AppFlowScreen.LOGIN }
      )
    }

    AppFlowScreen.MAIN -> {
      val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
      val coroutineScope = rememberCoroutineScope()

      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          FarmerDrawerContent(
            farmerProfile = farmerProfile,
            activeRoute = activeDrawerDestination,
            onNavigate = { destination ->
              coroutineScope.launch { drawerState.close() }
              when (destination) {
                DrawerDestination.DASHBOARD -> {
                  activeDrawerDestination = DrawerDestination.DASHBOARD
                }
                DrawerDestination.MY_FARMS -> {
                  activeDrawerDestination = null
                  currentTab = KisanTab.FARM
                }
                DrawerDestination.AI_PLANT_DOCTOR, DrawerDestination.SCAN_CROP -> {
                  activeDrawerDestination = null
                  showCropSelectionBeforeScan = true
                  currentTab = KisanTab.SCAN
                }
                DrawerDestination.DISEASE_HISTORY -> {
                  activeDrawerDestination = null
                  currentTab = KisanTab.HISTORY
                }
                DrawerDestination.WEATHER_CLIMATE, DrawerDestination.IRRIGATION_ADVISOR -> {
                  activeDrawerDestination = null
                  showWeatherIrrigationDetail = true
                  currentTab = KisanTab.ALERTS
                }
                else -> {
                  activeDrawerDestination = destination
                }
              }
            },
            onLogout = {
              coroutineScope.launch { drawerState.close() }
              viewModel.logoutUser()
              appFlowScreen = AppFlowScreen.LOGIN
            },
            strings = strings,
            activeFarmName = farmerProfile.farmName.ifEmpty { "Green Valley Farm" }
          )
        }
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
              KisanBottomBar(
                currentTab = currentTab,
                onTabSelected = {
                  activeDrawerDestination = null
                  currentTab = it
                },
                strings = strings,
              )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
          ) { innerPadding ->
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              if (!isOnline) {
                OfflineBanner(
                  message = "Offline mode: AI plant scanner and farm records are active offline."
                )
              }
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .weight(1f)
              ) {
                if (activeDrawerDestination != null) {
                  when (activeDrawerDestination) {
                    DrawerDestination.DASHBOARD -> {
                      CommandCenterScreen(
                        farmerProfile = farmerProfile,
                        weather = weather,
                        marketPrice = viewModel.marketPrices.collectAsStateWithLifecycle().value.firstOrNull(),
                        strings = strings,
                        activeFarmName = farmerProfile.farmName.ifEmpty { "Green Valley Farm – Tomato" },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onBackClick = { activeDrawerDestination = null },
                        onNavigateToMarket = { activeDrawerDestination = DrawerDestination.MARKET_INTELLIGENCE },
                        onNavigateToCropHealth = { activeDrawerDestination = DrawerDestination.CROP_HEALTH },
                        onNavigateToYield = { activeDrawerDestination = DrawerDestination.YIELD_INTELLIGENCE },
                        onNavigateToIrrigation = {
                          activeDrawerDestination = null
                          showWeatherIrrigationDetail = true
                          currentTab = KisanTab.ALERTS
                        },
                        onNavigateToWeather = {
                          activeDrawerDestination = null
                          showWeatherIrrigationDetail = true
                          currentTab = KisanTab.ALERTS
                        },
                        onNavigateToSatellite = { activeDrawerDestination = DrawerDestination.SATELLITE_INTELLIGENCE },
                        onNavigateToHardware = { activeDrawerDestination = DrawerDestination.SMART_FARM },
                        onNavigateToHistory = {
                          activeDrawerDestination = null
                          currentTab = KisanTab.HISTORY
                        },
                        onNavigateToCopilot = { activeDrawerDestination = DrawerDestination.AI_COPILOT }
                      )
                    }
                    DrawerDestination.CROP_HEALTH -> {
                      CropHealthScreen(
                        crops = crops,
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onNavigateToScan = {
                          activeDrawerDestination = null
                          showCropSelectionBeforeScan = true
                          currentTab = KisanTab.SCAN
                        },
                        onNavigateToYield = {
                          activeDrawerDestination = DrawerDestination.YIELD_INTELLIGENCE
                        }
                      )
                    }
                    DrawerDestination.YIELD_INTELLIGENCE, DrawerDestination.YIELD_HISTORY -> {
                      YieldIntelligenceScreen(
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onNavigateToIrrigation = {
                          activeDrawerDestination = null
                          showWeatherIrrigationDetail = true
                          currentTab = KisanTab.ALERTS
                        },
                        onNavigateToScan = {
                          activeDrawerDestination = null
                          showCropSelectionBeforeScan = true
                          currentTab = KisanTab.SCAN
                        }
                      )
                    }
                    DrawerDestination.IRRIGATION_ADVISOR, DrawerDestination.WEATHER_CLIMATE -> {
                      WeatherIrrigationScreen(
                        weather = weather,
                        farmerProfile = farmerProfile,
                        onRefreshLocation = { viewModel.refreshLocation() },
                        onSelectManualLocation = { city, state, lat, lon ->
                          viewModel.setManualLocation(city, state, lat, lon)
                        },
                        isRefreshingLocation = isFetchingLocation,
                        onBackClick = { activeDrawerDestination = null }
                      )
                    }
                    DrawerDestination.SATELLITE_INTELLIGENCE -> {
                      SatelliteIntelligenceScreen(
                        farmerProfile = farmerProfile,
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.SMART_FARM -> {
                      SmartFarmIoTScreen(
                        deviceHealth = viewModel.deviceHealth.collectAsStateWithLifecycle().value,
                        liveReading = viewModel.liveSensorReading.collectAsStateWithLifecycle().value,
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.FARM_PLAN, DrawerDestination.FARM_RECOMMENDATIONS -> {
                      FarmPlanScreen(
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.AI_COPILOT -> {
                      FarmCopilotScreen(
                        strings = strings,
                        viewModel = viewModel,
                        history = viewModel.copilotHistory.collectAsStateWithLifecycle().value,
                        onAskCopilot = { query, callback ->
                          viewModel.askCopilot(query, callback)
                        },
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.FARM_ANALYTICS -> {
                      FarmAnalyticsScreen(
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.WHAT_IF_SIMULATOR, DrawerDestination.COMPARE_CROPS -> {
                      WhatIfSimulatorScreen(
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.MARKET_INTELLIGENCE, DrawerDestination.MARKET_HISTORY -> {
                      MarketScreen(
                        farmerProfile = farmerProfile,
                        strings = strings,
                        viewModel = viewModel,
                        marketPrices = viewModel.marketPrices.collectAsStateWithLifecycle().value,
                        marketForecast = viewModel.marketForecast.collectAsStateWithLifecycle().value,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.FARM_ACTIVITY_HISTORY -> {
                      FarmActivityHistoryScreen(
                        strings = strings,
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.SETTINGS, DrawerDestination.HELP_HOW_IT_WORKS, DrawerDestination.HELP_SAFETY, DrawerDestination.HELP_ABOUT -> {
                      SettingsScreen(
                        currentLanguage = currentLanguage,
                        onLanguageChange = { viewModel.switchLanguage(it) },
                        farmerProfile = farmerProfile,
                        isBiometricHardwareSupported = true,
                        isBiometricLoginEnabled = true,
                        onBiometricToggle = { },
                        onLogout = {
                          viewModel.logoutUser()
                          appFlowScreen = AppFlowScreen.LOGIN
                        },
                        strings = strings,
                        onSeedDemoData = { viewModel.seedComprehensiveDemoData(force = true) },
                        onTriggerAlert = { type -> viewModel.triggerSimulatedAlert(type) },
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    DrawerDestination.PROBLEM_STATEMENT_DEMO, DrawerDestination.COMPLIANCE_MATRIX -> {
                      ProblemStatementDemoScreen(
                        strings = strings,
                        currentLanguage = currentLanguage,
                        onLanguageChange = { viewModel.switchLanguage(it) },
                        onSeedDemoData = { viewModel.seedComprehensiveDemoData(force = true) },
                        onTriggerAlert = { type -> viewModel.triggerSimulatedAlert(type) },
                        onNavigateToDestination = { activeDrawerDestination = it },
                        onBackClick = { activeDrawerDestination = null },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    else -> {
                      activeDrawerDestination = null
                    }
                  }
                } else {
                  when (currentTab) {
                    KisanTab.HOME -> {
                      HomeScreen(
                        strings = strings,
                        farmerProfile = farmerProfile,
                        weather = weather,
                        crops = crops,
                        recentScans = scans,
                        combinedIntelligence = combinedIntelligence,
                        isIntelligenceLoading = isIntelligenceLoading,
                        onAskCopilot = { query, callback ->
                          viewModel.askCopilot(query, callback)
                        },
                        onNavigateToScan = {
                          showCropSelectionBeforeScan = true
                          currentTab = KisanTab.SCAN
                        },
                        onNavigateToFarm = { currentTab = KisanTab.FARM },
                        onNavigateToWeather = {
                          showWeatherIrrigationDetail = true
                          currentTab = KisanTab.ALERTS
                        },
                        onNavigateToHistory = { currentTab = KisanTab.HISTORY },
                        onNavigateToMarket = { currentTab = KisanTab.MARKET },
                        onNavigateToHardware = { currentTab = KisanTab.FARM },
                        onNavigateToAlerts = { currentTab = KisanTab.ALERTS },
                        onProfileClick = { currentTab = KisanTab.PROFILE },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenFilter = { showFilterScreen = true }
                      )
                    }

                    KisanTab.SCAN -> {
                      if (showCropScanner) {
                        CropScannerScreen(
                          cropHint = selectedCropContext,
                          onImageCapturedWithCrop = { bitmap, crop ->
                            viewModel.setSelectedCropContext(crop)
                            showCropScanner = false
                            showCropSelectionBeforeScan = false
                            viewModel.scanLeaf(bitmap, crop, null, null)
                          },
                          onImageCaptured = { bitmap ->
                            showCropScanner = false
                            showCropSelectionBeforeScan = false
                            viewModel.scanLeaf(bitmap, selectedCropContext, null, null)
                          },
                          onClose = {
                            showCropScanner = false
                          }
                        )
                      } else if (showCropSelectionBeforeScan) {
                        CropSelectionScreen(
                          viewModel = viewModel,
                          onProceedToScan = { crop ->
                            requestCameraPermissionAndNavigate()
                          },
                          onNavigateBack = {
                            currentTab = KisanTab.HOME
                          }
                        )
                      } else {
                        ScanScreen(
                          strings = strings,
                          currentLanguage = currentLanguage,
                          scanState = scanState,
                          initialCrop = selectedCropContext,
                          onScanLeaf = { bitmap, cropHint, specimenId, imageUri ->
                            viewModel.scanLeaf(bitmap, cropHint, specimenId, imageUri)
                          },
                          onSaveScan = { viewModel.saveCurrentScanResult() },
                          onResetScan = {
                            viewModel.resetScanState()
                            showCropSelectionBeforeScan = true
                          },
                          onNavigateBack = {
                            showCropSelectionBeforeScan = true
                            currentTab = KisanTab.HOME
                          },
                          onNavigateToTransparency = { showModelTransparency = true },
                          onSubmitFeedback = { rating, reason ->
                            viewModel.submitScanFeedback(rating, reason)
                          },
                          onRequestLiveCamera = {
                            requestCameraPermissionAndNavigate()
                          }
                        )
                      }
                    }

                    KisanTab.FARM -> {
                      DashboardScreen(
                        crops = crops,
                        farmerProfile = farmerProfile,
                        deviceHealth = viewModel.deviceHealth.collectAsStateWithLifecycle().value,
                        liveReading = viewModel.liveSensorReading.collectAsStateWithLifecycle().value,
                        onAddCrop = { crop -> viewModel.addFarmCrop(crop) },
                        onDeleteCrop = { crop -> viewModel.deleteCrop(crop.id) },
                        strings = strings,
                        onOpenFilter = { showFilterScreen = true }
                      )
                    }

                    KisanTab.MARKET -> {
                      MarketScreen(
                        farmerProfile = farmerProfile,
                        strings = strings,
                        marketPrices = viewModel.marketPrices.collectAsStateWithLifecycle().value,
                        marketForecast = viewModel.marketForecast.collectAsStateWithLifecycle().value,
                        onBackClick = { currentTab = KisanTab.HOME },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                      )
                    }
                    KisanTab.ALERTS -> {
                      if (showWeatherIrrigationDetail) {
                        WeatherIrrigationScreen(
                          weather = weather,
                          farmerProfile = farmerProfile,
                          onRefreshLocation = { viewModel.refreshLocation() },
                          onSelectManualLocation = { city, state, lat, lon ->
                            viewModel.setManualLocation(city, state, lat, lon)
                          },
                          isRefreshingLocation = isFetchingLocation,
                          onBackClick = { showWeatherIrrigationDetail = false }
                        )
                      } else {
                        AlertsScreen(
                          strings = strings,
                          onNavigateToScan = {
                            showCropSelectionBeforeScan = true
                            currentTab = KisanTab.SCAN
                          },
                          onNavigateToIrrigation = {
                            showWeatherIrrigationDetail = true
                          },
                          onNavigateToMarket = { currentTab = KisanTab.MARKET },
                          onNavigateToHardware = { currentTab = KisanTab.FARM },
                          onSendDummyNotification = { type ->
                            requestNotificationPermissionAndSendDummy(type)
                          },
                          onBackClick = { currentTab = KisanTab.HOME }
                        )
                      }
                    }

                    KisanTab.HISTORY -> {
                      HistoryScreen(
                        strings = strings,
                        scans = scans,
                        onDeleteScan = { id -> viewModel.deleteScan(id) },
                        onNavigateToScan = { currentTab = KisanTab.SCAN }
                      )
                    }

                    KisanTab.PROFILE -> {
                      MyAccountScreen(
                        farmerProfile = farmerProfile,
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { viewModel.switchLanguage(it) },
                        onNavigateToBiometric = { appFlowScreen = AppFlowScreen.BIOMETRIC_APPROVAL },
                        onNavigateToHistory = { currentTab = KisanTab.HISTORY },
                        onNavigateToWeather = { currentTab = KisanTab.ALERTS },
                        onNavigateToTransparency = { showModelTransparency = true },
                        onLogout = {
                          viewModel.logoutUser()
                          appFlowScreen = AppFlowScreen.LOGIN
                        },
                        onBackClick = { currentTab = KisanTab.HOME }
                      )
                    }
                  }
                }
              }
            }
          }

          if (showProfileDialog) {
            ProfileDialog(
              strings = strings,
              profile = farmerProfile,
              onDismiss = { showProfileDialog = false },
              onSave = { updated ->
                viewModel.updateProfile(updated)
                showProfileDialog = false
              }
            )
          }

        // Overlay Filter Screen when triggered from Home or Dashboard
        if (showFilterScreen) {
          FilterScreen(
            onApplyFilters = {
              showFilterScreen = false
            },
            onDismiss = {
              showFilterScreen = false
            }
          )
        }

        // Overlay Model Architecture & Transparency Screen
        if (showModelTransparency) {
          ModelTransparencyScreen(
            onBackClick = {
              showModelTransparency = false
            }
          )
        }

        // Camera Permission Denied Explanation Dialog
        if (showPermissionDeniedDialog) {
          CameraPermissionDeniedDialog(
            onGrantPermission = {
              showPermissionDeniedDialog = false
              cameraPermissionRequestedOnce = true
              cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onDismiss = {
              showPermissionDeniedDialog = false
            }
          )
        }

        // Camera Permission Permanently Denied Dialog (with direct link to App Settings)
        if (showPermanentlyDeniedDialog) {
          CameraPermissionPermanentlyDeniedDialog(
            onOpenSettings = {
              showPermanentlyDeniedDialog = false
              openAppSettings(context)
            },
            onDismiss = {
              showPermanentlyDeniedDialog = false
            }
          )
        }
      }
    }
  }
}
}

/**
 * Opens system application details settings page so the farmer can grant camera permission manually.
 */
fun openAppSettings(context: Context) {
  try {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      data = Uri.fromParts("package", context.packageName, null)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  } catch (_: Exception) {
    try {
      val intent = Intent(Settings.ACTION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      // Fallback ignore if device intent fails
    }
  }
}

/**
 * User-friendly dialog shown when camera permission is denied temporarily.
 * Explains agricultural rationale for camera access and offers immediate retry.
 */
@Composable
fun CameraPermissionDeniedDialog(
  onGrantPermission: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    containerColor = Color.White,
    icon = {
      Icon(
        imageVector = Icons.Default.CameraAlt,
        contentDescription = "Camera Permission Required",
        tint = KisanEmerald,
        modifier = Modifier.size(36.dp)
      )
    },
    title = {
      Text(
        text = "Camera Access Required",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "KisanAI requires camera access to scan crop leaves, diagnose diseases, and recommend immediate agricultural remedies in real time.",
          fontSize = 14.sp,
          color = KisanCharcoal,
          lineHeight = 20.sp
        )
        Text(
          text = "Without camera permission, live leaf scanning is unavailable. However, you can still select sample specimens or pick photos from your device gallery.",
          fontSize = 12.sp,
          color = KisanMutedSage,
          lineHeight = 16.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onGrantPermission,
        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("grant_camera_permission_button")
      ) {
        Text("Grant Permission", fontWeight = FontWeight.SemiBold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("dismiss_permission_dialog_button")
      ) {
        Text("Not Now", color = KisanMutedSage)
      }
    },
    modifier = Modifier.testTag("camera_permission_denied_dialog")
  )
}

/**
 * User-friendly dialog shown when camera permission is permanently denied (or 'Don't ask again' chosen).
 * Provides clear instructions and an explicit action button to jump directly to app settings.
 */
@Composable
fun CameraPermissionPermanentlyDeniedDialog(
  onOpenSettings: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    containerColor = Color.White,
    icon = {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Camera Permission Disabled",
        tint = KisanHarvestGold,
        modifier = Modifier.size(36.dp)
      )
    },
    title = {
      Text(
        text = "Camera Permission Disabled",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Camera permission has been disabled for KisanAI. To scan crops in real time, please grant camera access in system settings.",
          fontSize = 14.sp,
          color = KisanCharcoal,
          lineHeight = 20.sp
        )
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = KisanWarmIvory,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "How to enable:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanDeepForest
            )
            Text(
              text = "1. Tap 'Open Settings' below\n2. Select 'Permissions'\n3. Turn on 'Camera'",
              fontSize = 12.sp,
              color = KisanCharcoal,
              lineHeight = 18.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onOpenSettings,
        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("open_settings_button")
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Open Settings", fontWeight = FontWeight.SemiBold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("dismiss_permanently_denied_dialog_button")
      ) {
        Text("Cancel", color = KisanMutedSage)
      }
    },
    modifier = Modifier.testTag("camera_permission_permanently_denied_dialog")
  )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  KisanAITheme { Greeting("KisanAI") }
}
