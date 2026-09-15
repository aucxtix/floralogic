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
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
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
import com.example.data.model.LocalAppStrings

@Composable
fun MyAccountScreen(
  farmerProfile: FarmerProfile,
  currentLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit,
  onNavigateToBiometric: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToWeather: () -> Unit,
  onNavigateToTransparency: () -> Unit = {},
  onLogout: () -> Unit,
  onBackClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  var biometricEnabled by remember { mutableStateOf(true) }
  var notificationsEnabled by remember { mutableStateOf(true) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("my_account_screen")
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
        text = "My Account",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Profile Identity Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = CircleShape,
          color = KisanEmeraldLight,
          border = BorderStroke(2.dp, KisanEmerald),
          modifier = Modifier.size(72.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "RP",
              fontSize = 26.sp,
              fontWeight = FontWeight.Bold,
              color = KisanDeepForest
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = farmerProfile.name,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )

        Text(
          text = "${farmerProfile.village}, ${farmerProfile.state}",
          fontSize = 13.sp,
          color = KisanMutedSage
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = KisanEmeraldLight,
          modifier = Modifier.padding(top = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = KisanEmerald,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = farmerProfile.mobileNumber,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanEmerald
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Farm Summary Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Farm Details",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanMutedSage
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Farm Name", fontSize = 12.sp, color = KisanMutedSage)
            Text(farmerProfile.farmName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
          }
          Column {
            Text("Land Size", fontSize = 12.sp, color = KisanMutedSage)
            Text("${farmerProfile.totalLandAcres} Acres", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
          }
          Column {
            Text("Primary Crop", fontSize = 12.sp, color = KisanMutedSage)
            Text(farmerProfile.primaryCrop, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Security & Biometrics Section
    Text(
      text = com.example.data.model.LocalizedStrings.safeTranslate("સુરક્ષા અને બાયોમેટ્રિક્સ", "Security & Biometrics"), // We will add to Language.kt next
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanMutedSage,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        // Biometric Toggle Item
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Surface(
              shape = CircleShape,
              color = KisanEmeraldLight,
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Biometric Quick Approval",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = KisanCharcoal
              )
              Text(
                text = "Fast login with fingerprint sensor",
                fontSize = 12.sp,
                color = KisanMutedSage
              )
            }
          }
          Switch(
            checked = biometricEnabled,
            onCheckedChange = { biometricEnabled = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = KisanEmerald
            )
          )
        }

        HorizontalDivider(color = KisanCardBorder.copy(alpha = 0.5f))

        // Quick Test Biometric Screen
        AccountSettingRow(
          icon = Icons.Default.Security,
          title = "Test Biometric Approval Screen",
          subtitle = "Preview biometric authentication UI",
          onClick = onNavigateToBiometric
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Preferences & Services Section
    Text(
      text = strings.drawerSettings,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanMutedSage,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        // App Language
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = KisanEmeraldLight,
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Language,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "App Language",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = KisanCharcoal
              )
              Text(
                text = currentLanguage.displayName,
                fontSize = 12.sp,
                color = KisanMutedSage
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AppLanguage.values().forEach { lang ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentLanguage == lang) KisanDeepForest else KisanWarmIvory,
                border = BorderStroke(1.dp, if (currentLanguage == lang) KisanDeepForest else KisanCardBorder),
                modifier = Modifier
                  .clickable { onLanguageSelected(lang) }
                  .padding(1.dp)
              ) {
                Text(
                  text = lang.code.uppercase(),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (currentLanguage == lang) Color.White else KisanCharcoal,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        HorizontalDivider(color = KisanCardBorder.copy(alpha = 0.5f))

        // Live GPS Location
        AccountSettingRow(
          icon = Icons.Default.LocationOn,
          title = "GPS Farm Coordinates",
          subtitle = "View FusedLocationProviderClient integration",
          onClick = onNavigateToWeather
        )

        HorizontalDivider(color = KisanCardBorder.copy(alpha = 0.5f))

        // Scan History
        AccountSettingRow(
          icon = Icons.Default.History,
          title = "Scan History & Diagnoses",
          subtitle = "Browse previous AI crop health scans",
          onClick = onNavigateToHistory
        )

        HorizontalDivider(color = KisanCardBorder.copy(alpha = 0.5f))

        // AI Model Transparency & Dataset Provenance
        AccountSettingRow(
          icon = Icons.Default.Science,
          title = "Model Transparency & Architecture",
          subtitle = "MobileNetV3 (4.2 MB) • 94.2% Top-1 Accuracy",
          onClick = onNavigateToTransparency
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Logout Button
    OutlinedButton(
      onClick = onLogout,
      shape = RoundedCornerShape(24.dp),
      border = BorderStroke(1.dp, Color(0xFFC94C4C)),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("account_logout_button")
    ) {
      Text(
        text = "Log Out from Farm",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFC94C4C)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Brand Footnote
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      KisanLogoHeader(
        iconSize = 32.dp,
        titleFontSize = 20,
        isDarkTheme = false
      )
    }

    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun AccountSettingRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Surface(
        shape = CircleShape,
        color = KisanEmeraldLight,
        modifier = Modifier.size(38.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KisanEmerald,
            modifier = Modifier.size(20.dp)
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
          fontSize = 12.sp,
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
