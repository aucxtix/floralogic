package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.DeviceHealthDto
import com.example.data.local.SensorReadingEntity
import com.example.data.model.AppStrings
import com.example.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartFarmIoTScreen(
    deviceHealth: DeviceHealthDto?,
    liveReading: SensorReadingEntity?,
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("24H") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Smart Farm / IoT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "ESP32 Sensor Telemetry Network",
                            fontSize = 11.sp,
                            color = KisanMutedSage
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KisanCharcoal)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = KisanEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KisanWarmIvory)
            )
        },
        containerColor = KisanWarmIvory,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("smart_farm_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hardware Status Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("ESP32 Farm Monitor #001", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                                    Text("Station: Plot 1 (Tomato Field)", fontSize = 11.sp, color = KisanMutedSage)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF16A34A), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ONLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SensorStat("Signal", "-68 dBm", Icons.Default.Wifi)
                            SensorStat("Battery", "${deviceHealth?.battery ?: 89}%", Icons.Default.BatteryFull)
                            SensorStat("Sync", "12s ago", Icons.Default.Sync)
                            SensorStat("Firmware", "v2.1.4", Icons.Default.Code)
                        }
                    }
                }
            }

            // Live Telemetry Grid
            item {
                Text("Live Sensor Readings", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val moistureVal = if (liveReading?.sensorType == "SOIL_MOISTURE") liveReading.value else 29.0
                    TelemetryCard(
                        title = "Soil Moisture",
                        value = "${moistureVal.toInt()}%",
                        status = "Water Stress",
                        color = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "Soil Temp",
                        value = "24.1°C",
                        status = "Optimal",
                        color = KisanEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TelemetryCard(
                        title = "Air Temp",
                        value = "28.4°C",
                        status = "Normal",
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "Humidity",
                        value = "67%",
                        status = "Favorable",
                        color = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TelemetryCard(
                        title = "Sunlight",
                        value = "712 lux",
                        status = "Bright Sun",
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "Water Tank",
                        value = "81%",
                        status = "Sufficient",
                        color = KisanEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sensor History Chart
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("MOISTURE HISTORY (PLOT 1)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("24H", "7D", "30D").forEach { tf ->
                                    val isSel = selectedTimeframe == tf
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) KisanEmerald else Color(0xFFF1F5F9),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { selectedTimeframe = tf }
                                    ) {
                                        Text(
                                            text = tf,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else KisanCharcoal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val path = Path().apply {
                                    moveTo(0f, h * 0.35f)
                                    lineTo(w * 0.25f, h * 0.40f)
                                    lineTo(w * 0.5f, h * 0.55f)
                                    lineTo(w * 0.75f, h * 0.70f)
                                    lineTo(w, h * 0.82f)
                                }
                                drawPath(path, Color(0xFF2563EB), style = Stroke(width = 3.dp.toPx()))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorStat(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = KisanMutedSage, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
        Text(label, fontSize = 9.sp, color = KisanMutedSage)
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    value: String,
    status: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = KisanMutedSage)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
            Spacer(modifier = Modifier.height(2.dp))
            Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
