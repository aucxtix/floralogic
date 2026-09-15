package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.presentation.theme.KisanCharcoal
import com.example.presentation.theme.KisanEmerald
import com.example.presentation.theme.KisanMutedSage
import com.example.presentation.theme.KisanWhite

enum class KisanTab(val icon: ImageVector, val tag: String) {
  HOME(Icons.Default.Home, "tab_home"),
  FARM(Icons.Default.Agriculture, "tab_farm"),
  SCAN(Icons.Default.CameraAlt, "tab_scan"),
  MARKET(Icons.Default.TrendingUp, "tab_market"),
  ALERTS(Icons.Default.Notifications, "tab_alerts"),
  PROFILE(Icons.Default.Person, "tab_profile"),
  HISTORY(Icons.Default.Notifications, "tab_history")
}

@Composable
fun KisanBottomBar(
  currentTab: KisanTab,
  onTabSelected: (KisanTab) -> Unit,
  strings: AppStrings,
  modifier: Modifier = Modifier
) {
  Card(
    shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    colors = CardDefaults.cardColors(containerColor = KisanWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    modifier = modifier
      .fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(top = 8.dp, bottom = 8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 1: Home
        BottomNavItem(
          icon = Icons.Default.Home,
          label = strings.navHome,
          selected = currentTab == KisanTab.HOME,
          testTag = "tab_home",
          onClick = { onTabSelected(KisanTab.HOME) }
        )

        // Tab 2: Farms
        BottomNavItem(
          icon = Icons.Default.Agriculture,
          label = strings.navFarm,
          selected = currentTab == KisanTab.FARM,
          testTag = "tab_farm",
          onClick = { onTabSelected(KisanTab.FARM) }
        )

        // Center Gap for the elevated Scan button
        Box(modifier = Modifier.size(54.dp))

        // Tab 3: Alerts
        BottomNavItem(
          icon = Icons.Default.Notifications,
          label = strings.navAlerts,
          selected = currentTab == KisanTab.ALERTS,
          testTag = "tab_alerts",
          badgeCount = 3,
          onClick = { onTabSelected(KisanTab.ALERTS) }
        )

        // Tab 4: Profile
        BottomNavItem(
          icon = Icons.Default.Person,
          label = strings.navProfile,
          selected = currentTab == KisanTab.PROFILE,
          testTag = "tab_profile",
          onClick = { onTabSelected(KisanTab.PROFILE) }
        )
      }

      // Center Elevated Scan FAB Button
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .offset(y = (-14).dp)
      ) {
        Surface(
          shape = CircleShape,
          color = KisanEmerald,
          shadowElevation = 8.dp,
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable { onTabSelected(KisanTab.SCAN) }
            .testTag("center_scan_fab_button")
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Start AI Scan",
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BottomNavItem(
  icon: ImageVector,
  label: String,
  selected: Boolean,
  testTag: String,
  badgeCount: Int = 0,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clip(CircleShape)
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) KisanEmerald else KisanMutedSage,
        modifier = Modifier.size(24.dp)
      )
      if (badgeCount > 0) {
        Surface(
          color = Color(0xFFDC2626),
          shape = CircleShape,
          modifier = Modifier.offset(x = 6.dp, y = (-4).dp)
        ) {
          Text(
            text = "$badgeCount",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      color = if (selected) KisanEmerald else KisanMutedSage
    )
  }
}
