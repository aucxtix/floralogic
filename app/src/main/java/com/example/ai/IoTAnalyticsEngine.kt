package com.example.ai

/**
 * IoT anomaly classification.
 */
enum class SensorAnomalyType {
    NONE,
    DEVICE_OFFLINE,
    FLATLINE_STUCK_VALUE,
    SUDDEN_IMPOSSIBLE_SPIKE,
    OUT_OF_BOUNDS_OUTLIER,
    BATTERY_CRITICAL
}

/**
 * Diagnostic result of IoT hardware stream.
 */
data class IoTDiagnosticsResult(
    val deviceId: String,
    val isOperational: Boolean,
    val anomalyType: SensorAnomalyType,
    val isHardwareFailure: Boolean, // True = hardware failure, False = actual agronomic stress
    val diagnosticMessage: String,
    val recommendedTroubleshooting: String,
    val soilMoisture: Int,
    val soilTempC: Float,
    val airTempC: Float,
    val airHumidityPercent: Int,
    val batteryPercent: Int,
    val telemetryHealthScore: Int // 0 to 100
)

/**
 * IoTAnalyticsEngine
 *
 * Distinguishes true crop water/heat stress from telemetry glitches, frozen ADC converters,
 * or disconnected probes.
 */
object IoTAnalyticsEngine {

    fun diagnoseSensors(state: DigitalFarmState): IoTDiagnosticsResult {
        val sensor = state.sensorData

        // 1. Check Device Offline
        if (!sensor.deviceOnline) {
            return IoTDiagnosticsResult(
                deviceId = sensor.deviceId,
                isOperational = false,
                anomalyType = SensorAnomalyType.DEVICE_OFFLINE,
                isHardwareFailure = true,
                diagnosticMessage = "Sensor node has not transmitted heartbeat packets in > 3 hours.",
                recommendedTroubleshooting = "Check ESP32 solar charging panel, dust accumulation, battery voltage, and local 4G/NB-IoT gateway connection.",
                soilMoisture = sensor.soilMoisturePercent,
                soilTempC = sensor.soilTempC,
                airTempC = sensor.airTempC,
                airHumidityPercent = sensor.airHumidityPercent,
                batteryPercent = sensor.batteryPercent,
                telemetryHealthScore = 20
            )
        }

        // 2. Battery Critical
        if (sensor.batteryPercent <= 15) {
            return IoTDiagnosticsResult(
                deviceId = sensor.deviceId,
                isOperational = true,
                anomalyType = SensorAnomalyType.BATTERY_CRITICAL,
                isHardwareFailure = true,
                diagnosticMessage = "Internal Li-ion battery is at ${sensor.batteryPercent}%. Sensor node risks imminent sleep shutdown.",
                recommendedTroubleshooting = "Inspect solar charge controller and ensure solar panel is facing south without leaf shading.",
                soilMoisture = sensor.soilMoisturePercent,
                soilTempC = sensor.soilTempC,
                airTempC = sensor.airTempC,
                airHumidityPercent = sensor.airHumidityPercent,
                batteryPercent = sensor.batteryPercent,
                telemetryHealthScore = 45
            )
        }

        // 3. Out of bounds outlier (e.g. soil moisture > 100% or negative)
        if (sensor.soilMoisturePercent < 0 || sensor.soilMoisturePercent > 100) {
            return IoTDiagnosticsResult(
                deviceId = sensor.deviceId,
                isOperational = false,
                anomalyType = SensorAnomalyType.OUT_OF_BOUNDS_OUTLIER,
                isHardwareFailure = true,
                diagnosticMessage = "Soil moisture probe ADC reported an uncalibrated raw value (${sensor.soilMoisturePercent}%).",
                recommendedTroubleshooting = "Inspect capacitive probe wiring for corrosion or loose terminal connections.",
                soilMoisture = sensor.soilMoisturePercent,
                soilTempC = sensor.soilTempC,
                airTempC = sensor.airTempC,
                airHumidityPercent = sensor.airHumidityPercent,
                batteryPercent = sensor.batteryPercent,
                telemetryHealthScore = 30
            )
        }

        // 4. Pre-flagged Anomaly in State
        if (sensor.isAnomalyDetected) {
            return IoTDiagnosticsResult(
                deviceId = sensor.deviceId,
                isOperational = true,
                anomalyType = SensorAnomalyType.FLATLINE_STUCK_VALUE,
                isHardwareFailure = false, // Flagged environmental anomaly
                diagnosticMessage = sensor.anomalyDescription ?: "Unusual rate of change detected in root zone parameters.",
                recommendedTroubleshooting = "Perform physical ground check to ensure probe is embedded securely in root mass without air pockets.",
                soilMoisture = sensor.soilMoisturePercent,
                soilTempC = sensor.soilTempC,
                airTempC = sensor.airTempC,
                airHumidityPercent = sensor.airHumidityPercent,
                batteryPercent = sensor.batteryPercent,
                telemetryHealthScore = 65
            )
        }

        // 5. Normal Telemetry Stream (Distinguish real stress from failure)
        val isRealWaterStress = sensor.soilMoisturePercent < state.soil.targetMoistureMin
        val diagnosticMsg = if (isRealWaterStress) {
            "Sensors functioning normally. True agronomic water deficit detected (Moisture: ${sensor.soilMoisturePercent}% vs target ${state.soil.targetMoistureMin}%)."
        } else {
            "All hardware telemetry channels operating within nominal parameters."
        }

        return IoTDiagnosticsResult(
            deviceId = sensor.deviceId,
            isOperational = true,
            anomalyType = SensorAnomalyType.NONE,
            isHardwareFailure = false,
            diagnosticMessage = diagnosticMsg,
            recommendedTroubleshooting = if (isRealWaterStress) "Turn on drip irrigation to recharge root zone." else "No maintenance required.",
            soilMoisture = sensor.soilMoisturePercent,
            soilTempC = sensor.soilTempC,
            airTempC = sensor.airTempC,
            airHumidityPercent = sensor.airHumidityPercent,
            batteryPercent = sensor.batteryPercent,
            telemetryHealthScore = 96
        )
    }
}
