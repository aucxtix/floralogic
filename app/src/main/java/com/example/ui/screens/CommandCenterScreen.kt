package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.api.MarketPriceDto
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import com.example.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandCenterScreen(
    farmerProfile: FarmerProfile,
    weather: WeatherInfo,
    marketPrice: MarketPriceDto?,
    strings: AppStrings,
    activeFarmName: String = "Green Valley Farm – Tomato",
    onOpenDrawer: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNavigateToMarket: () -> Unit = {},
    onNavigateToCropHealth: () -> Unit = {},
    onNavigateToYield: () -> Unit = {},
    onNavigateToIrrigation: () -> Unit = {},
    onNavigateToWeather: () -> Unit = {},
    onNavigateToSatellite: () -> Unit = {},
    onNavigateToHardware: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    onNavigateToCopilot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDiagnosisBreakdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Command Center",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Complete Operational Overview",
                            fontSize = 11.sp,
                            color = KisanMutedSage
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = KisanCharcoal
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("command_center_hamburger_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Command Center Menu",
                            tint = KisanEmerald
                        )
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
                .testTag("command_center_list"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // -------------------------------------------------------------
            // 1. GREETING & ACTIVE FARM CONTEXT
            // -------------------------------------------------------------
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
                            Column {
                                Text(
                                    text = "KISSAN AI COMMAND",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KisanEmeraldLight,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Good Morning, ${farmerProfile.name.split(" ").firstOrNull() ?: farmerProfile.name}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = KisanEmerald.copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(Color(0xFF22C55E), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "ALL SYSTEMS LIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Active Farm:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = activeFarmName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = KisanEmerald,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "82",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Farm Health", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text("82/100", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanEmeraldLight)
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 2. QUICK ACTIONS
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommandQuickAction(
                        icon = Icons.Default.CameraAlt,
                        label = "Scan Crop",
                        color = KisanEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToScan
                    )
                    CommandQuickAction(
                        icon = Icons.Default.Storefront,
                        label = "Check Market",
                        color = Color(0xFF0D9488),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMarket
                    )
                    CommandQuickAction(
                        icon = Icons.Default.WaterDrop,
                        label = "Irrigation",
                        color = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToIrrigation
                    )
                    CommandQuickAction(
                        icon = Icons.Default.Psychology,
                        label = "Ask AI",
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCopilot
                    )
                }
            }

            // -------------------------------------------------------------
            // 3. TODAY'S PRIORITIES (Section 42)
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TODAY'S PRIORITIES",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KisanCharcoal
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEE2E2)
                            ) {
                                Text(
                                    text = "3 ACTIONS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        PriorityRow(
                            tag = "WATER STRESS",
                            tagColor = Color(0xFF2563EB),
                            title = "Soil Moisture Critical: 29% in Plot 1",
                            actionLabel = "Irrigate",
                            onClick = onNavigateToIrrigation
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PriorityRow(
                            tag = "DISEASE WATCH",
                            tagColor = Color(0xFFDC2626),
                            title = "Favorable weather for Early Blight",
                            actionLabel = "Inspect & Scan",
                            onClick = onNavigateToScan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PriorityRow(
                            tag = "MARKET JUMP",
                            tagColor = Color(0xFF16A34A),
                            title = "APMC Mandi Price up +6.2% today",
                            actionLabel = "Review Price",
                            onClick = onNavigateToMarket
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // 4. CURRENT MARKET PRICE (Section 6 & 7 & 42)
            // -------------------------------------------------------------
            item {
                val modal = marketPrice?.modalPrice ?: 2450.0
                val min = marketPrice?.minPrice ?: 2100.0
                val max = marketPrice?.maxPrice ?: 2800.0
                val isMock = marketPrice?.source?.contains("MOCK", ignoreCase = true) != false

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
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = KisanEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CURRENT MARKET PRICE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KisanCharcoal
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isMock) Color(0xFFFEF3C7) else KisanEmerald.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (isMock) "DEMO MARKET DATA" else "LIVE e-NAM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMock) Color(0xFFB45309) else KisanEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Commodity: Tomato",
                                    fontSize = 12.sp,
                                    color = KisanMutedSage
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "₹${modal.toInt()}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KisanCharcoal
                                    )
                                    Text(
                                        text = " / quintal",
                                        fontSize = 13.sp,
                                        color = KisanMutedSage,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                                    )
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
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+6.2%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Range details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatItem(label = "Nearby Mandi", value = "Surat APMC")
                            StatItem(label = "Min", value = "₹${min.toInt()}")
                            StatItem(label = "Max", value = "₹${max.toInt()}")
                            StatItem(label = "7-Day", value = "↑ Increasing")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Updated: Today, 2:15 PM • e-NAM Agmarknet",
                                fontSize = 11.sp,
                                color = KisanMutedSage
                            )
                            Button(
                                onClick = onNavigateToMarket,
                                colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("View Market", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 5. CROP HEALTH & YIELD ROW
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Crop Health Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("CROP HEALTH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Icon(Icons.Default.Eco, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("86%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                            Text("Flowering Stage", fontSize = 11.sp, color = KisanCharcoal)
                            Text("Risk: Medium", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onNavigateToCropHealth,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("View Crop", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                            }
                        }
                    }

                    // Yield Prediction Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("YIELD ESTIMATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = KisanHarvestGold, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("3.1 t/ac", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            Text("Potential: 3.6 t/ac", fontSize = 11.sp, color = KisanMutedSage)
                            Text("Gap: 14% (Water)", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onNavigateToYield,
                                colors = ButtonDefaults.buttonColors(containerColor = KisanHarvestGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Improve Yield", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 6. IRRIGATION & WEATHER ROW
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Irrigation Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("IRRIGATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Soil: 29%", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            Text("Action: IRRIGATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Text("Target: 45–65%", fontSize = 11.sp, color = KisanMutedSage)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onNavigateToIrrigation,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("View Advisory", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                        }
                    }

                    // Weather Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("WEATHER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("${weather.temperatureC}°C", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            Text("Rain Chance: 20%", fontSize = 11.sp, color = KisanMutedSage)
                            Text("Humidity: ${weather.humidityPercent}%", fontSize = 11.sp, color = KisanMutedSage)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onNavigateToWeather,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("View Weather", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 7. SATELLITE & SMART FARM HARDWARE ROW
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Satellite Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("SATELLITE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Icon(Icons.Default.SatelliteAlt, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Veg: 78%", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                            Text("NDVI 0.78 Healthy", fontSize = 11.sp, color = KisanDeepForest, fontWeight = FontWeight.SemiBold)
                            Text("Sentinel-2 Pass", fontSize = 11.sp, color = KisanMutedSage)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onNavigateToSatellite,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Explore Map", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                            }
                        }
                    }

                    // Smart Farm IoT Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("SMART FARM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanMutedSage)
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF22C55E), CircleShape))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("ESP32 #001", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            Text("Status: ONLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            Text("Moisture: 43% | 28°C", fontSize = 11.sp, color = KisanMutedSage)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onNavigateToHardware,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("View Sensors", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanDeepForest)
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // 8. RECENT FARM ACTIVITY (Section 42)
            // -------------------------------------------------------------
            item {
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
                            Text(
                                text = "RECENT FARM ACTIVITY",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = KisanCharcoal
                            )
                            TextButton(onClick = onNavigateToHistory) {
                                Text("View All", fontSize = 12.sp, color = KisanEmerald, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        ActivityTimelineRow(
                            icon = Icons.Default.CameraAlt,
                            color = KisanEmerald,
                            title = "Disease Scan: Tomato Leaf",
                            time = "Today, 10:45 AM",
                            status = "Healthy (89% Confidence)"
                        )
                        ActivityTimelineRow(
                            icon = Icons.Default.Storefront,
                            color = Color(0xFF0D9488),
                            title = "Market Check: Surat APMC Mandi",
                            time = "Today, 09:15 AM",
                            status = "₹2,450/q (+6.2%)"
                        )
                        ActivityTimelineRow(
                            icon = Icons.Default.WaterDrop,
                            color = Color(0xFF2563EB),
                            title = "Irrigation Valve Cycle (Plot 1)",
                            time = "Yesterday, 06:30 PM",
                            status = "Completed (45 mins)"
                        )
                        ActivityTimelineRow(
                            icon = Icons.Default.Sensors,
                            color = Color(0xFF16A34A),
                            title = "ESP32 Sensor Telemetry Sync",
                            time = "12 seconds ago",
                            status = "Online • Valid"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandQuickAction(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = KisanCharcoal
            )
        }
    }
}

@Composable
private fun PriorityRow(
    tag: String,
    tagColor: Color,
    title: String,
    actionLabel: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = tagColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = tag,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = KisanCharcoal
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = actionLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = KisanEmerald
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = KisanEmerald,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = KisanMutedSage)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
    }
}

@Composable
private fun ActivityTimelineRow(
    icon: ImageVector,
    color: Color,
    title: String,
    time: String,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.12f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
            Text(text = time, fontSize = 10.sp, color = KisanMutedSage)
        }
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = KisanDeepForest
        )
    }
}
