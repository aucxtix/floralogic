package com.example.data.repository

import com.example.data.api.WeatherApiService
import com.example.data.local.KisanDao
import com.example.data.local.WeatherObservationEntity

class WeatherRepository(
    private val kisanDao: KisanDao,
    private val apiService: WeatherApiService? = null
) {
    suspend fun getLiveWeather(lat: Double, lon: Double): WeatherObservationEntity {
        return try {
            val dto = apiService?.getCurrentWeather(lat, lon) 
            if (dto != null) {
                val entity = WeatherObservationEntity(
                    farmId = "FARM_001",
                    temperature = dto.temperature,
                    humidity = dto.humidity,
                    rainfall = dto.rainfall,
                    windSpeed = dto.windSpeed,
                    condition = dto.condition,
                    timestamp = dto.timestamp
                )
                kisanDao.insertWeatherObservation(entity)
                entity
            } else {
                getMockWeather()
            }
        } catch (e: Exception) {
            getMockWeather()
        }
    }
    
    private fun getMockWeather() = WeatherObservationEntity(
        farmId = "FARM_001",
        temperature = 28.5,
        humidity = 65.0,
        rainfall = 0.0,
        windSpeed = 12.0,
        condition = "Sunny",
        timestamp = System.currentTimeMillis()
    )
}
