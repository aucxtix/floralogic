package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecast
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import com.example.ui.theme.KisanCardBorder
import com.example.ui.theme.KisanCharcoal
import com.example.ui.theme.KisanDeepForest
import com.example.ui.theme.KisanEmerald
import com.example.ui.theme.KisanEmeraldLight
import com.example.ui.theme.KisanHarvestGold
import com.example.ui.theme.KisanMutedSage
import com.example.ui.theme.KisanWarmIvory
import com.example.ui.theme.KisanWhite

@Composable
fun WeatherIrrigationScreen(
  weather: WeatherInfo,
  farmerProfile: FarmerProfile,
  onBackClick: () -> Unit = {},
  onRefreshLocation: () -> Unit = {},
  onSelectManualLocation: (cityName: String, stateName: String, lat: Double, lon: Double) -> Unit = { _, _, _, _ -> },
  isRefreshingLocation: Boolean = false,
  modifier: Modifier = Modifier
) {
  var showManualLocationDialog by remember { mutableStateOf(false) }

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    if (fineGranted || coarseGranted) {
      onRefreshLocation()
    } else {
      showManualLocationDialog = true
    }
  }
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KisanWarmIvory)
      .statusBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
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
        text = "Weather & Irrigation",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = KisanCharcoal
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Location & Date Subtitle + Coordinates Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 6.dp, end = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "${weather.locationName}, ${weather.state}",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanCharcoal
        )
        Text(
          text = "Coordinates: ${weather.coordinatesFormatted ?: "21.1702° N, 72.8311° E"}",
          fontSize = 11.sp,
          color = KisanMutedSage
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = KisanEmeraldLight,
          modifier = Modifier
            .clickable {
              locationPermissionLauncher.launch(
                arrayOf(
                  Manifest.permission.ACCESS_FINE_LOCATION,
                  Manifest.permission.ACCESS_COARSE_LOCATION
                )
              )
            }
            .testTag("weather_update_gps_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (isRefreshingLocation) {
              CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = KisanEmerald
              )
            } else {
              Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Update GPS Location",
                tint = KisanEmerald,
                modifier = Modifier.size(14.dp)
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isRefreshingLocation) "Locating..." else "GPS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = KisanEmerald
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = KisanEmeraldLight,
          modifier = Modifier
            .clickable { showManualLocationDialog = true }
            .testTag("weather_manual_location_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.EditLocation,
              contentDescription = "Manual District Selection",
              tint = KisanEmerald,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Manual",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = KisanEmerald
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Main Current Weather Card
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "${weather.temperatureC}°C",
              fontSize = 38.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Text(
              text = weather.condition,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium,
              color = KisanMutedSage
            )
          }

          Surface(
            shape = CircleShape,
            color = KisanHarvestGold.copy(alpha = 0.18f),
            modifier = Modifier.size(64.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = KisanHarvestGold,
                modifier = Modifier.size(36.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3 Metrics: Humidity, Rainfall, Wind
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          WeatherMetricCol(
            icon = Icons.Default.Opacity,
            value = "${weather.humidityPercent}%",
            label = "Humidity"
          )
          WeatherMetricCol(
            icon = Icons.Default.WaterDrop,
            value = "${weather.rainfallMm} mm",
            label = "Rainfall"
          )
          WeatherMetricCol(
            icon = Icons.Default.Air,
            value = "${weather.windSpeedKmh} km/h",
            label = "Wind"
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 5-Day Forecast Card
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
          text = "5-Day Forecast",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          weather.weeklyForecast.forEach { forecast ->
            ForecastDayItem(forecast)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Irrigation Advisory Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = KisanWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = if (weather.isIrrigationNeeded) KisanEmeraldLight else Color(0xFFF0F4F2),
            modifier = Modifier.size(46.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = if (weather.isIrrigationNeeded) KisanEmerald else KisanMutedSage,
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = if (weather.isIrrigationNeeded) "Irrigation Recommended" else "Irrigation Not Needed",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = KisanCharcoal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Crop: ${farmerProfile.primaryCrop.ifEmpty { "Tomato" }}",
              fontSize = 12.sp,
              color = KisanMutedSage
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = weather.irrigationAdvice,
          fontSize = 13.sp,
          color = KisanDeepForest,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = KisanWarmIvory,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Science,
              contentDescription = null,
              tint = KisanEmerald,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Calculated via FAO-56 Penman-Monteith Evapotranspiration Model",
              fontSize = 11.sp,
              color = KisanMutedSage,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Pesticide Spray Window & Climate Alert Card
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
          text = "Foliar Spray Advisory",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = weather.sprayCondition,
          fontSize = 13.sp,
          color = KisanDeepForest,
          fontWeight = FontWeight.Medium
        )

        if (weather.riskAlertMessage != null) {
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = weather.riskAlertMessage,
                fontSize = 12.sp,
                color = Color(0xFFB26A00)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  if (showManualLocationDialog) {
    ManualLocationDialog(
      currentCity = weather.locationName,
      currentState = weather.state,
      onDismiss = { showManualLocationDialog = false },
      onSelectLocation = { city, state, lat, lon ->
        onSelectManualLocation(city, state, lat, lon)
        showManualLocationDialog = false
      }
    )
  }
}

data class AgriculturalDistrict(
  val city: String,
  val state: String,
  val latitude: Double,
  val longitude: Double
)

private val AGRI_DISTRICTS = listOf(
  AgriculturalDistrict("Surat", "Gujarat", 21.1702, 72.8311),
  AgriculturalDistrict("Nashik", "Maharashtra", 19.9975, 73.7898),
  AgriculturalDistrict("Karnal", "Haryana", 29.6857, 76.9905),
  AgriculturalDistrict("Ludhiana", "Punjab", 30.9010, 75.8573),
  AgriculturalDistrict("Warangal", "Telangana", 17.9689, 79.5941),
  AgriculturalDistrict("Guntur", "Andhra Pradesh", 16.3067, 80.4365),
  AgriculturalDistrict("Indore", "Madhya Pradesh", 22.7196, 75.8577),
  AgriculturalDistrict("Patna", "Bihar", 25.5941, 85.1376)
)

@Composable
fun ManualLocationDialog(
  currentCity: String,
  currentState: String,
  onDismiss: () -> Unit,
  onSelectLocation: (city: String, state: String, lat: Double, lon: Double) -> Unit
) {
  var customCity by remember { mutableStateOf(currentCity) }
  var customState by remember { mutableStateOf(currentState) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.EditLocation,
          contentDescription = null,
          tint = KisanEmerald,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Select Farm Location",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = KisanCharcoal
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "GPS permission denied or manual fallback. Select your farming district to pull verified Open-Meteo weather and Penman-Monteith irrigation estimates:",
          fontSize = 12.sp,
          color = KisanMutedSage,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Major Agricultural Hubs:",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          AGRI_DISTRICTS.forEach { district ->
            val isSelected = district.city.equals(customCity, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) KisanEmeraldLight else KisanWarmIvory,
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  customCity = district.city
                  customState = district.state
                  onSelectLocation(district.city, district.state, district.latitude, district.longitude)
                }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${district.city}, ${district.state}",
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) KisanEmerald else KisanCharcoal
                )
                Text(
                  text = "${district.latitude}° N",
                  fontSize = 11.sp,
                  color = KisanMutedSage
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Or Enter Custom District:",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = KisanCharcoal
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = customCity,
          onValueChange = { customCity = it },
          label = { Text("District / City") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = customState,
          onValueChange = { customState = it },
          label = { Text("State") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (customCity.isNotBlank()) {
            val matched = AGRI_DISTRICTS.find { it.city.equals(customCity.trim(), ignoreCase = true) }
            val lat = matched?.latitude ?: 21.1702
            val lon = matched?.longitude ?: 72.8311
            onSelectLocation(customCity.trim(), customState.trim().ifEmpty { "India" }, lat, lon)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald)
      ) {
        Text("Apply Location")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = KisanMutedSage)
      }
    }
  )
}

@Composable
private fun WeatherMetricCol(icon: ImageVector, value: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = KisanEmerald,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = value,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
    Text(
      text = label,
      fontSize = 11.sp,
      color = KisanMutedSage
    )
  }
}

@Composable
private fun ForecastDayItem(forecast: DailyForecast) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Text(
      text = "${forecast.dayLabel} ${forecast.dayNumber}",
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      color = KisanCharcoal
    )
    Spacer(modifier = Modifier.height(6.dp))
    Icon(
      imageVector = when {
        forecast.condition.contains("Rain", ignoreCase = true) -> Icons.Default.Thunderstorm
        forecast.condition.contains("Cloud", ignoreCase = true) -> Icons.Default.WbCloudy
        else -> Icons.Default.WbSunny
      },
      contentDescription = forecast.condition,
      tint = if (forecast.condition.contains("Rain")) KisanEmerald else KisanHarvestGold,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "${forecast.temperatureC}°",
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = KisanCharcoal
    )
  }
}
