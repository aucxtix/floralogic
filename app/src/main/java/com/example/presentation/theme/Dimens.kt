package com.example.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized dimensions adhering strictly to standard 8dp-grid guidelines.
 */
object Dimens {
  // Spacing & Margins (8dp grid)
  val spaceNone: Dp = 0.dp
  val spaceExtraSmall: Dp = 4.dp
  val spaceSmall: Dp = 8.dp
  val spaceMedium: Dp = 12.dp
  val spaceStandard: Dp = 16.dp
  val spaceLarge: Dp = 20.dp
  val spaceExtraLarge: Dp = 24.dp
  val spaceHuge: Dp = 32.dp
  val spaceMassive: Dp = 48.dp

  // Component Heights
  val buttonHeight: Dp = 50.dp
  val buttonSmallHeight: Dp = 40.dp
  val inputFieldHeight: Dp = 56.dp
  val bottomBarHeight: Dp = 72.dp
  val topBarHeight: Dp = 64.dp
  val minTouchTargetSize: Dp = 48.dp

  // Icon & Avatar Sizes
  val iconSmall: Dp = 16.dp
  val iconMedium: Dp = 20.dp
  val iconStandard: Dp = 24.dp
  val iconLarge: Dp = 32.dp
  val iconHero: Dp = 48.dp

  val avatarSmall: Dp = 36.dp
  val avatarMedium: Dp = 44.dp
  val avatarLarge: Dp = 56.dp
  val avatarHero: Dp = 80.dp

  // Card & Corner Radii
  val cornerSmall: Dp = 8.dp
  val cornerMedium: Dp = 12.dp
  val cornerStandard: Dp = 16.dp
  val cornerLarge: Dp = 20.dp
  val cornerExtraLarge: Dp = 24.dp
  val cornerPill: Dp = 999.dp

  // Elevation
  val elevationNone: Dp = 0.dp
  val elevationLow: Dp = 1.dp
  val elevationMedium: Dp = 3.dp
  val elevationHigh: Dp = 6.dp
}
