package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage

/**
 * High fidelity vector drawing of the KisanAI 3-petal brand logo icon.
 * Features 2 emerald/forest leaves and 1 golden leaf accent representing plant health & solar energy.
 */
@Composable
fun KisanLogoIcon(
  modifier: Modifier = Modifier,
  size: Dp = 48.dp,
  primaryColor: Color = KisanDeepForest,
  secondaryColor: Color = KisanEmerald,
  accentGoldColor: Color = KisanHarvestGold
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Left Leaf (Forest green / primary)
    val leftLeafPath = Path().apply {
      moveTo(w * 0.48f, h * 0.88f)
      cubicTo(w * 0.20f, h * 0.85f, w * 0.05f, h * 0.55f, w * 0.12f, h * 0.32f)
      cubicTo(w * 0.22f, h * 0.25f, w * 0.48f, h * 0.45f, w * 0.48f, h * 0.88f)
      close()
    }
    drawPath(leftLeafPath, color = primaryColor, style = Fill)

    // Center Main Leaf (Emerald)
    val centerLeafPath = Path().apply {
      moveTo(w * 0.50f, h * 0.90f)
      cubicTo(w * 0.35f, h * 0.50f, w * 0.38f, h * 0.20f, w * 0.56f, h * 0.10f)
      cubicTo(w * 0.72f, h * 0.22f, w * 0.68f, h * 0.60f, w * 0.50f, h * 0.90f)
      close()
    }
    drawPath(centerLeafPath, color = secondaryColor, style = Fill)

    // Right Accent Leaf (Harvest Gold)
    val rightLeafPath = Path().apply {
      moveTo(w * 0.52f, h * 0.86f)
      cubicTo(w * 0.62f, h * 0.68f, w * 0.72f, h * 0.48f, w * 0.88f, h * 0.38f)
      cubicTo(w * 0.94f, h * 0.52f, w * 0.82f, h * 0.78f, w * 0.52f, h * 0.86f)
      close()
    }
    drawPath(rightLeafPath, color = accentGoldColor, style = Fill)

    // Center subtle stem vein line
    drawLine(
      color = Color.White.copy(alpha = 0.65f),
      start = Offset(w * 0.50f, h * 0.86f),
      end = Offset(w * 0.54f, h * 0.32f),
      strokeWidth = w * 0.035f
    )
  }
}

/**
 * Brand logo header with stylized typography: "Kisan" in dark forest/white, "AI" in harvest gold.
 */
@Composable
fun KisanLogoHeader(
  modifier: Modifier = Modifier,
  iconSize: Dp = 42.dp,
  titleFontSize: Int = 26,
  isDarkTheme: Boolean = false,
  showTagline: Boolean = false,
  orientation: LogoOrientation = LogoOrientation.HORIZONTAL
) {
  val kisanColor = if (isDarkTheme) Color.White else KisanDeepForest

  val annotatedTitle = buildAnnotatedString {
    withStyle(SpanStyle(color = kisanColor, fontWeight = FontWeight.Bold)) {
      append("Kisan")
    }
    withStyle(SpanStyle(color = KisanHarvestGold, fontWeight = FontWeight.ExtraBold)) {
      append("AI")
    }
  }

  if (orientation == LogoOrientation.HORIZONTAL) {
    Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      KisanLogoIcon(
        size = iconSize,
        primaryColor = if (isDarkTheme) Color.White.copy(alpha = 0.85f) else KisanDeepForest,
        secondaryColor = if (isDarkTheme) KisanEmerald else KisanEmerald,
        accentGoldColor = KisanHarvestGold
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = annotatedTitle,
          fontSize = titleFontSize.sp,
          letterSpacing = (-0.5).sp
        )
        if (showTagline) {
          Text(
            text = "AI-powered intelligence for healthier crops.",
            fontSize = 11.sp,
            color = if (isDarkTheme) Color.White.copy(alpha = 0.75f) else KisanMutedSage
          )
        }
      }
    }
  } else {
    Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      KisanLogoIcon(
        size = iconSize,
        primaryColor = if (isDarkTheme) Color.White.copy(alpha = 0.9f) else KisanDeepForest,
        secondaryColor = if (isDarkTheme) KisanEmerald else KisanEmerald,
        accentGoldColor = KisanHarvestGold
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = annotatedTitle,
        fontSize = titleFontSize.sp,
        letterSpacing = (-0.5).sp
      )
      if (showTagline) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "AI-powered intelligence for healthier crops.",
          fontSize = 13.sp,
          color = if (isDarkTheme) Color.White.copy(alpha = 0.85f) else KisanMutedSage,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

enum class LogoOrientation {
  HORIZONTAL,
  VERTICAL
}
