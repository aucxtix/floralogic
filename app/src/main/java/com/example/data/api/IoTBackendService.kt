package com.example.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Models for Backend integration
data class SensorReadingDto(
    val deviceId: String,
    val sensorType: String,
    val value: Double,
    val unit: String,
    val status: String,
    val timestamp: Long
)

data class DeviceHealthDto(
    val deviceId: String,
    val status: String,
    val battery: Int,
    val signalStrength: Int,
    val firmwareVersion: String,
    val lastSeen: Long
)

/**
 * Retrofit Interface simulating connection to the Phase 3 modular backend
 * Route: /api/hardware
 */
interface IoTBackendService {
    @GET("/api/hardware/devices/{farmId}")
    suspend fun getDevices(@Path("farmId") farmId: String): List<DeviceHealthDto>

    @GET("/api/hardware/readings/{deviceId}")
    suspend fun getRecentReadings(
        @Path("deviceId") deviceId: String,
        @Query("limit") limit: Int = 100
    ): List<SensorReadingDto>
}
