package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FarmerProfile
import com.example.ui.components.KisanLogoHeader
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.ui.auth.BiometricHelper
import android.widget.Toast
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite
import kotlinx.coroutines.delay

@Composable
fun BiometricApprovalScreen(
  farmerProfile: FarmerProfile = FarmerProfile(),
  onApprovalSuccess: () -> Unit,
  onUsePassword: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isAuthenticating by remember { mutableStateOf(false) }
  var isApproved by remember { mutableStateOf(false) }
  
  val context = LocalContext.current
  val activity = context as? FragmentActivity

  fun triggerBiometric() {
    if (activity == null) return
    if (BiometricHelper.isBiometricAvailable(activity)) {
      BiometricHelper.showBiometricPrompt(
        activity = activity,
        onSuccess = {
          isApproved = true
        },
        onError = { err ->
          Toast.makeText(activity, err, Toast.LENGTH_SHORT).show()
        }
      )
    } else {
      Toast.makeText(activity, "Biometric authentication not available on this device", Toast.LENGTH_SHORT).show()
    }
  }

  // Animated pulse for biometric scanner ring
  val infiniteTransition = rememberInfiniteTransition(label = "biometric_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ring_pulse"
  )

  LaunchedEffect(isApproved) {
    if (isApproved) {
      delay(700)
      onApprovalSuccess()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 20.dp)
      .testTag("biometric_approval_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Navigation & Logo
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onUsePassword,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to login",
            tint = KisanCharcoal
          )
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = KisanEmeraldLight,
          modifier = Modifier.padding(end = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = KisanEmerald,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Biometric Lock",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanEmerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      KisanLogoHeader(
        iconSize = 44.dp,
        titleFontSize = 26,
        isDarkTheme = false
      )
    }

    // Center Biometric Interactive Card
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("biometric_approval_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Farmer Avatar & Name
        Surface(
          shape = CircleShape,
          color = KisanHarvestGold.copy(alpha = 0.2f),
          modifier = Modifier.size(54.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "👨‍🌾",
              fontSize = 26.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Welcome, ${farmerProfile.name}",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Text(
          text = "${farmerProfile.farmName} • ${farmerProfile.village}, ${farmerProfile.state}",
          fontSize = 12.sp,
          color = KisanMutedSage
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Biometric Sensor Animation & Touch Target
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(140.dp)
            .clickable {
              if (!isApproved) {
                triggerBiometric()
              }
            }
            .testTag("biometric_sensor_button")
        ) {
          // Outer pulsing ring
          Box(
            modifier = Modifier
              .size(130.dp)
              .scale(if (isApproved) 1f else pulseScale)
              .clip(CircleShape)
              .background(
                if (isApproved) KisanEmerald.copy(alpha = 0.2f)
                else KisanEmeraldLight.copy(alpha = 0.8f)
              )
              .border(
                width = 2.dp,
                color = if (isApproved) KisanEmerald else KisanEmerald.copy(alpha = 0.4f),
                shape = CircleShape
              )
          )

          // Inner sensor circle
          Surface(
            shape = CircleShape,
            color = if (isApproved) KisanEmerald else KisanDeepForest,
            shadowElevation = 6.dp,
            modifier = Modifier.size(86.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              if (isApproved) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Approved",
                  tint = Color.White,
                  modifier = Modifier.size(46.dp)
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = "Touch fingerprint sensor",
                  tint = Color.White,
                  modifier = Modifier.size(46.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = if (isApproved) "Identity Verified!" else "Biometric Approval",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = if (isApproved) KisanEmerald else KisanCharcoal,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (isApproved)
            "Unlocking your farm records..."
          else
            "Touch the fingerprint sensor or tap the icon to approve farm access.",
          fontSize = 13.sp,
          color = KisanMutedSage,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            if (!isApproved) {
              triggerBiometric()
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isApproved) KisanEmerald else KisanDeepForest,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("biometric_approve_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isApproved) "Approved • Entering..." else "Approve & Open Farm",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Bottom Alternative Actions
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      TextButton(
        onClick = onUsePassword,
        modifier = Modifier.testTag("biometric_use_password_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = KisanEmerald,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Use Password Instead",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = KisanEmerald
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "🔒 Encrypted with Android Keystore",
        fontSize = 11.sp,
        color = KisanMutedSage
      )
    }
  }
}
