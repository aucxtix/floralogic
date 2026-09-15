package com.example.data.repository

import com.example.data.api.DeviceHealthDto
import com.example.data.api.IoTBackendService
import com.example.data.api.SensorReadingDto
import com.example.data.local.KisanDao
import com.example.data.local.SensorReadingEntity
import com.example.data.local.DeviceEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Real-time Hardware Dashboard IoT Repository
 * Integrates Retrofit modular backend with local Room persistence.
 */
class IoTRepository(
    private val kisanDao: KisanDao,
    private val apiService: IoTBackendService? = null
) {
    // 7. REAL-TIME HARDWARE DASHBOARD (simulated via flow in absence of real WebSocket)
    // In a real app this would use OkHttp WebSockets or an MQTT client
    fun observeLiveSensorData(deviceId: String): Flow<SensorReadingEntity> = flow {
        var lastReading = SensorReadingEntity(
            deviceId = deviceId,
            farmId = "FARM_001",
            sensorType = "SoilMoisture",
            value = 42.0,
            unit = "%",
            status = "VALID",
            timestamp = System.currentTimeMillis()
        )
        emit(lastReading)
        
        while (true) {
            delay(5000) // Update every 5 seconds without full page refresh
            
            // 9. SENSOR DATA QUALITY validation
            // Simulate random spikes to test validation
            val fluctuation = (Math.random() - 0.5) * 4
            var newValue = lastReading.value + fluctuation
            var status = "VALID"
            
            // Reject impossible values (e.g. negative humidity/moisture)
            if (newValue < 0) {
                newValue = 0.0
                status = "INVALID"
            } else if (newValue > 100 && lastReading.sensorType == "SoilMoisture") {
                newValue = 100.0
                status = "SUSPICIOUS"
            }
            
            lastReading = lastReading.copy(
                value = newValue,
                status = status,
                timestamp = System.currentTimeMillis()
            )
            
            // Cache to database
            kisanDao.insertSensorReading(lastReading)
            
            emit(lastReading)
        }
    }
    
    // 8. DEVICE HEALTH Tracker
    suspend fun getDeviceHealth(farmId: String): List<DeviceHealthDto> {
        return try {
            apiService?.getDevices(farmId) ?: getDefaultMockDevices()
        } catch (e: Exception) {
            getDefaultMockDevices()
        }
    }
    
    private fun getDefaultMockDevices() = listOf(
        DeviceHealthDto(
            deviceId = "ESP32_001",
            status = "ONLINE",
            battery = 87,
            signalStrength = -45,
            firmwareVersion = "v1.2.0",
            lastSeen = System.currentTimeMillis() - 12000
        )
    )
}
