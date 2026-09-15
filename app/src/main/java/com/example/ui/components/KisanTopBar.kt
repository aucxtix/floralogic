package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KisanTopBar(
  strings: AppStrings,
  currentLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit,
  onProfileClick: () -> Unit
) {
  var showLanguageMenu by remember { mutableStateOf(false) }

  TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.onSurface
    ),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = "KisanAI Logo",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = strings.appName,
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    },
    actions = {
      // Offline Status Pill
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        modifier = Modifier.padding(end = 4.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = "Offline Ready",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Offline AI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }

      // Language Switcher Dropdown
      Box {
        IconButton(
          onClick = { showLanguageMenu = true },
          modifier = Modifier.testTag("language_switch_button")
        ) {
          Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "Select Language",
            tint = MaterialTheme.colorScheme.primary
          )
        }

        DropdownMenu(
          expanded = showLanguageMenu,
          onDismissRequest = { showLanguageMenu = false }
        ) {
          AppLanguage.values().forEach { language ->
            DropdownMenuItem(
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = language.nativeName,
                    fontWeight = if (language == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                    color = if (language == currentLanguage) MaterialTheme.colorScheme.primary else Color.Unspecified
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "(${language.displayName})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              },
              onClick = {
                onLanguageSelected(language)
                showLanguageMenu = false
              }
            )
          }
        }
      }

      // Profile Button
      IconButton(
        onClick = onProfileClick,
        modifier = Modifier.testTag("farmer_profile_button")
      ) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Farmer Profile",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  )
}
