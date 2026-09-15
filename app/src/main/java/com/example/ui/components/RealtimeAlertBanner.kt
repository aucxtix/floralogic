package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.theme.*
import com.example.ui.RealtimeFarmAlert

@Composable
fun RealtimeAlertBanner(
  alert: RealtimeFarmAlert?,
  onActionClick: (DrawerDestination) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = alert != null,
    enter = fadeIn() + expandVertically(),
    exit = fadeOut() + shrinkVertically(),
    modifier = modifier
  ) {
    if (alert != null) {
      val isCritical = alert.severity == "CRITICAL"
      val isHigh = alert.severity == "HIGH"
      
      val bgColor = when {
        isCritical -> Color(0xFFFEF2F2)
        isHigh -> Color(0xFFFFFBEB)
        else -> Color(0xFFF0FDF4)
      }
      val borderColor = when {
        isCritical -> Color(0xFFEF4444)
        isHigh -> Color(0xFFF59E0B)
        else -> KisanEmerald
      }
      val iconTint = when {
        isCritical -> Color(0xFFDC2626)
        isHigh -> Color(0xFFD97706)
        else -> KisanEmerald
      }
      val icon = when (alert.category) {
        "IRRIGATION" -> Icons.Default.WaterDrop
        "MARKET" -> Icons.Default.TrendingUp
        "DISEASE" -> Icons.Default.Warning
        else -> Icons.Default.NotificationsActive
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
          .testTag("realtime_alert_banner")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(borderColor.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = borderColor,
                  modifier = Modifier.padding(end = 6.dp)
                ) {
                  Text(
                    text = "REAL-TIME ALERT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
                Text(
                  text = alert.category,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = iconTint
                )
              }
              Text(
                text = alert.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(28.dp)
                .testTag("dismiss_realtime_alert_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = alert.message,
            fontSize = 12.sp,
            color = Color(0xFF334155),
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 42.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 42.dp),
            horizontalArrangement = Arrangement.End
          ) {
            Button(
              onClick = {
                onActionClick(alert.destination)
                onDismiss()
              },
              colors = ButtonDefaults.buttonColors(containerColor = borderColor),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .height(32.dp)
                .testTag("action_realtime_alert_button")
            ) {
              Text(
                text = alert.actionText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}
