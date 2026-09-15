package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_observations")
data class WeatherObservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmId: String,
    val temperature: Double,
    val humidity: Double,
    val rainfall: Double,
    val windSpeed: Double,
    val condition: String,
    val timestamp: Long
)
