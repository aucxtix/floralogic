package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatelliteIntelligenceScreen(
    farmerProfile: FarmerProfile,
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLayer by remember { mutableStateOf("NDVI") }
    var showHistoricalCompare by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Satellite Intelligence",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Plot 1: ${farmerProfile.village}, ${farmerProfile.state}",
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
                .testTag("satellite_intelligence_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Satellite Hero Map / Canvas View
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
                            Column {
                                Text("PLOT 1 MULTISPECTRAL VIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Text("Vegetation Health (NDVI: 0.78)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KisanEmerald.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "SENTINEL-2 (10m)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KisanEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Field Boundary & NDVI Visualization
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw grid background
                                for (i in 0..6) {
                                    val x = w * (i / 6f)
                                    drawLine(Color(0xFF1E293B), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                                }
                                for (j in 0..4) {
                                    val y = h * (j / 4f)
                                    drawLine(Color(0xFF1E293B), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                                }

                                // Field Polygon Plot Area
                                val plotX = w * 0.12f
                                val plotY = h * 0.12f
                                val plotW = w * 0.76f
                                val plotH = h * 0.76f

                                // Green High-NDVI zone
                                drawRoundRect(
                                    color = Color(0xFF10B981).copy(alpha = 0.85f),
                                    topLeft = Offset(plotX, plotY),
                                    size = Size(plotW, plotH),
                                    cornerRadius = CornerRadius(12.dp.toPx())
                                )

                                // Stress zone (North-East quadrant)
                                drawRoundRect(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.9f),
                                    topLeft = Offset(plotX + plotW * 0.55f, plotY + 10f),
                                    size = Size(plotW * 0.40f, plotH * 0.45f),
                                    cornerRadius = CornerRadius(8.dp.toPx())
                                )
                            }

                            // Overlay info
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.65f)
                                ) {
                                    Text(
                                        text = "📍 Lat: 21.1702° N, Lon: 72.8311° E",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Layer Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("High Canopy (88%)", fontSize = 11.sp, color = KisanCharcoal)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Moderate Stress (12%)", fontSize = 11.sp, color = KisanCharcoal)
                            }
                            Text("Updated: 3 days ago", fontSize = 10.sp, color = KisanMutedSage)
                        }
                    }
                }
            }

            // Agronomic Interpretation
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "FARMER-FIRST INTERPRETATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanEmerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vegetation health is good. 88% of Plot 1 displays uniform, thick leaf cover. The North-East corner shows mild water stress matching your on-ground soil sensor readings.",
                            fontSize = 13.sp,
                            color = KisanCharcoal,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cloud Coverage: 4%", fontSize = 11.sp, color = KisanMutedSage)
                            Text("Provider: Copernicus Sentinel-2", fontSize = 11.sp, color = KisanMutedSage)
                        }
                    }
                }
            }
        }
    }
}
