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

data class FarmPlanDay(
    val dayNumber: Int,
    val dayLabel: String,
    val dateString: String,
    val title: String,
    val category: String,
    val priority: String,
    val priorityColor: Color,
    val description: String,
    val completed: Boolean,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmPlanScreen(
    strings: AppStrings,
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var planDays by remember {
        mutableStateOf(
            listOf(
                FarmPlanDay(
                    dayNumber = 1,
                    dayLabel = "Today",
                    dateString = "15 Sep",
                    title = "Execute 45-Min Drip Irrigation in Plot 1",
                    category = "Irrigation",
                    priority = "HIGH",
                    priorityColor = Color(0xFFDC2626),
                    description = "Soil moisture is currently at 29% (target 50%). Run drip line after 5:00 PM to minimize evaporation.",
                    completed = false,
                    icon = Icons.Default.WaterDrop
                ),
                FarmPlanDay(
                    dayNumber = 2,
                    dayLabel = "Tomorrow",
                    dateString = "16 Sep",
                    title = "Early Morning Leaf Inspection & Fungicide Spray",
                    category = "Crop Health",
                    priority = "MEDIUM",
                    priorityColor = KisanEmerald,
                    description = "Inspect lower leaves for Early Blight spots. Apply preventive organic Trichoderma foliar spray before 9:00 AM.",
                    completed = false,
                    icon = Icons.Default.CameraAlt
                ),
                FarmPlanDay(
                    dayNumber = 3,
                    dayLabel = "Day 3",
                    dateString = "17 Sep",
                    title = "Foliar Potassium & Boron Application",
                    category = "Nutrition",
                    priority = "MEDIUM",
                    priorityColor = KisanHarvestGold,
                    description = "Spray 0:0:50 fertilizer @ 5g/L to support uniform flowering and fruit enlargement in tomato crop.",
                    completed = false,
                    icon = Icons.Default.Science
                ),
                FarmPlanDay(
                    dayNumber = 4,
                    dayLabel = "Day 4",
                    dateString = "18 Sep",
                    title = "Field Drainage & Moisture Check",
                    category = "Weather",
                    priority = "LOW",
                    priorityColor = Color(0xFF2563EB),
                    description = "Review cloud cover and forecast. Inspect drainage channels to ensure no water logging in low-lying furrow edges.",
                    completed = false,
                    icon = Icons.Default.WbSunny
                ),
                FarmPlanDay(
                    dayNumber = 5,
                    dayLabel = "Day 5",
                    dateString = "19 Sep",
                    title = "Surat & Navsari APMC Mandi Rate Evaluation",
                    category = "Market",
                    priority = "HIGH",
                    priorityColor = Color(0xFF0D9488),
                    description = "Compare mandi price movements. Harvest ripe batches if modal rate holds above ₹2,400/quintal.",
                    completed = false,
                    icon = Icons.Default.Storefront
                ),
                FarmPlanDay(
                    dayNumber = 6,
                    dayLabel = "Day 6",
                    dateString = "20 Sep",
                    title = "Satellite Canopy Comparison Review",
                    category = "Satellite",
                    priority = "LOW",
                    priorityColor = KisanEmerald,
                    description = "Check newly refreshed Sentinel-2 imagery for canopy density recovery in the North-East quadrant.",
                    completed = false,
                    icon = Icons.Default.SatelliteAlt
                ),
                FarmPlanDay(
                    dayNumber = 7,
                    dayLabel = "Day 7",
                    dateString = "21 Sep",
                    title = "Weekly Yield Gap & Health Assessment",
                    category = "Review",
                    priority = "MEDIUM",
                    priorityColor = KisanCharcoal,
                    description = "Record flowering density and fruit count per plant. Calibrate on-ground estimates against 3.6 t/acre yield target.",
                    completed = false,
                    icon = Icons.Default.CheckCircle
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "7-Day Farm Plan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Dynamic Schedule • Weather & Crop Stage Aligned",
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
                .testTag("farm_plan_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(planDays) { day ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = day.priorityColor.copy(alpha = 0.12f),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = day.icon, contentDescription = null, tint = day.priorityColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${day.dayLabel} • ${day.dateString}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KisanMutedSage
                                    )
                                    Text(
                                        text = day.category,
                                        fontSize = 11.sp,
                                        color = day.priorityColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Checkbox(
                                checked = day.completed,
                                onCheckedChange = { isChecked ->
                                    planDays = planDays.map {
                                        if (it.dayNumber == day.dayNumber) it.copy(completed = isChecked) else it
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = KisanEmerald)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (day.completed) KisanMutedSage else KisanCharcoal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = day.description,
                            fontSize = 12.sp,
                            color = KisanMutedSage,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
