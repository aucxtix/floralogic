package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.data.api.DeviceHealthDto
import com.example.data.local.SensorReadingEntity

import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FarmCropEntity
import com.example.data.model.AppStrings
import com.example.data.model.CropGrowthStage
import com.example.data.model.FarmerProfile
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@Composable
fun FarmScreen(
  deviceHealth: DeviceHealthDto? = null,
  liveReading: SensorReadingEntity? = null,
  crops: List<FarmCropEntity>,
  farmerProfile: FarmerProfile,
  onAddCrop: (FarmCropEntity) -> Unit,
  onDeleteCrop: (FarmCropEntity) -> Unit,
  strings: AppStrings,
  onOpenFilter: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var cropToDelete by remember { mutableStateOf<FarmCropEntity?>(null) }

  val healthyCount = crops.count { !it.healthStatus.contains("Attention", ignoreCase = true) && !it.healthStatus.contains("Critical", ignoreCase = true) }
  val attentionCount = crops.count { it.healthStatus.contains("Attention", ignoreCase = true) }
  val criticalCount = crops.count { it.healthStatus.contains("Critical", ignoreCase = true) }
  val totalCrops = crops.size.coerceAtLeast(1)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      if (deviceHealth != null || liveReading != null) {
        RealTimeHardwareDashboard(deviceHealth, liveReading, modifier = Modifier.padding(top = 16.dp))
      }
    }
    // Top Header: "My Farms" + Filter & Add Button
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "My Farms",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = KisanWhite,
            border = BorderStroke(1.dp, KisanCardBorder),
            modifier = Modifier
              .size(38.dp)
              .clickable { onOpenFilter() }
              .testTag("farm_filter_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter Crops",
                tint = KisanCharcoal,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = KisanEmerald,
            modifier = Modifier
              .size(38.dp)
              .clickable { showAddDialog = true }
              .testTag("farm_add_crop_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Crop",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // Main Farm Card: "Green Valley Farm" - 2.5 acres • Tomato >
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = KisanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = KisanEmeraldLight,
              modifier = Modifier.size(46.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = KisanEmerald,
                  modifier = Modifier.size(24.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = farmerProfile.farmName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
              )
              Text(
                text = "${farmerProfile.totalLandAcres} acres • ${farmerProfile.primaryCrop}",
                fontSize = 13.sp,
                color = KisanMutedSage
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = KisanCharcoal,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    // Crop Health Donut Summary Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KisanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Text(
            text = "Crop Health",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Donut Canvas
            Box(
              modifier = Modifier.size(110.dp),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.size(100.dp)) {
                val strokeWidth = 14.dp.toPx()
                val arcSize = size.width - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                // Background track
                drawArc(
                  color = Color(0xFFE8ECE9),
                  startAngle = 0f,
                  sweepAngle = 360f,
                  useCenter = false,
                  topLeft = topLeft,
                  size = Size(arcSize, arcSize),
                  style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )

                // Green healthy segment
                val greenSweep = (healthyCount.toFloat() / totalCrops) * 360f
                drawArc(
                  color = KisanEmerald,
                  startAngle = -90f,
                  sweepAngle = greenSweep.coerceAtLeast(10f),
                  useCenter = false,
                  topLeft = topLeft,
                  size = Size(arcSize, arcSize),
                  style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )

                // Amber attention segment
                val amberSweep = (attentionCount.toFloat() / totalCrops) * 360f
                if (attentionCount > 0) {
                  drawArc(
                    color = KisanHarvestGold,
                    startAngle = -90f + greenSweep,
                    sweepAngle = amberSweep.coerceAtLeast(10f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                  )
                }
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "Good",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = KisanCharcoal
                )
                Text(
                  text = "$totalCrops crops",
                  fontSize = 11.sp,
                  color = KisanMutedSage
                )
              }
            }

            // Stats Breakdown
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              HealthStatRow(color = KisanEmerald, label = "Healthy", count = healthyCount)
              HealthStatRow(color = KisanHarvestGold, label = "Needs Attention", count = attentionCount)
              HealthStatRow(color = Color(0xFFC94C4C), label = "Critical", count = criticalCount)
            }
          }
        }
      }
    }

    // Recent Activity Section
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KisanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Text(
            text = "Recent Activity",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = KisanCharcoal
          )

          Spacer(modifier = Modifier.height(14.dp))

          ActivityItem(
            iconColor = Color(0xFFC94C4C),
            title = "Late Blight detected",
            subtitle = "Tomato • 2 hours ago"
          )
          Spacer(modifier = Modifier.height(12.dp))
          ActivityItem(
            iconColor = KisanHarvestGold,
            title = "Irrigation recommended",
            subtitle = "Maize • 5 hours ago"
          )
          Spacer(modifier = Modifier.height(12.dp))
          ActivityItem(
            iconColor = KisanEmerald,
            title = "Weather alert: Heat risk",
            subtitle = "Surat, Gujarat • 1 day ago"
          )
        }
      }
    }

    // Registered Farm Plots List Header
    item {
      Text(
        text = "Registered Plots (${crops.size})",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    }

    if (crops.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.Eco,
          title = "No Farm Plots Added",
          description = "Add your farm plots to monitor crop health, growth stages, and irrigation needs.",
          actionText = "Add Plot",
          onActionClick = { showAddDialog = true }
        )
      }
    } else {
      items(crops) { crop ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = KisanWhite),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                shape = CircleShape,
                color = if (crop.healthStatus.contains("Attention", ignoreCase = true)) {
                  KisanHarvestGold.copy(alpha = 0.15f)
                } else {
                  KisanEmeraldLight
                },
                modifier = Modifier.size(38.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = if (crop.healthStatus.contains("Attention", ignoreCase = true)) {
                      KisanHarvestGold
                    } else {
                      KisanEmerald
                    },
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = crop.cropName,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = KisanCharcoal
                )
                Text(
                  text = "${crop.areaAcres} acres • ${crop.growthStage} • ${crop.healthStatus}",
                  fontSize = 12.sp,
                  color = KisanMutedSage
                )
              }
            }

            IconButton(
              onClick = { cropToDelete = crop },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete plot",
                tint = KisanMutedSage,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Delete Confirmation Dialog
  cropToDelete?.let { crop ->
    ConfirmDialog(
      title = "Delete Farm Plot",
      message = "Are you sure you want to remove '${crop.cropName}' (${crop.areaAcres} acres) from your farm records?",
      confirmText = "Delete",
      cancelText = "Cancel",
      isDestructive = true,
      onConfirm = {
        onDeleteCrop(crop)
        cropToDelete = null
      },
      onDismiss = { cropToDelete = null }
    )
  }

  // Add Crop Dialog
  if (showAddDialog) {
    AddCropModalDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { newCrop ->
        onAddCrop(newCrop)
        showAddDialog = false
      }
    )
  }
}

@Composable
private fun HealthStatRow(color: Color, label: String, count: Int) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "$label: ",
      fontSize = 12.sp,
      color = KisanCharcoal
    )
    Text(
      text = "$count",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
  }
}

@Composable
private fun ActivityItem(iconColor: Color, title: String, subtitle: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(iconColor)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = KisanCharcoal
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = KisanMutedSage
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCropModalDialog(
  onDismiss: () -> Unit,
  onConfirm: (FarmCropEntity) -> Unit
) {
  var cropName by remember { mutableStateOf("") }
  var variety by remember { mutableStateOf("") }
  var areaAcresText by remember { mutableStateOf("1.5") }
  var growthStage by remember { mutableStateOf("Flowering") }
  var soilType by remember { mutableStateOf("Loamy Alluvial") }

  val stages = listOf("Sowing", "Vegetative", "Flowering", "Fruiting", "Maturity", "Harvesting")
  var stageDropdownExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = KisanWhite,
    title = {
      Text(
        text = "Register Farm Plot",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = cropName,
          onValueChange = { cropName = it },
          label = { Text("Crop Name (e.g. Tomato, Cotton)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KisanEmerald,
            unfocusedBorderColor = KisanCardBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = variety,
          onValueChange = { variety = it },
          label = { Text("Variety (e.g. Hybrid Roma)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KisanEmerald,
            unfocusedBorderColor = KisanCardBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = areaAcresText,
          onValueChange = { areaAcresText = it },
          label = { Text("Area (Acres)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KisanEmerald,
            unfocusedBorderColor = KisanCardBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
          expanded = stageDropdownExpanded,
          onExpandedChange = { stageDropdownExpanded = it }
        ) {
          OutlinedTextField(
            value = growthStage,
            onValueChange = {},
            readOnly = true,
            label = { Text("Growth Stage") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageDropdownExpanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KisanEmerald,
              unfocusedBorderColor = KisanCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = stageDropdownExpanded,
            onDismissRequest = { stageDropdownExpanded = false }
          ) {
            stages.forEach { stage ->
              DropdownMenuItem(
                text = { Text(stage) },
                onClick = {
                  growthStage = stage
                  stageDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (cropName.isNotBlank()) {
            val acres = areaAcresText.toDoubleOrNull() ?: 1.0
            onConfirm(
              FarmCropEntity(
                cropName = cropName.trim(),
                variety = if (variety.isBlank()) "Standard" else variety.trim(),
                areaAcres = acres,
                sowingDate = "Recent",
                growthStage = growthStage,
                soilType = soilType,
                healthStatus = "Healthy",
                lastWateredDate = "Today",
                notes = "Added via app"
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = KisanEmerald,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Add Plot", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = KisanMutedSage)
      }
    }
  )
}
