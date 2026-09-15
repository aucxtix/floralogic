package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.local.FarmCropEntity
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile

import com.example.data.api.DeviceHealthDto
import com.example.data.local.SensorReadingEntity

/**
 * Dedicated Dashboard Screen representing the Farmer Dashboard
 * with Crop Health summary donut, recent activities, and farm management.
 */
@Composable
fun DashboardScreen(
  crops: List<FarmCropEntity>,
  farmerProfile: FarmerProfile,
  deviceHealth: DeviceHealthDto? = null,
  liveReading: SensorReadingEntity? = null,
  onAddCrop: (FarmCropEntity) -> Unit,
  onDeleteCrop: (FarmCropEntity) -> Unit,
  strings: AppStrings,
  onOpenFilter: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  FarmScreen(
    crops = crops,
    farmerProfile = farmerProfile,
    deviceHealth = deviceHealth,
    liveReading = liveReading,
    onAddCrop = onAddCrop,
    onDeleteCrop = onDeleteCrop,
    strings = strings,
    onOpenFilter = onOpenFilter,
    modifier = modifier.fillMaxSize()
  )
}
