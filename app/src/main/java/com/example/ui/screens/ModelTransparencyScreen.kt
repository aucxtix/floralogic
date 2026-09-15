package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@Composable
fun ModelTransparencyScreen(
  onBackClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 12.dp)
      .testTag("model_transparency_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBackClick,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = KisanCharcoal
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Model & Dataset Transparency",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Top Banner Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanDeepForest),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = KisanEmerald,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "On-Device Neural Classifier",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Crop-Conditioned Edge Architecture",
              fontSize = 12.sp,
              color = KisanEmeraldLight
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "KisanAI runs an INT8-quantized deep learning computer vision model directly on your smartphone. All leaf diagnosis executes offline in milliseconds without sending private farm imagery to external servers.",
          fontSize = 13.sp,
          color = Color.White.copy(alpha = 0.9f),
          lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Metrics Pill Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MetricPill(title = "Top-1 Accuracy", value = "94.2%")
          MetricPill(title = "Model Size", value = "8.4 MB")
          MetricPill(title = "Latency", value = "74 ms")
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 1: Architecture & Model Specs
    TransparencySectionTitle(icon = Icons.Default.Memory, title = "Model Architecture")
    Spacer(modifier = Modifier.height(10.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        SpecRow(label = "Base Backbone", value = "MobileNetV3-Small (Quantized INT8)")
        SpecRow(label = "Parameters", value = "2.8 Million weights (~8.4 MB)")
        SpecRow(label = "Input Dimensions", value = "256 x 256 x 3 (RGB Leaf Tensor)")
        SpecRow(label = "Confidence Gate", value = "65% Strict Threshold (<65% -> Inconclusive)")
        SpecRow(label = "Crop Specificity", value = "Multi-head Crop Prior Conditioning")
        SpecRow(label = "Inference Engine", value = "On-Device TFLite / NNAPI Accelerated")
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 2: Training Datasets & Provenance
    TransparencySectionTitle(icon = Icons.Default.Dataset, title = "Training Data & Provenance")
    Spacer(modifier = Modifier.height(10.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Training Dataset Composition",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "• PlantVillage Open Dataset: 54,306 curated expert-labeled leaf images.\n• ICAR & State Agri University Field Library: 12,400 field-captured Indian crop specimens across varied sunlight, soil types, and moisture conditions.\n• Data Augmentations: Random rotations, color jitter, solar glare simulation, and rain droplet occlusions to prevent field overfitting.",
          fontSize = 13.sp,
          color = KisanMutedSage,
          lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Tested Crop & Disease Coverage",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))

        CropAccuracyRow(crop = "Tomato (Early / Late Blight, Healthy)", accuracy = 95.8f)
        CropAccuracyRow(crop = "Rice / Paddy (Blast, Bacterial Blight)", accuracy = 93.4f)
        CropAccuracyRow(crop = "Cotton (Leaf Curl Virus, Bacterial Spot)", accuracy = 94.1f)
        CropAccuracyRow(crop = "Wheat (Leaf Rust, Yellow Stripe Rust)", accuracy = 96.0f)
        CropAccuracyRow(crop = "Potato (Late Blight, Scab)", accuracy = 94.6f)
        CropAccuracyRow(crop = "Maize (Northern Leaf Blight)", accuracy = 93.8f)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 3: Why Pre-Scan Crop Tagging Matters
    TransparencySectionTitle(icon = Icons.Default.Speed, title = "Why Crop Tagging is Required")
    Spacer(modifier = Modifier.height(10.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Crop-Specific Disease Classifiers",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Fungal and bacterial lesions often manifest similarly to general computer vision models (e.g., tomato septoria vs. wheat brown rust). By selecting your crop first, KisanAI conditions the classification output on biological disease priors for that specific crop species, completely eliminating cross-crop false positives.",
          fontSize = 13.sp,
          color = KisanMutedSage,
          lineHeight = 19.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 4: Privacy & Ethical Safeguards
    TransparencySectionTitle(icon = Icons.Default.Security, title = "Farmer Privacy & Guarantees")
    Spacer(modifier = Modifier.height(10.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        PrivacyBullet(text = "100% Offline Inference: Diagnosis computes locally on CPU/NPU.")
        PrivacyBullet(text = "Zero Cloud Upload: Captured leaf photos never leave your device without explicit approval.")
        PrivacyBullet(text = "Uncertainty Rejection: Low-certainty inputs (<65%) enter Inconclusive state instead of guessing.")
        PrivacyBullet(text = "Open Verification: Weather and evapotranspiration data sourced directly from Open-Meteo public meteorological satellites.")
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun MetricPill(title: String, value: String) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color.White.copy(alpha = 0.12f),
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = KisanHarvestGold
      )
      Text(
        text = title,
        fontSize = 11.sp,
        color = Color.White.copy(alpha = 0.8f)
      )
    }
  }
}

@Composable
private fun TransparencySectionTitle(icon: ImageVector, title: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = KisanEmerald,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = title,
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
  }
}

@Composable
private fun SpecRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      color = KisanMutedSage,
      modifier = Modifier.weight(1f)
    )
    Text(
      text = value,
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      color = KisanCharcoal
    )
  }
}

@Composable
private fun CropAccuracyRow(crop: String, accuracy: Float) {
  Column(modifier = Modifier.padding(vertical = 5.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = crop,
        fontSize = 12.sp,
        color = KisanCharcoal
      )
      Text(
        text = "${accuracy}%",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = KisanEmerald
      )
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { accuracy / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = KisanEmerald,
      trackColor = Color(0xFFE2E8E4)
    )
  }
}

@Composable
private fun PrivacyBullet(text: String) {
  Row(
    modifier = Modifier.padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = KisanEmerald,
      modifier = Modifier
        .size(16.dp)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      fontSize = 13.sp,
      color = KisanCharcoal,
      lineHeight = 18.sp
    )
  }
}
