package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KisanViewModel
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

/**
 * Data representation for crop selection items.
 */
data class CropTypeItem(
  val id: String,
  val name: String,
  val vernacularName: String,
  val botanicalName: String,
  val iconEmoji: String,
  val category: String,
  val primaryDiseases: List<String>,
  val modelAccuracy: String
)

val SUPPORTED_CROPS = listOf(
  CropTypeItem(
    id = "Wheat",
    name = "Wheat",
    vernacularName = "Gehun (गेहूं / ઘઉં)",
    botanicalName = "Triticum aestivum",
    iconEmoji = "🌾",
    category = "Cereals & Grains",
    primaryDiseases = listOf("Leaf Rust", "Powdery Mildew", "Bacterial Blight"),
    modelAccuracy = "96.2% Confidence"
  ),
  CropTypeItem(
    id = "Tomato",
    name = "Tomato",
    vernacularName = "Tamatar (टमाटर / ટામેટા)",
    botanicalName = "Solanum lycopersicum",
    iconEmoji = "🍅",
    category = "Vegetables",
    primaryDiseases = listOf("Early Blight", "Late Blight", "Leaf Mold", "Septoria"),
    modelAccuracy = "95.8% Confidence"
  ),
  CropTypeItem(
    id = "Paddy",
    name = "Paddy (Rice)",
    vernacularName = "Dhan (धान / ડાંગર)",
    botanicalName = "Oryza sativa",
    iconEmoji = "🌱",
    category = "Cereals & Grains",
    primaryDiseases = listOf("Bacterial Blight", "Leaf Blast", "Brown Spot"),
    modelAccuracy = "94.5% Confidence"
  ),
  CropTypeItem(
    id = "Potato",
    name = "Potato",
    vernacularName = "Aaloo (आलू / બટાકા)",
    botanicalName = "Solanum tuberosum",
    iconEmoji = "🥔",
    category = "Vegetables & Tubers",
    primaryDiseases = listOf("Early Blight", "Late Blight"),
    modelAccuracy = "94.8% Confidence"
  ),
  CropTypeItem(
    id = "Cotton",
    name = "Cotton",
    vernacularName = "Kapas (कपास / કપાસ)",
    botanicalName = "Gossypium hirsutum",
    iconEmoji = "☁️",
    category = "Cash Crops",
    primaryDiseases = listOf("Bacterial Blight", "Leaf Curl Virus"),
    modelAccuracy = "93.9% Confidence"
  ),
  CropTypeItem(
    id = "Maize",
    name = "Maize (Corn)",
    vernacularName = "Makka (मक्का / મકાઈ)",
    botanicalName = "Zea mays",
    iconEmoji = "🌽",
    category = "Cereals & Grains",
    primaryDiseases = listOf("Common Rust", "Northern Leaf Blight"),
    modelAccuracy = "95.1% Confidence"
  )
)

/**
 * ViewModel-backed overload of CropSelectionScreen.
 * Reads and updates the selected crop context in KisanViewModel to inform the detection model.
 */
@Composable
fun CropSelectionScreen(
  viewModel: KisanViewModel,
  onProceedToScan: (String) -> Unit,
  onNavigateBack: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val selectedCrop by viewModel.selectedCropContext.collectAsState()

  CropSelectionScreen(
    selectedCrop = selectedCrop,
    onCropSelected = { cropName ->
      viewModel.setSelectedCropContext(cropName)
    },
    onProceedToScan = {
      onProceedToScan(selectedCrop)
    },
    onNavigateBack = onNavigateBack,
    modifier = modifier
  )
}

/**
 * Stateless implementation of CropSelectionScreen.
 * Allows farmers to choose their crop type before scanning, setting species priors
 * to improve AI model classification accuracy.
 */
@Composable
fun CropSelectionScreen(
  selectedCrop: String,
  onCropSelected: (String) -> Unit,
  onProceedToScan: () -> Unit,
  onNavigateBack: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val currentCropItem = remember(selectedCrop) {
    SUPPORTED_CROPS.find { it.id.equals(selectedCrop, ignoreCase = true) || it.name.startsWith(selectedCrop, ignoreCase = true) }
      ?: SUPPORTED_CROPS.first()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .testTag("crop_selection_screen")
  ) {
    // Top Navigation Bar
    CropSelectionTopBar(onNavigateBack = onNavigateBack)

    // Main Content
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(6.dp))

        // Step Indicator: Step 1 of 2
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = KisanEmeraldLight,
            modifier = Modifier.padding(end = 4.dp)
          ) {
            Text(
              text = "STEP 1 OF 2",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = KisanEmerald,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Text(
            text = "Prior Context Setup",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = KisanMutedSage
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title and Subtitle
        Text(
          text = "Select Crop Type",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Selecting your crop beforehand provides biological priors to the detection model, eliminating cross-species false positives.",
          fontSize = 13.sp,
          color = KisanMutedSage,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Model Context Explanation Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = KisanWhite),
          border = BorderStroke(1.dp, KisanCardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(KisanEmeraldLight),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = KisanEmerald,
                modifier = Modifier.size(20.dp)
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Why select a crop first?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = KisanCharcoal
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Conditions the classifier's output probabilities to only focus on known pathogens for ${currentCropItem.name}, increasing detection confidence by up to 25%.",
                fontSize = 12.sp,
                color = KisanMutedSage,
                lineHeight = 16.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
      }

      // Crop Cards List
      items(SUPPORTED_CROPS, key = { it.id }) { cropItem ->
        val isSelected = cropItem.id.equals(selectedCrop, ignoreCase = true) ||
            cropItem.name.equals(selectedCrop, ignoreCase = true)

        CropCard(
          crop = cropItem,
          isSelected = isSelected,
          onClick = { onCropSelected(cropItem.id) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Bottom Sticky CTA
    Surface(
      color = KisanWhite,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Selected Context",
              fontSize = 11.sp,
              color = KisanMutedSage
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = currentCropItem.iconEmoji,
                fontSize = 16.sp
              )
              Text(
                text = currentCropItem.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = KisanEmeraldLight
          ) {
            Text(
              text = currentCropItem.modelAccuracy,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanEmerald,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = onProceedToScan,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = KisanEmerald,
            contentColor = KisanWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("proceed_to_scan_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Proceed to Scan ${currentCropItem.name}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CropCard(
  crop: CropTypeItem,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) KisanEmerald else KisanCardBorder,
    label = "borderColor"
  )
  val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) KisanWhite else KisanWhite,
    label = "bgColor"
  )

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("crop_card_${crop.id.lowercase()}")
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Crop Emoji Circle Avatar
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape)
          .background(if (isSelected) KisanEmeraldLight else KisanWarmIvory),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = crop.iconEmoji,
          fontSize = 24.sp
        )
      }

      // Crop Info
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = crop.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
          Text(
            text = "• ${crop.category}",
            fontSize = 11.sp,
            color = KisanMutedSage
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = crop.vernacularName,
          fontSize = 12.sp,
          color = KisanEmerald,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(1.dp))

        Text(
          text = crop.botanicalName,
          fontSize = 11.sp,
          fontStyle = FontStyle.Italic,
          color = KisanMutedSage
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Detectable diseases pills
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          crop.primaryDiseases.take(2).forEach { disease ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = KisanWarmIvory
            ) {
              Text(
                text = disease,
                fontSize = 10.sp,
                color = KisanCharcoal,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          if (crop.primaryDiseases.size > 2) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = KisanWarmIvory
            ) {
              Text(
                text = "+${crop.primaryDiseases.size - 2}",
                fontSize = 10.sp,
                color = KisanMutedSage,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // Radio Selection Indicator
      Icon(
        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
        contentDescription = if (isSelected) "Selected" else "Not selected",
        tint = if (isSelected) KisanEmerald else KisanMutedSage,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun CropSelectionTopBar(
  onNavigateBack: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = onNavigateBack,
      modifier = Modifier.testTag("crop_selection_back_button")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = KisanCharcoal
      )
    }

    Text(
      text = "KisanAI Crop Context",
      fontSize = 17.sp,
      fontWeight = FontWeight.SemiBold,
      color = KisanCharcoal,
      modifier = Modifier.padding(start = 4.dp)
    )
  }
}
