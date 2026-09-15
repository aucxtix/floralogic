package com.example.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// =========================================================================
// KisanAI Brand Colors (Agricultural Identity)
// =========================================================================
val KisanDeepForest = Color(0xFF12372A)
val KisanEmerald = Color(0xFF1F7A5A)
val KisanHarvestGold = Color(0xFFD9A441)
val KisanWarmIvory = Color(0xFFF7F5EF)
val KisanCharcoal = Color(0xFF17211C)
val KisanMutedSage = Color(0xFF68756D)
val KisanEarthRed = Color(0xFFC94C4C)
val KisanWhite = Color(0xFFFFFFFF)

// Supplementary Tints & Surfaces
val KisanEmeraldLight = Color(0xFFE8F5EE)
val KisanGoldLight = Color(0xFFFEF7E6)
val KisanSageLight = Color(0xFFEDF1EE)
val KisanRedLight = Color(0xFFFDECEB)
val KisanCardBorder = Color(0xFFE5E9E4)
val KisanDivider = Color(0xFFEAECE8)

// Semantic Agricultural Role Mappings
val ForestGreenPrimary = KisanEmerald
val ForestGreenOnPrimary = Color(0xFFFFFFFF)
val ForestGreenContainer = KisanEmeraldLight
val ForestGreenOnContainer = KisanDeepForest

val HarvestAmber = KisanHarvestGold
val HarvestAmberContainer = KisanGoldLight
val HarvestAmberOnContainer = Color(0xFF451A03)

val IrrigationBlue = Color(0xFF0284C7)
val IrrigationBlueContainer = Color(0xFFE0F2FE)
val IrrigationBlueOnContainer = Color(0xFF0C4A6E)

// Dark Theme Variants
val LightGreenPrimaryDark = Color(0xFF5CD29E)
val ForestGreenContainerDark = Color(0xFF12372A)
val HarvestAmberDark = Color(0xFFF5BE58)
val HarvestAmberContainerDark = Color(0xFF5C410B)
val IrrigationBlueDark = Color(0xFF38BDF8)
val IrrigationBlueContainerDark = Color(0xFF075985)

val AlertRed = KisanEarthRed
val AlertYellow = KisanHarvestGold
val HealthyGreen = KisanEmerald
val SurfaceEarthy = KisanWarmIvory
val SurfaceCard = Color(0xFFFFFFFF)

// =========================================================================
// Material 3 Color Schemes
// =========================================================================
val KisanLightColorScheme = lightColorScheme(
  primary = ForestGreenPrimary,
  onPrimary = ForestGreenOnPrimary,
  primaryContainer = ForestGreenContainer,
  onPrimaryContainer = ForestGreenOnContainer,
  inversePrimary = LightGreenPrimaryDark,
  secondary = HarvestAmber,
  onSecondary = Color.White,
  secondaryContainer = HarvestAmberContainer,
  onSecondaryContainer = HarvestAmberOnContainer,
  tertiary = IrrigationBlue,
  onTertiary = Color.White,
  tertiaryContainer = IrrigationBlueContainer,
  onTertiaryContainer = IrrigationBlueOnContainer,
  error = KisanEarthRed,
  onError = Color.White,
  errorContainer = KisanRedLight,
  onErrorContainer = Color(0xFF410002),
  background = SurfaceEarthy,
  onBackground = KisanCharcoal,
  surface = SurfaceCard,
  onSurface = KisanCharcoal,
  surfaceVariant = KisanSageLight,
  onSurfaceVariant = KisanMutedSage,
  outline = KisanCardBorder,
  outlineVariant = Color(0xFFD0D7D2)
)

val KisanDarkColorScheme = darkColorScheme(
  primary = LightGreenPrimaryDark,
  onPrimary = Color(0xFF003915),
  primaryContainer = ForestGreenContainerDark,
  onPrimaryContainer = Color(0xFFA6F4C5),
  inversePrimary = ForestGreenPrimary,
  secondary = HarvestAmberDark,
  onSecondary = Color(0xFF451A03),
  secondaryContainer = HarvestAmberContainerDark,
  onSecondaryContainer = Color(0xFFFEF3C7),
  tertiary = IrrigationBlueDark,
  onTertiary = Color(0xFF00354E),
  tertiaryContainer = IrrigationBlueContainerDark,
  onTertiaryContainer = Color(0xFFCBE6FF),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
  errorContainer = Color(0xFF93000A),
  onErrorContainer = Color(0xFFFFDAD6),
  background = Color(0xFF111813),
  onBackground = Color(0xFFE2E8F0),
  surface = Color(0xFF17211A),
  onSurface = Color(0xFFE2E8F0),
  surfaceVariant = Color(0xFF223026),
  onSurfaceVariant = Color(0xFF94A39B),
  outline = Color(0xFF3B4B40),
  outlineVariant = Color(0xFF2B3A30)
)
