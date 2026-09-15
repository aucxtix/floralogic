package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    farmerProfile: FarmerProfile,
    isBiometricHardwareSupported: Boolean,
    isBiometricLoginEnabled: Boolean,
    onBiometricToggle: (Boolean) -> Unit,
    onLogout: () -> Unit,
    strings: AppStrings,
    onSeedDemoData: () -> Unit = {},
    onTriggerAlert: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var marketAlerts by remember { mutableStateOf(true) }
    var diseaseAlerts by remember { mutableStateOf(true) }
    var weatherAlerts by remember { mutableStateOf(true) }
    var hardwareAlerts by remember { mutableStateOf(true) }
    var showTechTerms by remember { mutableStateOf(false) }
    var offlineSyncEnabled by remember { mutableStateOf(true) }
    var showModelEvaluationDashboard by remember { mutableStateOf(false) }

    if (showModelEvaluationDashboard) {
        ModelEvaluationDashboardScreen(
            onBackClick = { showModelEvaluationDashboard = false },
            modifier = modifier
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "System Preferences & Farmer Profile",
                            fontSize = 11.sp,
                            color = KisanMutedSage
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KisanCharcoal)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = KisanEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KisanWarmIvory)
            )
        },
        containerColor = KisanWarmIvory,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("settings_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Language Selection
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = KisanEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.drawerSettings + " / Language", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppLanguage.entries.forEach { lang ->
                                val isSel = currentLanguage == lang
                                Button(
                                    onClick = { onLanguageChange(lang) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSel) KisanEmerald else Color(0xFFF1F5F9),
                                        contentColor = if (isSel) Color.White else KisanCharcoal
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(lang.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(lang.nativeName, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Security & Biometrics
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = KisanDeepForest)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Security & Access", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Biometric Login", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
                                Text("Use fingerprint to sign in without password", fontSize = 11.sp, color = KisanMutedSage)
                            }
                            Switch(
                                checked = isBiometricLoginEnabled,
                                onCheckedChange = onBiometricToggle,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KisanEmerald)
                            )
                        }
                    }
                }
            }

            // Notifications Preferences
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notification Categories", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        SettingToggleRow("Market Price Fluctuations (+5%)", marketAlerts) { marketAlerts = it }
                        SettingToggleRow("High Disease Risk Warnings", diseaseAlerts) { diseaseAlerts = it }
                        SettingToggleRow("Heavy Rain & Weather Forecasts", weatherAlerts) { weatherAlerts = it }
                        SettingToggleRow("IoT Soil Sensor Critical Thresholds", hardwareAlerts) { hardwareAlerts = it }
                    }
                }
            }

            // Farmer-First UX & Technical Terms
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF7C3AED))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Farmer-First AI Display", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        SettingToggleRow("Show Advanced Technical Terms (NDVI, GPS coordinates)", showTechTerms) { showTechTerms = it }
                        SettingToggleRow("Enable Offline Fast Diagnostic Cache", offlineSyncEnabled) { offlineSyncEnabled = it }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showModelEvaluationDashboard = true },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_open_model_metrics")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View AI Model Evaluation Benchmarks",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }
                }
            }

            // Demo Data & Live Notification Testing
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Demo Seeding & Real-Time Alert Testing", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        }
                        Text(
                            "Populate realistic sample farm crops, disease scans, and test dummy real-time alerts.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )
                        Button(
                            onClick = onSeedDemoData,
                            colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dump Seed Demo Data (5 Crops + 5 Scans)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onTriggerAlert("IRRIGATION") },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626))),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Water Alert", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onTriggerAlert("MARKET") },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0284C7)),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0284C7))),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mandi Alert", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Logout Action
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onLogout() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out of Kissan AI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 12.sp, color = KisanCharcoal, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KisanEmerald)
        )
    }
}
