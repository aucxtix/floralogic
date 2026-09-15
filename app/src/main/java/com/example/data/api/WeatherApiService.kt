package com.example.data.api

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherObservationDto(
    val temperature: Double,
    val humidity: Double,
    val rainfall: Double,
    val windSpeed: Double,
    val condition: String,
    val timestamp: Long
)

/**
 * Retrofit Interface simulating connection to the modular backend for Weather data
 * Route: /api/weather
 */
interface WeatherApiService {
    @GET("/api/weather/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherObservationDto
}
