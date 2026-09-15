package com.example.data.model

data class DailyForecast(
  val dayLabel: String,
  val dayNumber: Int,
  val temperatureC: Int,
  val condition: String
)

data class WeatherInfo(
  val locationName: String,
  val state: String,
  val temperatureC: Int,
  val condition: String,
  val conditionIcon: String,
  val humidityPercent: Int,
  val rainProbabilityPercent: Int,
  val rainfallMm: Double = 2.0,
  val windSpeedKmh: Int,
  val irrigationAdvice: String,
  val isIrrigationNeeded: Boolean,
  val sprayCondition: String,
  val heatwaveAlert: Boolean,
  val riskAlertMessage: String?,
  val latitude: Double? = 21.1702,
  val longitude: Double? = 72.8311,
  val coordinatesFormatted: String? = "21.1702° N, 72.8311° E",
  val weeklyForecast: List<DailyForecast> = listOf(
    DailyForecast("Fri", 12, 30, "Sunny"),
    DailyForecast("Sat", 13, 31, "Sunny"),
    DailyForecast("Sun", 14, 32, "Partly Cloudy"),
    DailyForecast("Mon", 15, 31, "Sunny"),
    DailyForecast("Tue", 16, 29, "Light Rain")
  )
)

data class FarmerProfile(
  val name: String = "Rudra Patel",
  val village: String = "Surat",
  val state: String = "Gujarat",
  val totalLandAcres: Double = 2.5,
  val farmName: String = "Green Valley Farm",
  val primaryCrop: String = "Tomato",
  val mobileNumber: String = "+91 98765 43210",
  val language: AppLanguage = AppLanguage.ENGLISH
)
