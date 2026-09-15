package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.ui.components.KisanLogoHeader
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@Composable
fun ProfileScreen(
  farmerProfile: FarmerProfile,
  currentLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit,
  onNavigateToSplash: () -> Unit,
  onNavigateToOnboarding: () -> Unit,
  onNavigateToLogin: () -> Unit,
  onBackClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBackClick,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = KisanCharcoal
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Farmer Profile",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Profile Summary Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = KisanEmeraldLight,
          border = BorderStroke(2.dp, KisanEmerald),
          modifier = Modifier.size(60.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "RP",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = KisanDeepForest
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
          Text(
            text = farmerProfile.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${farmerProfile.farmName} • ${farmerProfile.totalLandAcres} acres",
            fontSize = 13.sp,
            color = KisanMutedSage
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "📍 ${farmerProfile.village}, ${farmerProfile.state}",
            fontSize = 12.sp,
            color = KisanEmerald,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Language Selector
    Text(
      text = "App Language",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        AppLanguage.values().forEachIndexed { index, lang ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onLanguageSelected(lang) }
              .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = if (currentLanguage == lang) KisanEmerald else KisanMutedSage,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = lang.nativeName,
                fontSize = 14.sp,
                fontWeight = if (currentLanguage == lang) FontWeight.Bold else FontWeight.Normal,
                color = KisanCharcoal
              )
            }

            if (currentLanguage == lang) {
              Surface(
                shape = CircleShape,
                color = KisanEmeraldLight,
                modifier = Modifier.size(24.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "✓",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = KisanEmerald
                  )
                }
              }
            }
          }
          if (index < AppLanguage.values().size - 1) {
            HorizontalDivider(color = Color(0xFFF0F0F0))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Measurement Units
    Text(
      text = "Measurement Units",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Land Area Unit", fontSize = 13.sp, color = KisanCharcoal)
          Text("Acres", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF0F0F0))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Weight Unit", fontSize = 13.sp, color = KisanCharcoal)
          Text("Quintal (100 kg)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF0F0F0))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Temperature", fontSize = 13.sp, color = KisanCharcoal)
          Text("Celsius (°C)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Security & Biometric Login
    Text(
      text = "Security & Access",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Biometric Authentication", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
              Text("Fingerprint / Face unlock", fontSize = 11.sp, color = KisanMutedSage)
            }
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = KisanEmeraldLight
          ) {
            Text("Active", color = KisanEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Connected IoT Devices
    Text(
      text = "Connected Farm Hardware",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("ESP32 Farm Monitor #001", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
            Text("Soil Moisture & Weather Telemetry Node", fontSize = 11.sp, color = KisanMutedSage)
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFDCFCE7)
          ) {
            Text("ONLINE", color = Color(0xFF16A34A), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // AI & Data Preferences
    Text(
      text = "AI & Data Preferences",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Offline AI Disease Model", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
            Text("Edge inference without internet", fontSize = 11.sp, color = KisanMutedSage)
          }
          Text("Enabled", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF0F0F0))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Satellite Auto-Cache", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
            Text("Pre-fetch Sentinel-2 indices", fontSize = 11.sp, color = KisanMutedSage)
          }
          Text("Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
        }
      }
    }

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(6.dp)) {
        ProfileNavRow(
          icon = Icons.Default.PlayCircleOutline,
          title = "Launch Splash Screen",
          subtitle = "Deep forest green brand entrance",
          onClick = onNavigateToSplash
        )
        HorizontalDivider(color = Color(0xFFF0F0F0))
        ProfileNavRow(
          icon = Icons.Default.Eco,
          title = "Onboarding Screen",
          subtitle = "Field landscape and core value props",
          onClick = onNavigateToOnboarding
        )
        HorizontalDivider(color = Color(0xFFF0F0F0))
        ProfileNavRow(
          icon = Icons.Default.Lock,
          title = "Farmer Login Screen",
          subtitle = "Phone & credentials authentication",
          onClick = onNavigateToLogin
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Branding & Version
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      KisanLogoHeader(
        iconSize = 36.dp,
        titleFontSize = 22,
        isDarkTheme = false
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "KisanAI v1.2.0 • Offline AI & CameraX Enabled",
        fontSize = 12.sp,
        color = KisanMutedSage
      )
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun ProfileNavRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Surface(
        shape = CircleShape,
        color = KisanEmeraldLight,
        modifier = Modifier.size(36.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KisanEmerald,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanCharcoal
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = KisanMutedSage
        )
      }
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = KisanMutedSage,
      modifier = Modifier.size(20.dp)
    )
  }
}
