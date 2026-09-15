package com.example.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Standard Material 3 Shapes for KisanAI.
 */
val KisanShapes = Shapes(
  extraSmall = RoundedCornerShape(4.dp),
  small = RoundedCornerShape(8.dp),
  medium = RoundedCornerShape(12.dp),
  large = RoundedCornerShape(16.dp),
  extraLarge = RoundedCornerShape(24.dp)
)

// Semantic shapes for specific UI components
val PillShape = RoundedCornerShape(50.dp)
val DialogShape = RoundedCornerShape(20.dp)
val CardShape = RoundedCornerShape(16.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val InputFieldShape = RoundedCornerShape(12.dp)

val Shapes = KisanShapes
