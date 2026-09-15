package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo

/**
 * OverviewScreen displays the comprehensive Farm Overview,
 * including live Weather, 5-Day Forecast, Irrigation Advisory,
 * Soil Status, and real-time FusedLocationProviderClient GPS coordinates.
 */
@Composable
fun OverviewScreen(
  weather: WeatherInfo,
  farmerProfile: FarmerProfile,
  onBackClick: () -> Unit = {},
  onRefreshLocation: () -> Unit = {},
  onSelectManualLocation: (cityName: String, stateName: String, lat: Double, lon: Double) -> Unit = { _, _, _, _ -> },
  isRefreshingLocation: Boolean = false,
  modifier: Modifier = Modifier
) {
  WeatherIrrigationScreen(
    weather = weather,
    farmerProfile = farmerProfile,
    onBackClick = onBackClick,
    onRefreshLocation = onRefreshLocation,
    onSelectManualLocation = onSelectManualLocation,
    isRefreshingLocation = isRefreshingLocation,
    modifier = modifier.fillMaxSize()
  )
}
