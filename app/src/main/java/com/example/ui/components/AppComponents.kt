package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Dimens
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEarthRed
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanGoldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanRedLight
import com.example.ui.theme.KisanSageLight
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

/**
 * Reusable Primary Button with built-in loading indicator, minimum 48dp touch target, and test tag.
 */
@Composable
fun AppButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  icon: ImageVector? = null,
  containerColor: Color = KisanEmerald,
  contentColor: Color = KisanWhite,
  testTag: String = "app_button"
) {
  Button(
    onClick = onClick,
    enabled = enabled && !isLoading,
    shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
    colors = ButtonDefaults.buttonColors(
      containerColor = containerColor,
      contentColor = contentColor,
      disabledContainerColor = KisanMutedSage.copy(alpha = 0.3f),
      disabledContentColor = KisanWhite.copy(alpha = 0.7f)
    ),
    modifier = modifier
      .fillMaxWidth()
      .height(Dimens.ButtonHeight)
      .testTag(testTag)
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = contentColor,
        strokeWidth = 2.5.dp
      )
      Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
      Text(
        text = "Please wait...",
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
      )
    } else {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimens.IconSizeMedium)
          )
          Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
        }
        Text(
          text = text,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Reusable Outlined Button for secondary actions.
 */
@Composable
fun AppOutlinedButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: ImageVector? = null,
  borderColor: Color = KisanEmerald,
  contentColor: Color = KisanEmerald,
  testTag: String = "app_outlined_button"
) {
  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
    border = BorderStroke(1.5.dp, borderColor),
    modifier = modifier
      .fillMaxWidth()
      .height(Dimens.ButtonHeight)
      .testTag(testTag)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = contentColor,
          modifier = Modifier.size(Dimens.IconSizeMedium)
        )
        Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
      }
      Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = contentColor
      )
    }
  }
}

/**
 * Reusable M3 Outlined Text Field with error state and leading/trailing icons.
 */
@Composable
fun AppTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  placeholder: String = "",
  leadingIcon: ImageVector? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  errorMessage: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = true,
  maxLines: Int = 1,
  enabled: Boolean = true,
  testTag: String = "app_text_field"
) {
  Column(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text(label) },
      placeholder = if (placeholder.isNotBlank()) {
        { Text(placeholder, color = KisanMutedSage.copy(alpha = 0.6f)) }
      } else null,
      leadingIcon = if (leadingIcon != null) {
        {
          Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = if (isError) KisanEarthRed else KisanEmerald
          )
        }
      } else null,
      trailingIcon = trailingIcon,
      isError = isError,
      enabled = enabled,
      singleLine = singleLine,
      maxLines = maxLines,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = KisanEmerald,
        unfocusedBorderColor = KisanCardBorder,
        errorBorderColor = KisanEarthRed,
        focusedContainerColor = KisanWhite,
        unfocusedContainerColor = KisanWhite,
        focusedLabelColor = KisanEmerald,
        unfocusedLabelColor = KisanMutedSage
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag(testTag)
    )

    if (isError && !errorMessage.isNullOrBlank()) {
      Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = Dimens.SpacingSmall)
      ) {
        Icon(
          imageVector = Icons.Default.ErrorOutline,
          contentDescription = null,
          tint = KisanEarthRed,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = errorMessage,
          fontSize = 12.sp,
          color = KisanEarthRed
        )
      }
    }
  }
}

/**
 * Reusable Password Field with built-in show/hide visibility toggle.
 */
@Composable
fun AppPasswordField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String = "Password",
  modifier: Modifier = Modifier,
  isError: Boolean = false,
  errorMessage: String? = null,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  testTag: String = "app_password_field"
) {
  var passwordVisible by remember { mutableStateOf(false) }

  AppTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    modifier = modifier,
    isError = isError,
    errorMessage = errorMessage,
    trailingIcon = {
      IconButton(onClick = { passwordVisible = !passwordVisible }) {
        Icon(
          imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
          contentDescription = if (passwordVisible) "Hide password" else "Show password",
          tint = KisanMutedSage
        )
      }
    },
    keyboardOptions = KeyboardOptions(autoCorrect = false),
    keyboardActions = keyboardActions,
    singleLine = true,
    testTag = testTag
  )
}

/**
 * Reusable Card container with consistent borders and rounded shapes.
 */
@Composable
fun AppCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  containerColor: Color = KisanWhite,
  borderColor: Color = KisanCardBorder,
  content: @Composable () -> Unit
) {
  val baseModifier = if (onClick != null) {
    modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  } else {
    modifier.fillMaxWidth()
  }

  Card(
    shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(Dimens.CardBorderWidth, borderColor),
    modifier = baseModifier
  ) {
    content()
  }
}

/**
 * Standard Loading State View.
 */
@Composable
fun LoadingView(
  message: String = "Loading...",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.SpacingLarge),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
      CircularProgressIndicator(
        color = KisanEmerald,
        strokeWidth = 3.dp,
        modifier = Modifier.size(36.dp)
      )
      Text(
        text = message,
        fontSize = 14.sp,
        color = KisanMutedSage,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

/**
 * Standard Error View with Retry Action.
 */
@Composable
fun ErrorView(
  title: String = "Something went wrong",
  message: String,
  onRetry: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
    colors = CardDefaults.cardColors(containerColor = KisanRedLight),
    border = BorderStroke(1.dp, KisanEarthRed.copy(alpha = 0.3f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.SpacingNormal)
  ) {
    Column(
      modifier = Modifier.padding(Dimens.SpacingNormal),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
    ) {
      Icon(
        imageVector = Icons.Default.ErrorOutline,
        contentDescription = null,
        tint = KisanEarthRed,
        modifier = Modifier.size(32.dp)
      )
      Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )
      Text(
        text = message,
        fontSize = 13.sp,
        color = KisanMutedSage,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )
      if (onRetry != null) {
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        Button(
          onClick = onRetry,
          shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
          colors = ButtonDefaults.buttonColors(containerColor = KisanEarthRed)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
          Text("Try Again", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

/**
 * Standard Empty State View with informative graphic/icon and primary action.
 */
@Composable
fun EmptyStateView(
  icon: ImageVector,
  title: String,
  description: String,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.SpacingLarge),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
      modifier = Modifier.padding(Dimens.SpacingNormal)
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(KisanEmeraldLight),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(32.dp)
        )
      }

      Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

      Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal,
        textAlign = TextAlign.Center
      )

      Text(
        text = description,
        fontSize = 13.sp,
        color = KisanMutedSage,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )

      if (actionText != null && onActionClick != null) {
        Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
        AppButton(
          text = actionText,
          onClick = onActionClick,
          modifier = Modifier.width(220.dp)
        )
      }
    }
  }
}

/**
 * Reusable Confirmation Dialog for delete, reset, or logout flows.
 */
@Composable
fun ConfirmDialog(
  title: String,
  message: String,
  confirmText: String = "Confirm",
  cancelText: String = "Cancel",
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
  isDestructive: Boolean = false
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
    containerColor = KisanWhite,
    title = {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = KisanCharcoal
      )
    },
    text = {
      Text(
        text = message,
        fontSize = 14.sp,
        color = KisanMutedSage,
        lineHeight = 20.sp
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDestructive) KisanEarthRed else KisanEmerald
        )
      ) {
        Text(confirmText, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(cancelText, color = KisanMutedSage)
      }
    }
  )
}

/**
 * Section Header with title and optional trailing action.
 */
@Composable
fun SectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    if (actionText != null && onActionClick != null) {
      Text(
        text = actionText,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = KisanEmerald,
        modifier = Modifier
          .clickable(onClick = onActionClick)
          .padding(4.dp)
      )
    }
  }
}

/**
 * Status Badge for disease severity, health index, or crop condition.
 */
enum class BadgeTone {
  SUCCESS, WARNING, DANGER, INFO, NEUTRAL
}

@Composable
fun StatusBadge(
  text: String,
  tone: BadgeTone = BadgeTone.INFO,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (tone) {
    BadgeTone.SUCCESS -> KisanEmeraldLight to KisanEmerald
    BadgeTone.WARNING -> KisanGoldLight to KisanHarvestGold
    BadgeTone.DANGER -> KisanRedLight to KisanEarthRed
    BadgeTone.INFO -> KisanEmeraldLight to KisanDeepForest
    BadgeTone.NEUTRAL -> KisanSageLight to KisanMutedSage
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Text(
      text = text,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = textColor,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}

/**
 * Reusable Search Bar.
 */
@Composable
fun SearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholder: String = "Search crops, diseases, treatments...",
  modifier: Modifier = Modifier,
  onClear: () -> Unit = { onQueryChange("") }
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    placeholder = {
      Text(placeholder, fontSize = 13.sp, color = KisanMutedSage)
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search",
        tint = KisanMutedSage,
        modifier = Modifier.size(20.dp)
      )
    },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = onClear) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
            tint = KisanMutedSage,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    },
    singleLine = true,
    shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = KisanEmerald,
      unfocusedBorderColor = KisanCardBorder,
      focusedContainerColor = KisanWhite,
      unfocusedContainerColor = KisanWhite
    ),
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag("search_bar")
  )
}

/**
 * Profile Avatar representation with farmer initials or placeholder.
 */
@Composable
fun ProfileAvatar(
  name: String,
  modifier: Modifier = Modifier,
  size: Dp = Dimens.AvatarSizeNormal
) {
  val initials = name.split(" ")
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercase() }
    .joinToString("")
    .ifBlank { "K" }

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(KisanEmeraldLight),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = initials,
      fontSize = (size.value * 0.4f).sp,
      fontWeight = FontWeight.Bold,
      color = KisanEmerald
    )
  }
}

/**
 * Offline Status Banner with icon and message.
 */
@Composable
fun OfflineBanner(
  message: String = "You're offline. Showing locally cached farm data and offline on-device AI scanner.",
  modifier: Modifier = Modifier
) {
  Surface(
    color = KisanHarvestGold.copy(alpha = 0.15f),
    shape = RoundedCornerShape(Dimens.CornerRadiusMedium),
    border = BorderStroke(1.dp, KisanHarvestGold.copy(alpha = 0.4f)),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(Dimens.SpacingSmall + 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
    ) {
      Icon(
        imageVector = Icons.Default.WifiOff,
        contentDescription = null,
        tint = KisanHarvestGold,
        modifier = Modifier.size(18.dp)
      )
      Text(
        text = message,
        fontSize = 12.sp,
        color = KisanCharcoal,
        lineHeight = 16.sp
      )
    }
  }
}
