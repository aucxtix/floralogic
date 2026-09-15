package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmAnalyticsScreen(
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf("30D") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Farm Analytics",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Long-Term Agronomic Trends & KPIs",
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
                .testTag("farm_analytics_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Range Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("7D", "30D", "90D", "1Y").forEach { range ->
                        val isSelected = selectedRange == range
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) KisanEmerald else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) KisanEmerald else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedRange = range }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = range,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else KisanCharcoal
                                )
                            }
                        }
                    }
                }
            }

            // Chart 1: Farm Health Trend
            item {
                AnalyticsCard(
                    title = "Farm Health Score Trend",
                    currentValue = "82 / 100",
                    delta = "+4% this month",
                    deltaColor = Color(0xFF16A34A),
                    lineColor = Color(0xFF10B981)
                )
            }

            // Chart 2: Soil Moisture & Irrigation Trend
            item {
                AnalyticsCard(
                    title = "Soil Moisture Depletion Curve",
                    currentValue = "29% (Depleted)",
                    delta = "-12% over 5 days",
                    deltaColor = Color(0xFFDC2626),
                    lineColor = Color(0xFF2563EB)
                )
            }

            // Chart 3: Market Price History
            item {
                AnalyticsCard(
                    title = "Mandi Price Trend (Tomato)",
                    currentValue = "₹2,450 / quintal",
                    delta = "+₹240/q (+6.2%)",
                    deltaColor = Color(0xFF16A34A),
                    lineColor = Color(0xFF0D9488)
                )
            }

            // Chart 4: Yield Gap Realization
            item {
                AnalyticsCard(
                    title = "Target vs Actual Biomass Yield",
                    currentValue = "3.1 / 3.6 t/acre",
                    delta = "86% Potential",
                    deltaColor = KisanHarvestGold,
                    lineColor = KisanHarvestGold
                )
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    currentValue: String,
    delta: String,
    deltaColor: Color,
    lineColor: Color
) {
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
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = deltaColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = delta,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = deltaColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(currentValue, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)

            Spacer(modifier = Modifier.height(10.dp))

            // Trend line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, h * 0.75f)
                        cubicTo(w * 0.25f, h * 0.85f, w * 0.5f, h * 0.45f, w * 0.75f, h * 0.55f)
                        lineTo(w, h * 0.25f)
                    }
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }
    }
}
