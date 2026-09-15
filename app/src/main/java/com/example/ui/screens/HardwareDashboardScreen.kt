package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.DeviceHealthDto
import com.example.data.local.SensorReadingEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RealTimeHardwareDashboard(
    deviceHealth: DeviceHealthDto?,
    liveReading: SensorReadingEntity?,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with LIVE indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sensors, contentDescription = null, tint = KisanEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hardware Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }
                
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Box(modifier = Modifier.size(6.dp).background(Color.Red, androidx.compose.foundation.shape.CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LIVE", fontSize = 10.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Device Health Status
            if (deviceHealth != null) {
                val timeSinceSeen = (System.currentTimeMillis() - deviceHealth.lastSeen) / 1000
                val isOnline = timeSinceSeen < 60 && deviceHealth.status == "ONLINE"
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(deviceHealth.deviceId, fontSize = 12.sp, color = KisanCharcoal, fontWeight = FontWeight.Medium)
                    Text(if (isOnline) "ONLINE" else "OFFLINE", fontSize = 12.sp, color = if (isOnline) KisanEmerald else Color.Red, fontWeight = FontWeight.Bold)
                }
                
                Text("Last update: $timeSinceSeen seconds ago", fontSize = 11.sp, color = KisanMutedSage)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BatteryFull, contentDescription = null, modifier = Modifier.size(14.dp), tint = KisanMutedSage)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${deviceHealth.battery}%", fontSize = 11.sp, color = KisanCharcoal)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(14.dp), tint = KisanMutedSage)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Good", fontSize = 11.sp, color = KisanCharcoal)
                    }
                    Text("v${deviceHealth.firmwareVersion}", fontSize = 11.sp, color = KisanMutedSage)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = KisanCardBorder)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Live Sensor Readings
            if (liveReading != null) {
                val isSuspicious = liveReading.status != "VALID"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(liveReading.sensorType, fontSize = 12.sp, color = KisanMutedSage)
                        Text(String.format("%.1f %s", liveReading.value, liveReading.unit), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (isSuspicious) Color(0xFFE65100) else KisanCharcoal)
                    }
                    
                    if (isSuspicious) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(liveReading.status, fontSize = 10.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text("Validating...", fontSize = 10.sp, color = KisanEmerald)
                    }
                }
            }
        }
    }
}
