package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.theme.*

/**
 * Reusable Loading State UI.
 * Used across data loads and authentication checks.
 */
@Composable
fun LoadingStateView(
  message: String = "Loading...",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp)
      .testTag("loading_state_view"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator(
        color = KisanEmerald,
        strokeWidth = 4.dp,
        modifier = Modifier.size(48.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = message,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )
    }
  }
}

/**
 * Reusable Farmer-friendly Error State UI.
 * Never displays raw technical stack traces.
 */
@Composable
fun ErrorStateView(
  errorMessage: String,
  onRetry: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp)
      .testTag("error_state_view"),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(KisanHarvestGold.copy(alpha = 0.15f))
        ) {
          Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = KisanHarvestGold,
            modifier = Modifier.size(32.dp)
          )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Something went wrong",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = errorMessage,
          fontSize = 14.sp,
          color = KisanMutedSage,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )
        if (onRetry != null) {
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("error_retry_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again", fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

/**
 * Reusable Empty State UI.
 */
@Composable
fun EmptyStateView(
  title: String,
  description: String,
  actionText: String? = null,
  onAction: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp)
      .testTag("empty_state_view"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(KisanEmerald.copy(alpha = 0.1f))
      ) {
        Icon(
          imageVector = Icons.Default.Inbox,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(36.dp)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = description,
        fontSize = 14.sp,
        color = KisanMutedSage,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
      )
      if (actionText != null && onAction != null) {
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = onAction,
          colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("empty_state_action_button")
        ) {
          Text(actionText, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

/**
 * Reusable Success Notification Banner/Card.
 */
@Composable
fun SuccessNoticeView(
  title: String,
  message: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = KisanEmerald.copy(alpha = 0.12f),
    modifier = modifier.fillMaxWidth().testTag("success_notice_view")
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = KisanEmerald,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
        Text(text = message, fontSize = 13.sp, color = KisanCharcoal.copy(alpha = 0.8f))
      }
    }
  }
}
