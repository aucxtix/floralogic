package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * Compatibility delegate targeting unified presentation.theme.KisanAITheme
 */
@Composable
fun KisanAITheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  com.example.presentation.theme.KisanAITheme(
    darkTheme = darkTheme,
    dynamicColor = dynamicColor,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  com.example.presentation.theme.MyApplicationTheme(
    darkTheme = darkTheme,
    dynamicColor = dynamicColor,
    content = content
  )
}
