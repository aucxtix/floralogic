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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.presentation.theme.*

data class YieldActionPlanItem(
    val rank: Int,
    val title: String,
    val impact: String,
    val impactGain: String,
    val impactColor: Color,
    val why: String,
    val what: String,
    val whenToAct: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YieldIntelligenceScreen(
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToIrrigation: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val actionPlan = remember {
        listOf(
            YieldActionPlanItem(
                rank = 1,
                title = "Improve Soil Moisture to 50% via Drip Irrigation",
                impact = "HIGH IMPACT",
                impactGain = "+0.30 t/acre",
                impactColor = Color(0xFF16A34A),
                why = "Water stress (29% soil moisture) during early flowering causes flower abortion and stunted fruit set.",
                what = "Run drip irrigation cycle 2 for 45 minutes in Plot 1 to restore moisture to target 50-60%.",
                whenToAct = "Today evening (after 5:00 PM)"
            ),
            YieldActionPlanItem(
                rank = 2,
                title = "Prevent Fungal Early Blight Infection",
                impact = "MEDIUM IMPACT",
                impactGain = "+0.15 t/acre",
                impactColor = KisanEmerald,
                why = "Morning dew and high night humidity favor spore germination on lower canopy foliage.",
                what = "Apply preventative foliar spray of Trichoderma harzianum or Mancozeb 75 WP @ 2g/L.",
                whenToAct = "Tomorrow morning (before 9:00 AM)"
            ),
            YieldActionPlanItem(
                rank = 3,
                title = "Foliar Potassium & Micronutrient Boost",
                impact = "MEDIUM IMPACT",
                impactGain = "+0.15 t/acre",
                impactColor = KisanHarvestGold,
                why = "Flowering-to-fruit transition requires high Potassium and Boron for pollen viability and fruit sizing.",
                what = "Spray 0:0:50 (Potassium Sulfate) @ 5g/L + Boron 20% @ 1g/L on tomato canopy.",
                whenToAct = "In 4 days (after irrigation cycle)"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Yield Intelligence",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Crop: Tomato • Green Valley Farm (Plot 1)",
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
                .testTag("yield_intelligence_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Metric Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = KisanDeepForest),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("YIELD POTENTIAL FORECAST", fontSize = 11.sp, color = KisanEmeraldLight, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KisanEmerald.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "CONFIDENCE: 85%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Current Expected Yield", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = "3.1 t/acre",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Potential Target", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = "3.6 t/acre",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KisanHarvestGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Yield Gap: -14% (-0.5 t/ac)", fontSize = 12.sp, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
                            Text("Recoverable: +0.60 t/ac", fontSize = 12.sp, color = KisanEmeraldLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Key Limiting Factors
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PRIMARY LIMITING FACTORS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LimitingFactorRow("1. Soil Moisture Stress (29%)", "Impact: -8% yield loss", Color(0xFFDC2626))
                        LimitingFactorRow("2. Early Blight Spore Pressure", "Impact: -4% risk factor", Color(0xFFE11D48))
                        LimitingFactorRow("3. Nitrogen & Boron Uptake Timing", "Impact: -2% potential loss", Color(0xFFB45309))
                    }
                }
            }

            // High-Yield Action Plan (Ranked Actions)
            item {
                Text(
                    text = "High-Yield Action Plan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KisanCharcoal,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(actionPlan) { action ->
                YieldActionCard(
                    action = action,
                    onExecute = {
                        if (action.rank == 1) onNavigateToIrrigation() else onNavigateToScan()
                    }
                )
            }
        }
    }
}

@Composable
private fun LimitingFactorRow(title: String, impact: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
        Text(impact, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun YieldActionCard(
    action: YieldActionPlanItem,
    onExecute: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
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
                        color = action.impactColor,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${action.rank}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = action.impact,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = action.impactColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = action.impactGain,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(action.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)

            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text("WHY:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                Text(action.why, fontSize = 12.sp, color = KisanCharcoal)
                Spacer(modifier = Modifier.height(4.dp))
                Text("WHAT TO DO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                Text(action.what, fontSize = 12.sp, color = KisanCharcoal)
                Spacer(modifier = Modifier.height(4.dp))
                Text("TIMING: ${action.whenToAct}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanDeepForest)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onExecute,
                colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Execute Recommendation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
