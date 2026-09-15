package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_readings")
data class SensorReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val farmId: String,
    val sensorType: String,
    val value: Double,
    val unit: String,
    val status: String, // VALID, SUSPICIOUS, INVALID
    val timestamp: Long
)

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val deviceId: String,
    val farmId: String,
    val status: String,
    val battery: Int,
    val signalStrength: Int,
    val firmwareVersion: String,
    val lastSeen: Long
)
