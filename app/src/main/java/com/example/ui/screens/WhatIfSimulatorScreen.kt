package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatIfSimulatorScreen(
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var soilMoisture by remember { mutableFloatStateOf(45f) }
    var irrigationDays by remember { mutableFloatStateOf(3f) }
    var weatherScenario by remember { mutableStateOf("Normal") }
    var pestRisk by remember { mutableStateOf("Low") }

    // Dynamic simulation calculation
    val weatherMultiplier = when (weatherScenario) {
        "Drought / Heat" -> 0.82f
        "Heavy Rain" -> 0.90f
        else -> 1.0f
    }
    val moistureMultiplier = (soilMoisture / 50f).coerceIn(0.6f, 1.15f)
    val irrigationMultiplier = (irrigationDays / 3f).coerceIn(0.7f, 1.1f)
    val pestMultiplier = when (pestRisk) {
        "High" -> 0.75f
        "Medium" -> 0.90f
        else -> 1.0f
    }

    val simulatedYield = (3.1f * moistureMultiplier * irrigationMultiplier * weatherMultiplier * pestMultiplier).coerceIn(1.8f, 3.8f)
    val baselineYield = 3.1f
    val percentDelta = ((simulatedYield - baselineYield) / baselineYield * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "What-If Simulator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Model Hypothetical Farm Scenarios",
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
                .testTag("what_if_simulator_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Simulation Disclaimer Notice
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SIMULATION — ESTIMATED SCENARIOS ONLY. NOT A GUARANTEED OUTCOME.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            // Real-Time Outcome Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = KisanDeepForest),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("SIMULATED HARVEST OUTCOME", fontSize = 11.sp, color = KisanEmeraldLight, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = String.format("%.2f t/ac", simulatedYield),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text("Baseline: 3.10 t/acre", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (percentDelta >= 0) Color(0xFF22C55E) else Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = if (percentDelta >= 0) "+$percentDelta%" else "$percentDelta%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Controls Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("HYPOTHETICAL PARAMETERS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Parameter 1: Target Soil Moisture
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Target Soil Moisture", fontSize = 12.sp, color = KisanCharcoal)
                            Text("${soilMoisture.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        }
                        Slider(
                            value = soilMoisture,
                            onValueChange = { soilMoisture = it },
                            valueRange = 15f..80f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF2563EB), activeTrackColor = Color(0xFF2563EB))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Parameter 2: Irrigation Frequency
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Irrigation Frequency", fontSize = 12.sp, color = KisanCharcoal)
                            Text("${irrigationDays.toInt()} days / week", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                        }
                        Slider(
                            value = irrigationDays,
                            onValueChange = { irrigationDays = it },
                            valueRange = 1f..7f,
                            steps = 5,
                            colors = SliderDefaults.colors(thumbColor = KisanEmerald, activeTrackColor = KisanEmerald)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Parameter 3: Weather Scenario Toggle
                        Text("Weather Scenario", fontSize = 12.sp, color = KisanCharcoal)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Normal", "Drought / Heat", "Heavy Rain").forEach { scen ->
                                val isSel = weatherScenario == scen
                                Button(
                                    onClick = { weatherScenario = scen },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSel) KisanEmerald else Color(0xFFF1F5F9),
                                        contentColor = if (isSel) Color.White else KisanCharcoal
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    Text(scen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Parameter 4: Disease / Pest Pressure
                        Text("Disease Pressure Scenario", fontSize = 12.sp, color = KisanCharcoal)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Low", "Medium", "High").forEach { r ->
                                val isSel = pestRisk == r
                                Button(
                                    onClick = { pestRisk = r },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSel) KisanDeepForest else Color(0xFFF1F5F9),
                                        contentColor = if (isSel) Color.White else KisanCharcoal
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    Text(r, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
