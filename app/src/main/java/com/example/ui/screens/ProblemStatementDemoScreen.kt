package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.presentation.theme.*
import com.example.ui.components.DrawerDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemStatementDemoScreen(
  strings: AppStrings,
  currentLanguage: AppLanguage,
  onLanguageChange: (AppLanguage) -> Unit,
  onSeedDemoData: () -> Unit,
  onTriggerAlert: (String) -> Unit,
  onNavigateToDestination: (DrawerDestination) -> Unit,
  onBackClick: () -> Unit,
  onOpenDrawer: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = strings.drawerProblemStatementDemo,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Live Functionality & Verification Hub",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.8f)
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("back_button_demo")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        actions = {
          IconButton(
            onClick = onOpenDrawer,
            modifier = Modifier.testTag("open_drawer_button_demo")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "Menu",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = KisanDeepForest
        )
      )
    },
    modifier = modifier
  ) { padding ->
    LazyColumn(
      contentPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = padding.calculateTopPadding() + 12.dp,
        bottom = 90.dp
      ),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier
        .fillMaxSize()
        .background(KisanWarmIvory)
    ) {
      // 1. Quick Demo Actions Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = KisanEmerald,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Seed & Live Notification Tools",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
            }
            Text(
              text = "Populate realistic sample farm crops, disease scans, and test dummy real-time alerts.",
              fontSize = 12.sp,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Seed Data Button
            Button(
              onClick = onSeedDemoData,
              colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("seed_demo_data_button")
            ) {
              Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Dump Seed Demo Data (5 Crops + 5 Scans)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trigger Real-time Alerts Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { onTriggerAlert("IRRIGATION") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626))),
                modifier = Modifier
                  .weight(1f)
                  .testTag("trigger_irrigation_alert_button")
              ) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Trigger Water Alert", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              }

              OutlinedButton(
                onClick = { onTriggerAlert("MARKET") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0284C7)),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0284C7))),
                modifier = Modifier
                  .weight(1f)
                  .testTag("trigger_market_alert_button")
              ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Trigger Mandi Alert", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      // 2. Unbiased Language Verification Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = Color(0xFF7C3AED),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Language Localization Test",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
            }
            Text(
              text = "Fully unbiased 3-language system: English, Hindi, and Gujarati with complete parity across all UI strings.",
              fontSize = 12.sp,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              LanguageTestChip(
                language = AppLanguage.ENGLISH,
                label = "English",
                sublabel = "Standard",
                isSelected = currentLanguage == AppLanguage.ENGLISH,
                onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                modifier = Modifier.weight(1f)
              )
              LanguageTestChip(
                language = AppLanguage.HINDI,
                label = "हिंदी",
                sublabel = "Hindi",
                isSelected = currentLanguage == AppLanguage.HINDI,
                onClick = { onLanguageChange(AppLanguage.HINDI) },
                modifier = Modifier.weight(1f)
              )
              LanguageTestChip(
                language = AppLanguage.GUJARATI,
                label = "ગુજરાતી",
                sublabel = "Gujarati",
                isSelected = currentLanguage == AppLanguage.GUJARATI,
                onClick = { onLanguageChange(AppLanguage.GUJARATI) },
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF8FAFC),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "Active Language String Sample:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Side Drawer Header: ${strings.sectionFarmIntelligence}", fontSize = 12.sp, color = KisanCharcoal)
                Text("• Plant Doctor Label: ${strings.drawerAiPlantDoctor}", fontSize = 12.sp, color = KisanCharcoal)
                Text("• Mandi Rates Label: ${strings.drawerMarketIntelligence}", fontSize = 12.sp, color = KisanCharcoal)
                Text("• Scan Camera Action: ${strings.scanNow}", fontSize = 12.sp, color = KisanCharcoal)
              }
            }
          }
        }
      }

      // 3. Problem Statement Architectural Flow
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "OBSERVE → ANALYZE → PREDICT → RECOMMEND → ALERT → TRACK",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = KisanEmerald,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Core Farmer Intelligence Modules",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Text(
              text = "Tap any module to inspect its live functional implementation.",
              fontSize = 12.sp,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(bottom = 12.dp)
            )

            ModuleJumpItem(
              icon = Icons.Default.CameraAlt,
              title = "AI Plant Doctor",
              subtitle = "Disease detection, severity %, organic & chemical remedies",
              badge = "ANALYZE",
              onClick = { onNavigateToDestination(DrawerDestination.AI_PLANT_DOCTOR) }
            )

            ModuleJumpItem(
              icon = Icons.Default.WaterDrop,
              title = "Irrigation Advisor",
              subtitle = "Soil moisture threshold logic, evapotranspiration & schedule",
              badge = "OBSERVE",
              onClick = { onNavigateToDestination(DrawerDestination.IRRIGATION_ADVISOR) }
            )

            ModuleJumpItem(
              icon = Icons.Default.Storefront,
              title = "Market Intelligence",
              subtitle = "Current APMC mandi rates, min/max/modal prices & forecast",
              badge = "PREDICT",
              onClick = { onNavigateToDestination(DrawerDestination.MARKET_INTELLIGENCE) }
            )

            ModuleJumpItem(
              icon = Icons.Default.SatelliteAlt,
              title = "Satellite Intelligence",
              subtitle = "NDVI vegetation health, moisture index & canopy vigor",
              badge = "OBSERVE",
              onClick = { onNavigateToDestination(DrawerDestination.SATELLITE_INTELLIGENCE) }
            )

            ModuleJumpItem(
              icon = Icons.Default.Sensors,
              title = "Smart Farm / IoT",
              subtitle = "ESP32 telemetry, soil pH, moisture, LoRa status",
              badge = "OBSERVE",
              onClick = { onNavigateToDestination(DrawerDestination.SMART_FARM) }
            )

            ModuleJumpItem(
              icon = Icons.Default.CalendarMonth,
              title = "7-Day Farm Plan",
              subtitle = "Grounded agronomic daily tasks tailored to farmer crops",
              badge = "RECOMMEND",
              onClick = { onNavigateToDestination(DrawerDestination.FARM_PLAN) }
            )

            ModuleJumpItem(
              icon = Icons.Default.Notifications,
              title = "Farm Alerts Center",
              subtitle = "Priority alerts for water stress, pest outbreaks & price spikes",
              badge = "ALERT",
              onClick = { onNavigateToDestination(DrawerDestination.DISEASE_HISTORY) }
            )

            ModuleJumpItem(
              icon = Icons.Default.History,
              title = "Farm Activity History",
              subtitle = "Complete chronological event log with timestamps & filters",
              badge = "TRACK",
              onClick = { onNavigateToDestination(DrawerDestination.FARM_ACTIVITY_HISTORY) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LanguageTestChip(
  language: AppLanguage,
  label: String,
  sublabel: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) KisanEmerald.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, KisanEmerald) else null,
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
      Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) KisanEmerald else KisanCharcoal
      )
      Text(
        text = sublabel,
        fontSize = 10.sp,
        color = Color(0xFF64748B)
      )
    }
  }
}

@Composable
private fun ModuleJumpItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  badge: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFF8FAFC),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(KisanEmerald.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFFE2E8F0)
          ) {
            Text(
              text = badge,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF475569),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = Color(0xFF64748B),
          maxLines = 1
        )
      }
      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = Color(0xFF94A3B8),
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
