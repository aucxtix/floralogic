package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@Composable
fun OnboardingScreen(
  onGetStarted: () -> Unit,
  onSignInClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activePage by remember { mutableIntStateOf(0) }

  val pages = listOf(
    Triple(
      "Smart farming starts with better insights.",
      "KisanAI uses AI and weather data to help you protect your crops, improve yields, and make informed decisions.",
      listOf(KisanEmerald, KisanHarvestGold)
    ),
    Triple(
      "Instant AI crop disease diagnosis.",
      "Snap a photo of any damaged leaf to identify blight, rust, and pests with precision treatments and dosage.",
      listOf(Color(0xFF2E7D32), KisanEmerald)
    ),
    Triple(
      "Precision weather & irrigation advisory.",
      "Save water and avoid foliar risk with real-time temperature, humidity, rainfall, and spray conditions.",
      listOf(KisanDeepForest, Color(0xFF00796B))
    )
  )

  val currentPage = pages[activePage]

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    // Top Illustration Box (Farm Landscape with Morning Sun)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1.15f)
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(28.dp))
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFFEBF7F0),
              Color(0xFFD6F0E0)
            )
          )
        )
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sun & Glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(KisanHarvestGold, KisanHarvestGold.copy(alpha = 0.2f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.35f),
            radius = w * 0.45f
          ),
          radius = w * 0.45f,
          center = Offset(w * 0.5f, h * 0.35f)
        )

        drawCircle(
          color = Color(0xFFFFD54F),
          radius = w * 0.12f,
          center = Offset(w * 0.5f, h * 0.35f)
        )

        // Rolling green hills
        val hillPath1 = Path().apply {
          moveTo(0f, h * 0.65f)
          cubicTo(w * 0.25f, h * 0.52f, w * 0.6f, h * 0.68f, w, h * 0.55f)
          lineTo(w, h)
          lineTo(0f, h)
          close()
        }
        drawPath(hillPath1, color = KisanEmerald.copy(alpha = 0.7f))

        val hillPath2 = Path().apply {
          moveTo(0f, h * 0.72f)
          cubicTo(w * 0.35f, h * 0.62f, w * 0.7f, h * 0.78f, w, h * 0.68f)
          lineTo(w, h)
          lineTo(0f, h)
          close()
        }
        drawPath(hillPath2, color = KisanDeepForest)

        // Neat perspective crop rows
        for (i in 0..10) {
          val startX = w * 0.5f + (i - 5) * 8f
          val endX = w * (i / 10f)
          drawLine(
            color = KisanEmeraldLightBorder(i),
            start = Offset(startX, h * 0.68f),
            end = Offset(endX, h),
            strokeWidth = 3f
          )
        }
      }

      // Skip button on top right
      TextButton(
        onClick = onGetStarted,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(12.dp)
      ) {
        Text(
          text = "Skip",
          color = KisanDeepForest,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
      }
    }

    // Bottom Content Panel
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(0.95f)
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = currentPage.first,
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal,
          textAlign = TextAlign.Center,
          lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = currentPage.second,
          fontSize = 14.sp,
          color = KisanMutedSage,
          textAlign = TextAlign.Center,
          lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3 Pagination Indicator Dots
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 0..2) {
            Box(
              modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(if (i == activePage) 22.dp else 8.dp, 8.dp)
                .clip(CircleShape)
                .background(if (i == activePage) KisanEmerald else Color(0xFFD0D7D2))
                .clickable { activePage = i }
            )
          }
        }
      }

      // Actions: Get Started & Sign In
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Button(
          onClick = {
            if (activePage < 2) {
              activePage++
            } else {
              onGetStarted()
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = KisanEmerald,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(28.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("onboarding_get_started_button")
        ) {
          Text(
            text = if (activePage == 2) "Get Started" else "Get Started",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
          onClick = onSignInClick,
          modifier = Modifier.testTag("onboarding_signin_link")
        ) {
          Text(
            text = "Have an account? Sign In",
            color = KisanDeepForest,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

private fun KisanEmeraldLightBorder(index: Int): Color {
  return if (index % 2 == 0) Color(0xFF66BB6A) else Color(0xFF81C784)
}
