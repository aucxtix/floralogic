package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FarmCropEntity
import com.example.data.model.AppStrings
import com.example.presentation.theme.*

data class CropDetailedMetric(
    val cropName: String,
    val variety: String,
    val area: String,
    val sowingDate: String,
    val daysAfterSowing: Int,
    val growthStage: String,
    val healthScore: Int,
    val diseaseRisk: String,
    val expectedYield: String,
    val waterStatus: String,
    val healthTrend: String,
    val diseaseTrend: String,
    val yieldTrend: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHealthScreen(
    crops: List<FarmCropEntity> = emptyList(),
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    onNavigateToYield: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val detailedCrops = remember(crops) {
        if (crops.isEmpty()) {
            listOf(
                CropDetailedMetric(
                    cropName = "Tomato",
                    variety = "Abhinav F1 Hybrid",
                    area = "2.5 Acres (Plot 1)",
                    sowingDate = "15 Jan 2026",
                    daysAfterSowing = 42,
                    growthStage = "Flowering & Early Fruit Set",
                    healthScore = 86,
                    diseaseRisk = "Medium (Early Blight Watch)",
                    expectedYield = "3.1 t/acre",
                    waterStatus = "Water Stress (29% Soil Moisture)",
                    healthTrend = "↑ +3% over 7 days",
                    diseaseTrend = "Stable (No active blight)",
                    yieldTrend = "↑ Potential 3.6 t/acre"
                ),
                CropDetailedMetric(
                    cropName = "Cotton",
                    variety = "Bt RCH-659 Hybrid",
                    area = "4.0 Acres (Plot 2)",
                    sowingDate = "10 Dec 2025",
                    daysAfterSowing = 78,
                    growthStage = "Boll Formation Stage",
                    healthScore = 81,
                    diseaseRisk = "Low (Pest Scouting Clear)",
                    expectedYield = "1.8 t/acre",
                    waterStatus = "Adequate (38% Soil Moisture)",
                    healthTrend = "→ Consistent canopy",
                    diseaseTrend = "Low Risk (Dry Weather)",
                    yieldTrend = "→ Normal baseline"
                )
            )
        } else {
            crops.mapIndexed { idx, c ->
                CropDetailedMetric(
                    cropName = c.cropName,
                    variety = c.variety,
                    area = "${c.areaAcres} Acres",
                    sowingDate = c.sowingDate,
                    daysAfterSowing = 35 + (idx * 15),
                    growthStage = c.growthStage,
                    healthScore = 85 - (idx * 4),
                    diseaseRisk = "Low",
                    expectedYield = "3.0 t/acre",
                    waterStatus = "Optimal",
                    healthTrend = "↑ Improving",
                    diseaseTrend = "Low Risk",
                    yieldTrend = "↑ On Track"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Crop Health Intelligence",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "${detailedCrops.size} Registered Crops Monitored",
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
                .testTag("crop_health_screen_list"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KisanDeepForest),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CANOPY VIGOR", fontSize = 11.sp, color = KisanEmeraldLight, fontWeight = FontWeight.Bold)
                            Text("Average Health: 84%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Flowering stage active across 60% acreage", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Surface(
                            shape = CircleShape,
                            color = KisanEmerald,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            // Crops Detailed Breakdown
            items(detailedCrops) { crop ->
                CropDetailCard(
                    crop = crop,
                    onScanLeaf = onNavigateToScan,
                    onViewYield = onNavigateToYield
                )
            }
        }
    }
}

@Composable
private fun CropDetailCard(
    crop: CropDetailedMetric,
    onScanLeaf: () -> Unit,
    onViewYield: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(crop.cropName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                    Text("Variety: ${crop.variety} • ${crop.area}", fontSize = 12.sp, color = KisanMutedSage)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KisanEmerald.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "HEALTH: ${crop.healthScore}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stage and Timeline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Growth Stage", fontSize = 10.sp, color = KisanMutedSage)
                    Text(crop.growthStage, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Days After Sowing", fontSize = 10.sp, color = KisanMutedSage)
                    Text("${crop.daysAfterSowing} Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vital Status Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusPill("Disease Risk", crop.diseaseRisk, Color(0xFFDC2626))
                StatusPill("Water Status", crop.waterStatus, Color(0xFF2563EB))
                StatusPill("Est. Yield", crop.expectedYield, KisanHarvestGold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Trends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Health: ${crop.healthTrend}", fontSize = 11.sp, color = KisanDeepForest, fontWeight = FontWeight.Medium)
                Text("Yield: ${crop.yieldTrend}", fontSize = 11.sp, color = KisanHarvestGold, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onScanLeaf,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan Leaf", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                }
                Button(
                    onClick = onViewYield,
                    colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Yield Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, value: String, color: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = KisanMutedSage)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}
