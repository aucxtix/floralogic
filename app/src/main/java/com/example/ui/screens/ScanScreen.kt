package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import android.content.Intent
import android.widget.Toast
import com.example.util.FileUploadValidator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.CropDiseaseDetector
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.CropDisease
import com.example.data.model.SampleSpecimen
import com.example.ui.ScanUiState
import com.example.ui.components.KisanLogoIcon
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
  strings: AppStrings,
  currentLanguage: AppLanguage,
  scanState: ScanUiState,
  onScanLeaf: (bitmap: Bitmap?, cropHint: String, specimenId: String?, imageUri: String?) -> Unit,
  onSaveScan: () -> Unit,
  onResetScan: () -> Unit,
  onNavigateBack: () -> Unit = onResetScan,
  onNavigateToTransparency: () -> Unit = {},
  onSubmitFeedback: (rating: Int, reason: String) -> Unit = { _, _ -> },
  initialCrop: String = "Wheat",
  onRequestLiveCamera: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val cropsList = listOf("Wheat", "Tomato", "Rice / Paddy", "Cotton", "Potato", "Maize")
  var selectedCrop by remember(initialCrop) { mutableStateOf(initialCrop) }
  var expandedCropDropdown by remember { mutableStateOf(false) }

  var showLiveCamera by remember { mutableStateOf(false) }
  var capturedPhotoForPreview by remember { mutableStateOf<Bitmap?>(null) }

  // Gallery Picker Launcher with strict security validation
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      when (val validation = FileUploadValidator.validateAndSanitizeImage(context, uri)) {
        is FileUploadValidator.ValidationResult.Success -> {
          capturedPhotoForPreview = validation.bitmap
          val safeUriString = Uri.fromFile(validation.sanitizedFile).toString()
          onScanLeaf(validation.bitmap, selectedCrop, null, safeUriString)
        }
        is FileUploadValidator.ValidationResult.Error -> {
          Toast.makeText(context, validation.message, Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  // Handle distinct state screens:
  when (scanState) {
    is ScanUiState.Analyzing -> {
      // 1. Analysis Loading Screen
      AnalysisLoadingView(cropHint = scanState.cropHint)
    }

    is ScanUiState.Inconclusive -> {
      // Model Safe Fallback / Inconclusive State
      InconclusiveResultView(
        cropHint = scanState.cropHint,
        confidence = scanState.confidence,
        reasons = scanState.reasons,
        suggestions = scanState.suggestions,
        capturedBitmap = scanState.capturedBitmap ?: capturedPhotoForPreview,
        imageUri = scanState.imageUri,
        onRetake = {
          capturedPhotoForPreview = null
          onResetScan()
          showLiveCamera = true
        },
        onTryDifferentCrop = {
          capturedPhotoForPreview = null
          onResetScan()
        },
        onBackClick = {
          capturedPhotoForPreview = null
          onResetScan()
        },
        modifier = modifier
      )
    }

    is ScanUiState.Success -> {
      // 2. Results Screen with captured image and populated AI detection output
      ResultsScreen(
        capturedBitmap = scanState.capturedBitmap ?: capturedPhotoForPreview,
        imageUri = scanState.imageUri,
        cropName = scanState.disease.cropName,
        detectionResult = scanState.disease,
        isSaved = scanState.isSaved,
        feedbackRating = scanState.feedbackRating,
        onSubmitFeedback = onSubmitFeedback,
        onRunDetection = {
          onScanLeaf(scanState.capturedBitmap ?: capturedPhotoForPreview, scanState.disease.cropName, null, scanState.imageUri)
        },
        onRetake = {
          capturedPhotoForPreview = null
          onResetScan()
          showLiveCamera = true
        },
        onSaveResult = onSaveScan,
        onScanAnother = {
          capturedPhotoForPreview = null
          onResetScan()
        },
        onBackClick = {
          capturedPhotoForPreview = null
          onResetScan()
        },
        modifier = modifier
      )
    }

    else -> {
      if (capturedPhotoForPreview != null) {
        // ResultsScreen displaying the captured photo alongside the AI placeholder space
        ResultsScreen(
          capturedBitmap = capturedPhotoForPreview,
          imageUri = null,
          cropName = selectedCrop,
          detectionResult = null,
          isSaved = false,
          onRunDetection = {
            onScanLeaf(capturedPhotoForPreview, selectedCrop, null, null)
          },
          onRetake = {
            capturedPhotoForPreview = null
            if (onRequestLiveCamera != null) {
              onRequestLiveCamera()
            } else {
              showLiveCamera = true
            }
          },
          onScanAnother = {
            capturedPhotoForPreview = null
            onResetScan()
          },
          onBackClick = {
            capturedPhotoForPreview = null
          },
          modifier = modifier
        )
      } else if (showLiveCamera) {
        CropScannerScreen(
          cropHint = selectedCrop,
          onImageCapturedWithCrop = { bitmap, crop ->
            selectedCrop = crop
            showLiveCamera = false
            capturedPhotoForPreview = bitmap
            onScanLeaf(bitmap, crop, null, null)
          },
          onImageCaptured = { bitmap ->
            showLiveCamera = false
            capturedPhotoForPreview = bitmap
            onScanLeaf(bitmap, selectedCrop, null, null)
          },
          onClose = { showLiveCamera = false }
        )
      } else {
        // Pre-Scan Selection Screen: Farmer selects crop type before launching camera
        PreScanCropSelectionScreen(
          selectedCrop = selectedCrop,
          onCropSelected = { crop ->
            selectedCrop = crop
          },
          onLaunchCamera = {
            if (onRequestLiveCamera != null) {
              onRequestLiveCamera()
            } else {
              showLiveCamera = true
            }
          },
          onGalleryClick = {
            galleryLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          onSelectSpecimen = { specimen ->
            onScanLeaf(null, specimen.cropName, specimen.id, null)
          },
          onBackClick = onNavigateBack,
          onNavigateToTransparency = onNavigateToTransparency,
          modifier = modifier
        )
      }
    }
  }
}

/**
 * Screen 1: The Stepper + Crop selector + Dashed capture box + Dual buttons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanCaptureScreen(
  selectedCrop: String,
  expandedCropDropdown: Boolean,
  onCropSelected: (String) -> Unit,
  onDropdownExpandChange: (Boolean) -> Unit,
  onCameraClick: () -> Unit,
  onGalleryClick: () -> Unit,
  onSelectSpecimen: (SampleSpecimen) -> Unit,
  onBackClick: () -> Unit,
  onNavigateToTransparency: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val cropsList = listOf("Tomato", "Rice / Paddy", "Cotton", "Wheat", "Potato", "Maize")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 12.dp)
  ) {
    // Header with back button
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
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "AI Crop Scan",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Model Specs & Transparency Banner
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = KisanEmeraldLight.copy(alpha = 0.6f)),
      border = BorderStroke(1.dp, KisanEmerald.copy(alpha = 0.25f)),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onNavigateToTransparency() }
        .testTag("model_transparency_banner")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Icon(
            imageVector = Icons.Default.Science,
            contentDescription = null,
            tint = KisanDeepForest,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Model Specs: MobileNetV3 • 94.2% Top-1",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = KisanDeepForest
            )
            Text(
              text = "PlantVillage + ICAR Dataset Provenance (Open-Source)",
              fontSize = 10.sp,
              color = KisanMutedSage
            )
          }
        }
        Text(
          text = "Specs ➔",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = KisanEmerald
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 4-Step Stepper: (1) Crop -> (2) Image -> (3) Analyse -> (4) Result
    ScanStepperRow(currentStep = 2)

    Spacer(modifier = Modifier.height(20.dp))

    // Step 1: Select Crop
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Step 1: Select Crop Species",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFE8F5E9)
      ) {
        Text(
          text = "Mandatory Prior",
          fontSize = 10.sp,
          color = KisanEmerald,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Neural weights are conditioned on plant species to eliminate cross-crop misclassification.",
      fontSize = 11.sp,
      color = KisanMutedSage,
      lineHeight = 15.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Quick Crop Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      cropsList.take(3).forEach { crop ->
        val isSelected = selectedCrop == crop
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) KisanEmerald else KisanWhite,
          border = BorderStroke(1.dp, if (isSelected) KisanEmerald else KisanCardBorder),
          modifier = Modifier
            .weight(1f)
            .clickable { onCropSelected(crop) }
            .testTag("crop_chip_${crop.replace(" ", "_")}")
        ) {
          Text(
            text = crop.split("/")[0].trim(),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else KisanCharcoal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
          )
        }
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      cropsList.drop(3).forEach { crop ->
        val isSelected = selectedCrop == crop
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) KisanEmerald else KisanWhite,
          border = BorderStroke(1.dp, if (isSelected) KisanEmerald else KisanCardBorder),
          modifier = Modifier
            .weight(1f)
            .clickable { onCropSelected(crop) }
            .testTag("crop_chip_${crop.replace(" ", "_")}")
        ) {
          Text(
            text = crop.split("/")[0].trim(),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else KisanCharcoal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    ExposedDropdownMenuBox(
      expanded = expandedCropDropdown,
      onExpandedChange = onDropdownExpandChange
    ) {
      OutlinedTextField(
        value = "Selected: $selectedCrop",
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = KisanCharcoal
          )
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = KisanWhite,
          unfocusedContainerColor = KisanWhite,
          focusedBorderColor = KisanEmerald,
          unfocusedBorderColor = KisanCardBorder
        ),
        modifier = Modifier
          .fillMaxWidth()
          .menuAnchor()
          .testTag("scan_crop_dropdown")
      )

      ExposedDropdownMenu(
        expanded = expandedCropDropdown,
        onDismissRequest = { onDropdownExpandChange(false) }
      ) {
        cropsList.forEach { crop ->
          DropdownMenuItem(
            text = { Text(crop, fontSize = 14.sp) },
            onClick = { onCropSelected(crop) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Dashed Rounded Capture Viewport
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(KisanWhite)
        .clickable { onCameraClick() }
        .testTag("scan_viewport_box")
    ) {
      // Dashed green border outline
      Canvas(modifier = Modifier.fillMaxSize()) {
        val stroke = Stroke(
          width = 4f,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 16f), 0f)
        )
        drawRoundRect(
          color = KisanEmerald.copy(alpha = 0.55f),
          size = size,
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
          style = stroke
        )
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Surface(
          shape = CircleShape,
          color = KisanEmeraldLight,
          modifier = Modifier.size(64.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Capture",
              tint = KisanEmerald,
              modifier = Modifier.size(32.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Tap to take a photo\nor upload from gallery",
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          color = KisanCharcoal,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Supported: JPG, PNG (Max 10MB)",
          fontSize = 11.sp,
          color = KisanMutedSage
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Dual Action Buttons: [ Gallery ] [ Camera ]
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      OutlinedButton(
        onClick = onGalleryClick,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, KisanEmerald),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("scan_gallery_button")
      ) {
        Icon(
          imageVector = Icons.Default.Collections,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Gallery",
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanEmerald
        )
      }

      Button(
        onClick = onCameraClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = KisanEmerald,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("scan_camera_button")
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Camera",
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Demo Specimens Quick Tray for Instant AI Diagnosis Testing
    Text(
      text = "Demo Crop Samples (Quick Test)",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(10.dp))

    CropDiseaseDetector.SAMPLE_SPECIMENS.forEach { specimen ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KisanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { onSelectSpecimen(specimen) }
          .testTag("specimen_${specimen.id}")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = if (specimen.diseaseName.contains("Healthy", ignoreCase = true)) {
                KisanEmeraldLight
              } else {
                Color(0xFFFDECEB)
              },
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = if (specimen.diseaseName.contains("Healthy", ignoreCase = true)) {
                    KisanEmerald
                  } else {
                    Color(0xFFC94C4C)
                  },
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = specimen.diseaseName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
              Text(
                text = "${specimen.cropName} • Tap to simulate",
                fontSize = 11.sp,
                color = KisanMutedSage
              )
            }
          }

          Text(
            text = "Test ➔",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = KisanEmerald
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

/**
 * 4-Step Horizontal Stepper: (1) Crop -> (2) Image -> (3) Analyse -> (4) Result
 */
@Composable
private fun ScanStepperRow(currentStep: Int) {
  val steps = listOf("Crop", "Image", "Analyse", "Result")

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    steps.forEachIndexed { index, stepName ->
      val stepNum = index + 1
      val isPast = stepNum < currentStep
      val isCurrent = stepNum == currentStep

      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = CircleShape,
          color = when {
            isPast -> KisanEmerald
            isCurrent -> KisanEmerald
            else -> Color(0xFFE2E8E4)
          },
          modifier = Modifier.size(28.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            if (isPast) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            } else {
              Text(
                text = "$stepNum",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) Color.White else KisanMutedSage
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = stepName,
          fontSize = 11.sp,
          fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
          color = if (isCurrent || isPast) KisanCharcoal else KisanMutedSage
        )
      }

      if (index < steps.size - 1) {
        Box(
          modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .padding(horizontal = 4.dp)
            .background(if (stepNum < currentStep) KisanEmerald else Color(0xFFE2E8E4))
        )
      }
    }
  }
}

/**
 * Screen 2: Analysis Loading Screen
 * Deep forest green background, pulsing circular badge with leaf icon, progress bar
 */
@Composable
fun AnalysisLoadingView(cropHint: String) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "loading_pulse"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(KisanDeepForest)
      .testTag("analysis_loading_view"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Glowing pulsing circular badge with leaf sprout icon
      Box(
        modifier = Modifier
          .scale(pulseScale)
          .size(110.dp)
          .clip(CircleShape)
          .background(KisanEmerald.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          shape = CircleShape,
          color = KisanDeepForest,
          border = BorderStroke(2.dp, KisanEmerald),
          modifier = Modifier.size(80.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            KisanLogoIcon(
              size = 46.dp,
              primaryColor = Color.White,
              secondaryColor = KisanEmerald,
              accentGoldColor = KisanHarvestGold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      Text(
        text = "Analysing crop image...",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Our AI model is checking for diseases and health issues.",
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(36.dp))

      // Linear Progress Bar
      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth(0.75f)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = KisanEmerald,
        trackColor = Color.White.copy(alpha = 0.2f)
      )

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "This may take a few seconds.",
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.6f)
      )
    }
  }
}

/**
 * Screen 3: Diagnosis Result Screen
 * Shows scanned leaf image, match badge ("96% Match"), confidence bar, recommendation, prevention, and dual buttons.
 */
@Composable
fun DiagnosisResultView(
  disease: CropDisease,
  capturedBitmap: Bitmap?,
  imageUri: String?,
  isSaved: Boolean,
  onSaveResult: () -> Unit,
  onScanAnother: () -> Unit,
  onBackClick: () -> Unit,
  onDemoToggle: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 12.dp)
  ) {
    // Header: Back Arrow, "Diagnosis Result", "Demo Result" pill button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
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
          text = "Diagnosis Result",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = KisanEmeraldLight,
        modifier = Modifier
          .clickable { onDemoToggle() }
          .testTag("diagnosis_demo_toggle")
      ) {
        Text(
          text = "Demo Result",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = KisanEmerald,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Leaf Preview Area with "96% Match" Pill Badge
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        if (capturedBitmap != null) {
          Image(
            bitmap = capturedBitmap.asImageBitmap(),
            contentDescription = "Scanned leaf",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else if (imageUri != null) {
          AsyncImage(
            model = imageUri,
            contentDescription = "Scanned leaf",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          // Handcrafted high-fidelity leaf illustration for the demo view
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Lush green background
            drawRect(
              brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2E6930), Color(0xFF1E4620))
              )
            )

            // Realistic leaf contour
            val leafPath = Path().apply {
              moveTo(w * 0.15f, h * 0.85f)
              cubicTo(w * 0.1f, h * 0.3f, w * 0.4f, h * 0.08f, w * 0.85f, h * 0.2f)
              cubicTo(w * 0.9f, h * 0.7f, w * 0.6f, h * 0.92f, w * 0.15f, h * 0.85f)
              close()
            }
            drawPath(leafPath, color = Color(0xFF43A047))

            // Central leaf vein
            drawLine(
              color = Color(0xFFA5D6A7),
              start = Offset(w * 0.15f, h * 0.85f),
              end = Offset(w * 0.85f, h * 0.2f),
              strokeWidth = 5f
            )

            // Chlorotic / necrotic blight spot for visual realism
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(Color(0xFF5D4037), Color(0xFFFBC02D).copy(alpha = 0.7f), Color.Transparent),
                center = Offset(w * 0.55f, h * 0.45f),
                radius = 70f
              ),
              radius = 70f,
              center = Offset(w * 0.55f, h * 0.45f)
            )
          }
        }

        // Bounding box / match pill tag on top right of image
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = KisanEmerald,
          shadowElevation = 4.dp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(14.dp)
        ) {
          Text(
            text = "${(disease.confidence * 100).toInt()}% Match",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Detailed Diagnosis Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Disease Title & Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = if (disease.isHealthy) KisanEmeraldLight else Color(0xFFFDECEB),
            modifier = Modifier.size(46.dp)
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

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = disease.diseaseName.split("(")[0].trim(),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = disease.cropName,
                fontSize = 13.sp,
                color = KisanMutedSage
              )
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (disease.modelSource.contains("Gemini")) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                border = BorderStroke(0.5.dp, if (disease.modelSource.contains("Gemini")) Color(0xFF93C5FD) else Color(0xFFCBD5E1))
              ) {
                Text(
                  text = disease.modelSource,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (disease.modelSource.contains("Gemini")) Color(0xFF1D4ED8) else KisanCharcoal,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confidence Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Confidence",
            fontSize = 13.sp,
            color = KisanMutedSage
          )
          Text(
            text = "${(disease.confidence * 100).toInt()}%",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { disease.confidence },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = KisanEmerald,
          trackColor = Color(0xFFE2E8E4)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Agronomic Diagnosis (What & Why)
        Text(
          text = "Observed Symptoms & Cause",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (disease.isHealthy) {
            "Lush leaf canopy with normal cell turgor and chlorophyll density."
          } else {
            "Symptoms: ${disease.symptoms.joinToString("; ")}"
          },
          fontSize = 13.sp,
          color = KisanCharcoal,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Organic / Biological Management
        if (!disease.isHealthy && disease.organicTreatment.isNotBlank()) {
          Text(
            text = "Organic & Biological Remedy",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = KisanEmerald
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = disease.organicTreatment,
            fontSize = 13.sp,
            color = KisanCharcoal,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(16.dp))
        }

        // Chemical Control & Dosage
        Text(
          text = if (disease.isHealthy) "Maintenance Care" else "Chemical Control & Dosage",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (disease.isHealthy) KisanCharcoal else Color(0xFFC62828)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (disease.isHealthy) {
            "Crop is in optimal health. Continue standard drip irrigation and scheduled organic nutrition."
          } else {
            "${disease.chemicalTreatment}\nRecommended Dosage: ${disease.dosage} (Est. Cost: ${disease.estimatedCostInr})"
          },
          fontSize = 13.sp,
          color = KisanCharcoal,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Spread & Progression Risk
        if (!disease.isHealthy) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFF8E1),
            border = BorderStroke(1.dp, Color(0xFFFFE082)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "Progression & Spread Risk:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF57F17)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Under prevailing humidity, secondary lesions may expand to adjacent rows within 3 to 5 days if preventive action is deferred.",
                fontSize = 11.sp,
                color = Color(0xFF5D4037),
                lineHeight = 16.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
        }

        // Prevention Bullet Points
        Text(
          text = "Long-Term Prevention",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (disease.preventiveMeasures.isNotEmpty()) {
          disease.preventiveMeasures.take(3).forEach { measure ->
            Text(
              text = "• $measure",
              fontSize = 13.sp,
              color = KisanMutedSage,
              lineHeight = 18.sp,
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }
        } else {
          Text(
            text = "• Maintain proper plant spacing\n• Avoid overhead leaf splash irrigation",
            fontSize = 13.sp,
            color = KisanMutedSage,
            lineHeight = 18.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Regulatory & Safety Disclaimer
        Text(
          text = "Note: Always adhere to Central Insecticides Board (CIBRC) approved label instructions and wear protective gear.",
          fontSize = 10.sp,
          color = KisanMutedSage,
          lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Scan Date: Verified On-Device Diagnosis",
          fontSize = 11.sp,
          color = KisanMutedSage
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Dual Buttons at Bottom: [ Save Result ] [ Scan Another ]
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      OutlinedButton(
        onClick = onSaveResult,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, KisanEmerald),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("diagnosis_save_button")
      ) {
        Icon(
          imageVector = if (isSaved) Icons.Default.BookmarkAdded else Icons.Default.BookmarkAdded,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isSaved) "Saved" else "Save Result",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanEmerald
        )
      }

      Button(
        onClick = onScanAnother,
        colors = ButtonDefaults.buttonColors(
          containerColor = KisanDeepForest,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("diagnosis_scan_another_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Scan Another",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

/**
 * Screen 4: Inconclusive / Low-Confidence Fallback Screen
 * Explicitly displays confidence threshold safety behavior when model confidence < 65%.
 * Provides reasons, actionable photo capture advice, and Government Kisan Call Centre helpline.
 */
@Composable
fun InconclusiveResultView(
  cropHint: String,
  confidence: Float,
  reasons: List<String>,
  suggestions: List<String>,
  capturedBitmap: Bitmap?,
  imageUri: String?,
  onRetake: () -> Unit,
  onTryDifferentCrop: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 12.dp)
      .testTag("inconclusive_result_view")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
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
          text = "Scan Inconclusive",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF3E0),
        border = BorderStroke(1.dp, Color(0xFFFFB74D))
      ) {
        Text(
          text = "Safety Mode",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFE65100),
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Leaf Image Preview with Confidence Badge
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        if (capturedBitmap != null) {
          Image(
            bitmap = capturedBitmap.asImageBitmap(),
            contentDescription = "Scanned leaf image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else if (imageUri != null) {
          AsyncImage(
            model = imageUri,
            contentDescription = "Scanned leaf image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0xFFF1F5F2)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Eco,
              contentDescription = null,
              tint = KisanMutedSage,
              modifier = Modifier.size(56.dp)
            )
          }
        }

        // Low-Confidence Warning Banner overlay
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF263238).copy(alpha = 0.88f),
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color(0xFFFFB74D),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${(confidence * 100).toInt()}% Confidence (Threshold: 65%)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Threshold Safety Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      border = BorderStroke(1.dp, Color(0xFFFFCC80)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = Color(0xFFFFF3E0),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Diagnosis Withheld for Safety",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "To protect your $cropHint crop from accidental exposure to unneeded or harmful chemicals, KisanAI requires at least 65% diagnostic confidence before recommending chemical or organic treatments.",
          fontSize = 13.sp,
          color = KisanCharcoal.copy(alpha = 0.85f),
          lineHeight = 18.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Analysis Reasons
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Why is confidence low?",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))

        reasons.forEach { reason ->
          Row(
            modifier = Modifier.padding(vertical = 3.dp),
            verticalAlignment = Alignment.Top
          ) {
            Text(text = "• ", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
            Text(
              text = reason,
              fontSize = 13.sp,
              color = KisanCharcoal.copy(alpha = 0.85f),
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Photography Suggestions
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Tips for a clear scan:",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))

        suggestions.forEach { tip ->
          Row(
            modifier = Modifier.padding(vertical = 3.dp),
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
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = tip,
              fontSize = 13.sp,
              color = KisanCharcoal.copy(alpha = 0.85f),
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Helpline Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = KisanEmeraldLight.copy(alpha = 0.4f)),
      border = BorderStroke(1.dp, KisanEmerald.copy(alpha = 0.2f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = null,
              tint = KisanEmerald,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Kisan Call Centre (Govt. of India)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = KisanDeepForest
            )
          }
          Text(
            text = "Toll-Free",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = KisanEmerald
          )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Speak directly to a government agricultural scientist for personalized crop advice.",
          fontSize = 12.sp,
          color = KisanCharcoal,
          lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18001801551"))
              context.startActivity(intent)
            } catch (_: Exception) {
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag("kisan_call_centre_button")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Call 1800-180-1551",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Action Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onTryDifferentCrop,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, KisanEmerald),
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("inconclusive_change_crop_button")
      ) {
        Text(
          text = "Change Crop",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanEmerald
        )
      }

      Button(
        onClick = onRetake,
        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
          .weight(1.2f)
          .height(50.dp)
          .testTag("inconclusive_retake_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Retake Photo",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
