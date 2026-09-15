package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CropDisease
import com.example.data.model.DiseaseSeverity
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

const val DEFAULT_CONFIDENCE_THRESHOLD = 0.70f

/**
 * ResultsScreen Composable
 * Displays the captured leaf image alongside a dedicated placeholder space
 * for the AI model's disease detection output.
 *
 * Supports:
 * 1. Image viewport displaying captured Bitmap, image Uri, or synthetic illustration
 * 2. Dedicated AI model output space:
 *    - Placeholder mode (awaiting AI inference) with structured skeleton slots
 *    - Analyzing mode (real-time inference indicator)
 *    - Detection Result mode (rich diagnosis, pathogen classification, confidence score, and remediation)
 *    - Inconclusive warning when model confidence falls below 70% threshold
 */
@Composable
fun ResultsScreen(
  capturedBitmap: Bitmap? = null,
  imageUri: String? = null,
  cropName: String = "Tomato",
  detectionResult: CropDisease? = null,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  isAnalyzing: Boolean = false,
  isSaved: Boolean = false,
  feedbackRating: Int? = null,
  onSubmitFeedback: (rating: Int, reason: String) -> Unit = { _, _ -> },
  onRunDetection: () -> Unit = {},
  onRetake: () -> Unit = {},
  onSaveResult: () -> Unit = {},
  onScanAnother: () -> Unit = {},
  onBackClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showDemoPlaceholderToggle by remember { mutableStateOf(false) }
  val activeResult = if (showDemoPlaceholderToggle) null else detectionResult

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 18.dp, vertical = 12.dp)
      .testTag("results_screen")
  ) {
    // 1. Top Bar
    ResultsTopBar(
      hasResult = activeResult != null,
      isAnalyzing = isAnalyzing,
      onBackClick = onBackClick,
      onTogglePlaceholderDemo = {
        showDemoPlaceholderToggle = !showDemoPlaceholderToggle
      }
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Captured Image Viewport
    CapturedImageViewport(
      bitmap = capturedBitmap,
      imageUri = imageUri,
      cropName = cropName,
      hasResult = activeResult != null,
      matchPercentage = activeResult?.let { (it.confidence * 100).toInt() },
      confidenceThreshold = confidenceThreshold,
      onRetake = onRetake
    )

    Spacer(modifier = Modifier.height(18.dp))

    // 3. AI Model Disease Detection Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "AI Disease Detection Output",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
          isAnalyzing -> KisanHarvestGold.copy(alpha = 0.2f)
          activeResult != null -> if (activeResult.confidence < confidenceThreshold) Color(0xFFFFF3CD) else KisanEmeraldLight
          else -> Color(0xFFE8ECE9)
        }
      ) {
        Text(
          text = when {
            isAnalyzing -> "Inferring..."
            activeResult != null -> if (activeResult.confidence < confidenceThreshold) "Inconclusive (<70%)" else "Model Output"
            else -> "Placeholder Space"
          },
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = when {
            isAnalyzing -> Color(0xFFB26A00)
            activeResult != null -> if (activeResult.confidence < confidenceThreshold) Color(0xFFB26A00) else KisanDeepForest
            else -> KisanMutedSage
          },
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4. Content of AI Output Space (Placeholder vs Analyzing vs Populated Result)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .animateContentSize()
    ) {
      when {
        isAnalyzing -> {
          AiModelInferenceCard()
        }

        activeResult != null -> {
          PopulatedDetectionOutputCard(
            disease = activeResult,
            isSaved = isSaved,
            confidenceThreshold = confidenceThreshold,
            feedbackRating = feedbackRating,
            onSubmitFeedback = onSubmitFeedback,
            onSaveResult = onSaveResult,
            onScanAnother = onScanAnother,
            onRetake = onRetake
          )
        }

        else -> {
          // Placeholder space for the AI model's disease detection output
          AiDetectionPlaceholderCard(
            cropName = cropName,
            onRunDetection = onRunDetection
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

/**
 * Top App Bar for ResultsScreen
 */
@Composable
private fun ResultsTopBar(
  hasResult: Boolean,
  isAnalyzing: Boolean,
  onBackClick: () -> Unit,
  onTogglePlaceholderDemo: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = onBackClick,
        modifier = Modifier
          .size(38.dp)
          .testTag("results_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = KisanCharcoal
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = "Scan & Analysis",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Text(
          text = if (hasResult) "AI Diagnosis Generated" else "Specimen Ready for Model",
          fontSize = 11.sp,
          color = KisanMutedSage
        )
      }
    }

    // Interactive toggle to preview placeholder vs output
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = KisanEmeraldLight,
      modifier = Modifier
        .clickable(onClick = onTogglePlaceholderDemo)
        .testTag("results_toggle_placeholder_demo")
    ) {
      Text(
        text = if (hasResult) "Show Placeholder" else "Show Output",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = KisanEmerald,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
      )
    }
  }
}

/**
 * Viewport displaying the captured image with metadata overlays and retake action
 */
@Composable
private fun CapturedImageViewport(
  bitmap: Bitmap?,
  imageUri: String?,
  cropName: String,
  hasResult: Boolean,
  matchPercentage: Int?,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  onRetake: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KisanWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, KisanCardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .height(220.dp)
      .testTag("results_image_viewport")
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      when {
        bitmap != null -> {
          Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Captured crop specimen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        imageUri != null -> {
          AsyncImage(
            model = imageUri,
            contentDescription = "Captured crop specimen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        else -> {
          // Synthetic high-fidelity leaf preview when no physical bitmap is loaded
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Gradient field background
            drawRect(
              brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2C5E2E), Color(0xFF1B3B1D))
              )
            )

            // Leaf contour
            val leafPath = Path().apply {
              moveTo(w * 0.16f, h * 0.84f)
              cubicTo(w * 0.12f, h * 0.32f, w * 0.42f, h * 0.1f, w * 0.84f, h * 0.22f)
              cubicTo(w * 0.88f, h * 0.72f, w * 0.58f, h * 0.90f, w * 0.16f, h * 0.84f)
              close()
            }
            drawPath(leafPath, color = Color(0xFF388E3C))

            // Leaf vein
            drawLine(
              color = Color(0xFF81C784),
              start = Offset(w * 0.16f, h * 0.84f),
              end = Offset(w * 0.84f, h * 0.22f),
              strokeWidth = 4.5f
            )

            // Lesion spot
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4E342E), Color(0xFFFBC02D).copy(alpha = 0.6f), Color.Transparent),
                center = Offset(w * 0.52f, h * 0.46f),
                radius = 64f
              ),
              radius = 64f,
              center = Offset(w * 0.52f, h * 0.46f)
            )
          }
        }
      }

      // Top-Left: Camera Capture Tag
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Captured Leaf",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Top-Right: Crop or Match Tag
      if (hasResult && matchPercentage != null) {
        val isInconclusive = matchPercentage < (confidenceThreshold * 100).toInt()
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isInconclusive) KisanHarvestGold else KisanEmerald,
          shadowElevation = 3.dp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
            .testTag(if (isInconclusive) "captured_image_inconclusive_tag" else "captured_image_match_tag")
        ) {
          Text(
            text = if (isInconclusive) "$matchPercentage% • Inconclusive" else "$matchPercentage% Match",
            color = if (isInconclusive) KisanCharcoal else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
      } else {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = KisanDeepForest.copy(alpha = 0.85f),
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
        ) {
          Text(
            text = "Crop: $cropName",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
      }

      // Bottom-Right: Retake Button
      Surface(
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.92f),
        shadowElevation = 3.dp,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp)
          .size(38.dp)
      ) {
        IconButton(
          onClick = onRetake,
          modifier = Modifier.testTag("results_retake_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Retake photo",
            tint = KisanDeepForest,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

/**
 * Placeholder space for the AI model's disease detection output.
 * Renders structured skeleton slots, an informative empty state,
 * and an action trigger to run inference.
 */
@Composable
private fun AiDetectionPlaceholderCard(
  cropName: String,
  onRunDetection: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KisanWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.5.dp, KisanEmerald.copy(alpha = 0.4f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_detection_placeholder_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Placeholder Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = KisanEmeraldLight,
          modifier = Modifier.size(44.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Biotech,
              contentDescription = null,
              tint = KisanEmerald,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "AI Model Output Space",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
          Text(
            text = "Awaiting model evaluation on $cropName leaf",
            fontSize = 12.sp,
            color = KisanMutedSage
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Structured Skeleton / Placeholder Slots
      PlaceholderSlotItem(
        icon = Icons.Default.Search,
        title = "Disease Classification Slot",
        subtitle = "Pathogen name, crop variety & severity classification will populate here."
      )

      Spacer(modifier = Modifier.height(10.dp))

      PlaceholderSlotItem(
        icon = Icons.Default.Shield,
        title = "Model Confidence Slot",
        subtitle = "Neural network probability score and match percentage indicator."
      )

      Spacer(modifier = Modifier.height(10.dp))

      PlaceholderSlotItem(
        icon = Icons.Default.Healing,
        title = "Treatment & Prescription Slot",
        subtitle = "Organic remedies, chemical fungicides, exact dosage and preventive advice."
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Primary Trigger CTA
      Button(
        onClick = onRunDetection,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("run_ai_detection_button")
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Run AI Disease Detection",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}

/**
 * Reusable slot in the placeholder space
 */
@Composable
private fun PlaceholderSlotItem(
  icon: ImageVector,
  title: String,
  subtitle: String
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = KisanWarmIvory.copy(alpha = 0.7f),
    border = BorderStroke(1.dp, Color(0xFFE2E7E2)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = KisanWhite,
        modifier = Modifier.size(32.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KisanMutedSage,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = KisanMutedSage,
          lineHeight = 15.sp
        )
      }
    }
  }
}

/**
 * Card shown when AI model inference is actively running
 */
@Composable
private fun AiModelInferenceCard() {
  val infiniteTransition = rememberInfiniteTransition(label = "inference_anim")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KisanWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, KisanCardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("ai_model_inference_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Surface(
        shape = CircleShape,
        color = KisanEmeraldLight,
        modifier = Modifier.size(56.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = KisanEmerald,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Running AI Disease Inference...",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Scanning leaf pathology, color spectrum, and chlorosis spots",
        fontSize = 12.sp,
        color = KisanMutedSage,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(18.dp))

      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = KisanEmerald,
        trackColor = Color(0xFFE2E8E4)
      )
    }
  }
}

/**
 * Full populated AI Model output displaying disease classification,
 * confidence score, symptoms, and treatment plan.
 */
@Composable
private fun PopulatedDetectionOutputCard(
  disease: CropDisease,
  isSaved: Boolean,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  feedbackRating: Int? = null,
  onSubmitFeedback: (rating: Int, reason: String) -> Unit = { _, _ -> },
  onSaveResult: () -> Unit,
  onScanAnother: () -> Unit,
  onRetake: () -> Unit = {}
) {
  var currentRating by remember(feedbackRating) { mutableStateOf(feedbackRating) }
  var showReasons by remember { mutableStateOf(false) }
  var feedbackSubmittedReason by remember { mutableStateOf<String?>(null) }
  val isInconclusive = disease.confidence < confidenceThreshold

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KisanWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, if (isInconclusive) KisanHarvestGold.copy(alpha = 0.8f) else KisanCardBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("populated_detection_output_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Inconclusive Confidence Warning Banner (if confidence < threshold)
      if (isInconclusive) {
        InconclusiveConfidenceWarningBanner(
          confidence = disease.confidence,
          confidenceThreshold = confidenceThreshold,
          onRetake = onRetake
        )
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Disease Title & Severity
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = if (disease.isHealthy) KisanEmeraldLight else Color(0xFFFDECEB),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (disease.isHealthy) Icons.Default.Eco else Icons.Default.Warning,
                contentDescription = null,
                tint = if (disease.isHealthy) KisanEmerald else Color(0xFFC94C4C),
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = disease.diseaseName.split("(")[0].trim(),
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Text(
              text = disease.scientificName,
              fontSize = 12.sp,
              color = KisanMutedSage
            )
          }
        }

        // Severity Tag
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = when (disease.severity) {
            DiseaseSeverity.NONE -> KisanEmeraldLight
            DiseaseSeverity.LOW -> Color(0xFFFFF8E1)
            DiseaseSeverity.MEDIUM -> Color(0xFFFFF3E0)
            DiseaseSeverity.HIGH -> Color(0xFFFFEBEE)
          }
        ) {
          Text(
            text = disease.severity.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = when (disease.severity) {
              DiseaseSeverity.NONE -> KisanEmerald
              DiseaseSeverity.LOW -> Color(0xFFB28900)
              DiseaseSeverity.MEDIUM -> Color(0xFFB26A00)
              DiseaseSeverity.HIGH -> Color(0xFFC62828)
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Confidence Score Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "AI Confidence Score",
            fontSize = 13.sp,
            color = KisanMutedSage
          )
          if (isInconclusive) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFFF3CD),
              border = BorderStroke(0.5.dp, KisanHarvestGold.copy(alpha = 0.6f))
            ) {
              Text(
                text = "Below ${(confidenceThreshold * 100).toInt()}% Threshold",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB28900),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
        Text(
          text = "${(disease.confidence * 100).toInt()}%",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isInconclusive) Color(0xFFB28900) else KisanCharcoal
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { disease.confidence },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (isInconclusive) KisanHarvestGold else KisanEmerald,
        trackColor = Color(0xFFE2E8E4)
      )

      if (isInconclusive) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "⚠️ Prediction confidence is below ${(confidenceThreshold * 100).toInt()}%. Please retake with better lighting and focus for higher diagnostic certainty.",
          fontSize = 11.sp,
          color = Color(0xFFB28900),
          fontWeight = FontWeight.Medium,
          lineHeight = 15.sp
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Symptoms List
      Text(
        text = "Identified Symptoms",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
      Spacer(modifier = Modifier.height(6.dp))
      disease.symptoms.forEach { symptom ->
        Row(
          modifier = Modifier.padding(vertical = 2.dp),
          verticalAlignment = Alignment.Top
        ) {
          Text(text = "• ", color = KisanEmerald, fontWeight = FontWeight.Bold)
          Text(
            text = symptom,
            fontSize = 13.sp,
            color = KisanCharcoal.copy(alpha = 0.85f),
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = Color(0xFFF0F0F0))
      Spacer(modifier = Modifier.height(16.dp))

      // Recommended Treatment Plan
      Text(
        text = "Recommended Treatment",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = KisanEmeraldLight.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "🌿 Organic Solution",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = KisanDeepForest
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = disease.organicTreatment,
            fontSize = 12.sp,
            color = KisanCharcoal
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "🧪 Chemical Fungicide & Dosage",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = KisanDeepForest
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${disease.chemicalTreatment} (${disease.dosage})",
            fontSize = 12.sp,
            color = KisanCharcoal
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // ML Continuous Learning Feedback Loop
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = KisanWarmIvory,
        border = BorderStroke(1.dp, Color(0xFFE2E8E4)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("model_feedback_section")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Was this diagnosis accurate?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = KisanCharcoal
              )
              Text(
                text = "Your feedback improves our on-device vision model.",
                fontSize = 11.sp,
                color = KisanMutedSage
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              // Thumbs Up
              Surface(
                shape = CircleShape,
                color = if (currentRating == 1) KisanEmerald else Color.White,
                border = BorderStroke(1.dp, if (currentRating == 1) KisanEmerald else Color(0xFFCFD8DC)),
                modifier = Modifier
                  .size(36.dp)
                  .clickable {
                    currentRating = 1
                    showReasons = false
                    feedbackSubmittedReason = "Accurate diagnosis"
                    onSubmitFeedback(1, "Accurate diagnosis")
                  }
                  .testTag("feedback_thumbs_up_button")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Accurate diagnosis",
                    tint = if (currentRating == 1) Color.White else KisanDeepForest,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              // Thumbs Down
              Surface(
                shape = CircleShape,
                color = if (currentRating == -1) Color(0xFFC62828) else Color.White,
                border = BorderStroke(1.dp, if (currentRating == -1) Color(0xFFC62828) else Color(0xFFCFD8DC)),
                modifier = Modifier
                  .size(36.dp)
                  .clickable {
                    currentRating = -1
                    showReasons = true
                  }
                  .testTag("feedback_thumbs_down_button")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = "Inaccurate diagnosis",
                    tint = if (currentRating == -1) Color.White else Color(0xFFB71C1C),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          if (currentRating == 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = KisanEmerald,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Thank you! Validated diagnosis recorded for model benchmarking.",
                fontSize = 11.sp,
                color = KisanEmerald,
                fontWeight = FontWeight.Medium
              )
            }
          }

          if (showReasons && currentRating == -1) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "What was incorrect?",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = KisanCharcoal
            )
            Spacer(modifier = Modifier.height(6.dp))
            val reasonsList = listOf(
              "Wrong Disease",
              "Wrong Crop",
              "Symptoms Differ",
              "Treatment Unclear"
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              reasonsList.forEach { reason ->
                val isSelected = feedbackSubmittedReason == reason
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) Color(0xFFFFEBEE) else Color.White,
                  border = BorderStroke(1.dp, if (isSelected) Color(0xFFC62828) else Color(0xFFCFD8DC)),
                  modifier = Modifier
                    .clickable {
                      feedbackSubmittedReason = reason
                      onSubmitFeedback(-1, reason)
                    }
                    .testTag("feedback_reason_${reason.replace(" ", "_")}")
                ) {
                  Text(
                    text = reason,
                    fontSize = 10.sp,
                    color = if (isSelected) Color(0xFFC62828) else KisanCharcoal,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                  )
                }
              }
            }
            if (feedbackSubmittedReason != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Feedback recorded ($feedbackSubmittedReason). Thank you for improving KisanAI!",
                fontSize = 11.sp,
                color = Color(0xFFC62828)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Actions: Save and Scan Another
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onSaveResult,
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, if (isSaved) Color(0xFF81C784) else KisanEmerald),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSaved) KisanEmeraldLight else Color.Transparent
          ),
          modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .testTag("results_save_button")
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Default.Check else Icons.Default.BookmarkBorder,
            contentDescription = null,
            tint = KisanEmerald,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isSaved) "Saved" else "Save Result",
            color = KisanEmerald,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Button(
          onClick = onScanAnother,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
          modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .testTag("results_scan_another_button")
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Scan Another",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

/**
 * Inconclusive Confidence Warning Banner
 * Displayed when the AI model's prediction confidence is below the reliability threshold (e.g. 70%).
 * Prompts the farmer to retake a clearer, better-lit photo and provides actionable photography tips.
 */
@Composable
private fun InconclusiveConfidenceWarningBanner(
  confidence: Float,
  confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
  onRetake: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
    border = BorderStroke(1.5.dp, KisanHarvestGold),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("inconclusive_confidence_warning")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Warning Icon + Title + Confidence Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = KisanHarvestGold,
          modifier = Modifier.size(38.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Inconclusive Warning",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Inconclusive Prediction",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF5D4037)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFFE082)
            ) {
              Text(
                text = "${(confidence * 100).toInt()}% < ${(confidenceThreshold * 100).toInt()}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Model confidence is below the verified 70% threshold",
            fontSize = 11.sp,
            color = Color(0xFF795548)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // User prompt message for clearer, better-lit photo
      Text(
        text = "The leaf image does not exhibit distinct enough disease symptoms or visual clarity for a reliable diagnosis. Please try taking a clearer, better-lit photo of the affected leaf.",
        fontSize = 12.sp,
        color = KisanCharcoal,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Actionable photography tips
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3CD),
        border = BorderStroke(1.dp, Color(0xFFFFE082)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "Tips for a Clearer, Better-Lit Photo:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "☀️ ", fontSize = 12.sp)
            Text(
              text = "Bright natural lighting: Photograph under diffused daylight; avoid heavy shadows or flash glare reflections.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "🔍 ", fontSize = 12.sp)
            Text(
              text = "Sharp macro focus: Hold the phone 15–20 cm away and tap the leaf on-screen to ensure sharp focus on lesion spots.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "🍃 ", fontSize = 12.sp)
            Text(
              text = "Flat leaf framing: Hold or support the leaf flat so it covers the central reticle, minimizing distracting background weeds or soil.",
              fontSize = 11.sp,
              color = Color(0xFF5D4037),
              lineHeight = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Primary Action to Retake Photo
      Button(
        onClick = onRetake,
        colors = ButtonDefaults.buttonColors(
          containerColor = KisanHarvestGold,
          contentColor = KisanCharcoal
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("inconclusive_retake_button")
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = KisanCharcoal,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Try Taking Clearer, Better-Lit Photo",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
      }
    }
  }
}
