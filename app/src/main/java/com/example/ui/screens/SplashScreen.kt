package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KisanLogoHeader
import com.example.ui.components.KisanLogoIcon
import com.example.ui.components.LogoOrientation
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanHarvestGold

@Composable
fun SplashScreen(
  onGetStarted: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "logo_pulse"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            KisanDeepForest,
            Color(0xFF0D251C),
            Color(0xFF091913)
          )
        )
      )
      .clickable { onGetStarted() }
      .testTag("splash_screen_container")
  ) {
    // Decorative organic background leaf silhouette
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val leafGlowPath = Path().apply {
        moveTo(w * 0.1f, h)
        cubicTo(w * 0.2f, h * 0.6f, w * 0.5f, h * 0.4f, w * 0.9f, h * 0.5f)
        cubicTo(w * 0.8f, h * 0.8f, w * 0.6f, h * 0.95f, w * 0.1f, h)
        close()
      }
      drawPath(
        path = leafGlowPath,
        brush = Brush.radialGradient(
          colors = listOf(KisanEmerald.copy(alpha = 0.22f), Color.Transparent),
          center = Offset(w * 0.5f, h * 0.7f),
          radius = w * 0.8f
        ),
        style = Fill
      )
    }

    // Centered Brand Identity
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier.scale(pulseScale),
        contentAlignment = Alignment.Center
      ) {
        KisanLogoHeader(
          iconSize = 88.dp,
          titleFontSize = 38,
          isDarkTheme = true,
          showTagline = true,
          orientation = LogoOrientation.VERTICAL
        )
      }

      Spacer(modifier = Modifier.height(48.dp))

      Button(
        onClick = onGetStarted,
        colors = ButtonDefaults.buttonColors(
          containerColor = KisanEmerald,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .height(52.dp)
          .testTag("splash_continue_button")
      ) {
        Text(
          text = "Explore Farm →",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Bottom Sub-motto from the Design
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Better Insights. Healthier Crops.",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        color = KisanHarvestGold.copy(alpha = 0.9f)
      )
      Text(
        text = "Stronger Farmers.",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        color = Color.White.copy(alpha = 0.95f)
      )
    }
  }
}
