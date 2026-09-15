package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ai.CombinedIntelligence
import com.example.data.local.FarmCropEntity
import com.example.data.local.ScanRecordEntity
import com.example.data.model.AppStrings
import com.example.data.model.LocalAppStrings
import com.example.data.model.FarmerProfile
import com.example.data.model.WeatherInfo
import com.example.presentation.theme.*

@Composable
fun HomeScreen(
    strings: AppStrings,
    farmerProfile: FarmerProfile,
    weather: WeatherInfo,
    crops: List<FarmCropEntity> = emptyList(),
    recentScans: List<ScanRecordEntity> = emptyList(),
    combinedIntelligence: CombinedIntelligence? = null,
    isIntelligenceLoading: Boolean = false,
    onAskCopilot: (String, (String) -> Unit) -> Unit = { _, _ -> },
    onNavigateToScan: () -> Unit = {},
    onNavigateToFarm: () -> Unit = {},
    onNavigateToWeather: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToMarket: () -> Unit = {},
    onNavigateToHardware: () -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onOpenFilter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFarmName by remember { mutableStateOf(farmerProfile.farmName.ifEmpty { "Green Valley Farm" }) }
    var showFarmSelectorDialog by remember { mutableStateOf(false) }

    val registeredPlots = remember {
        listOf(
            PlotInfo(
                name = "Green Valley Farm (Plot 1)",
                crop = "Tomato",
                area = "2.5 Acres",
                healthScore = 86,
                moisture = 31,
                diseaseRisk = "Low",
                yield = "3.1 t/ac",
                status = "Healthy"
            ),
            PlotInfo(
                name = "Sardar Patel Agro (Plot 2)",
                crop = "Cotton",
                area = "4.0 Acres",
                healthScore = 81,
                moisture = 38,
                diseaseRisk = "Low",
                yield = "1.8 t/ac",
                status = "Healthy"
            )
        )
    }

    if (showFarmSelectorDialog) {
        FarmSelectorModal(
            currentFarm = selectedFarmName,
            plots = registeredPlots,
            onSelectFarm = {
                selectedFarmName = it
                showFarmSelectorDialog = false
            },
            onAddNewPlot = {
                showFarmSelectorDialog = false
                onNavigateToFarm()
            },
            onDismiss = { showFarmSelectorDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KisanWarmIvory)
            .padding(horizontal = 16.dp)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Section (Farmer greeting, location, weather, farm selector)
        item {
            FarmerHeaderSection(
                farmerProfile = farmerProfile,
                selectedFarm = selectedFarmName,
                weather = weather,
                onFarmSelectorClick = { showFarmSelectorDialog = true },
                onProfileClick = onProfileClick,
                onOpenDrawer = onOpenDrawer,
                onNavigateToAlerts = onNavigateToAlerts
            )
        }

        // 2. Farm Health Hero Card (Score 82/100, Breakdown, Expandable "Why?")
        item {
            FarmHealthHeroCard(
                healthScore = combinedIntelligence?.briefing?.healthScore ?: 82,
                trendDelta = "+4% from last week",
                cropScore = 86,
                waterScore = 71,
                diseaseScore = 78,
                weatherScore = 84,
                soilScore = 80
            )
        }

        // 3. Today's Priorities (Max 3 actionable cards)
        item {
            TodayPrioritiesSection(
                onNavigateToIrrigation = onNavigateToWeather,
                onNavigateToScan = onNavigateToScan,
                onNavigateToMarket = onNavigateToMarket
            )
        }

        // 4. AI Farm Copilot (Proactive Q&A with structured responses)
        item {
            AiFarmCopilotCard(
                onAskCopilot = onAskCopilot
            )
        }

        // 5. AI Scan CTA Banner
        item {
            AiScanCtaBanner(
                onLaunchCamera = onNavigateToScan,
                onOpenGallery = onNavigateToScan
            )
        }

        // 6. Dedicated Crop Health Section
        item {
            CropHealthSection(
                cropName = farmerProfile.primaryCrop.ifEmpty { "Tomato" },
                stage = "Flowering Stage (Day 42)",
                healthPercent = 86,
                diseaseRisk = "Medium",
                yieldPotential = 84,
                onViewCropDetails = onNavigateToFarm
            )
        }

        // 7. Registered Plots / Multi-Farm Intelligence Cards
        item {
            RegisteredPlotsSection(
                plots = registeredPlots,
                onViewPlot = onNavigateToFarm,
                onAddPlot = onNavigateToFarm
            )
        }

        // 8. Weather + Irrigation Card
        item {
            WeatherIrrigationCard(
                temperature = weather.temperatureC,
                condition = weather.condition,
                rainProbability = 20,
                soilMoisture = 29,
                targetMoistureRange = "40% - 55%",
                advisory = "IRRIGATION RECOMMENDED",
                advisoryReason = "Rain probability is low and soil moisture is below target for current flowering stage.",
                onViewIrrigation = onNavigateToWeather
            )
        }

        // 9. Disease Risk Card ("Disease Watch")
        item {
            DiseaseWatchCard(
                riskLevel = "MEDIUM",
                humidity = "78% (High)",
                recentRain = "Light Dew / Showers",
                stage = "Flowering",
                actionRecommendation = "Inspect lower foliage for concentric brown spots.",
                onScanCrop = onNavigateToScan
            )
        }

        // 10. Yield Intelligence Card ("Yield Outlook")
        item {
            YieldOutlookCard(
                expectedYield = "3.1 t/acre",
                potentialYield = "3.6 t/acre",
                yieldGap = "14%",
                mainLimitation = "Water stress during early flowering",
                onHowToImprove = {
                    onAskCopilot("How can I close my 14% tomato yield gap?") { _ -> }
                }
            )
        }

        // 11. Market Intelligence Card ("Market Today")
        item {
            MarketTodayCard(
                cropName = farmerProfile.primaryCrop.ifEmpty { "Tomato" },
                mandiName = "Surat APMC Mandi",
                modalPrice = "₹2,450 / quintal",
                priceChange = "+6.2%",
                outlook = "Moderately Increasing (7-Day Forecast)",
                isLive = false,
                lastUpdated = "18 min ago",
                onViewMarket = onNavigateToMarket
            )
        }

        // 12. Satellite Intelligence Card
        item {
            SatellitePreviewCard(
                farmName = selectedFarmName,
                vegetationHealth = "78%",
                trend = "↑ Improving",
                lastImage = "3 days ago (Sentinel-2)",
                summary = "Vegetation index (NDVI 0.78) confirms uniform canopy density across 88% of plot.",
                onExploreSatellite = onNavigateToFarm
            )
        }

        // 13. Smart Farm Hardware Card (ESP32)
        item {
            SmartFarmHardwareCard(
                deviceName = "ESP32 Farm Monitor #001",
                isOnline = true,
                soilMoisture = 43,
                temperature = 28.4,
                humidity = 67,
                battery = 92,
                lastUpdate = "12 sec ago",
                onViewSensors = onNavigateToHardware
            )
        }

        // 14. Recent Activity Timeline
        item {
            RecentActivityTimeline(
                recentScans = recentScans,
                onNavigateToScan = onNavigateToScan,
                onNavigateToHistory = onNavigateToHistory
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// DATA CLASSES
// -----------------------------------------------------------------------------------------

data class PlotInfo(
    val name: String,
    val crop: String,
    val area: String,
    val healthScore: Int,
    val moisture: Int,
    val diseaseRisk: String,
    val yield: String,
    val status: String
)

// -----------------------------------------------------------------------------------------
// COMPONENT 1: FARMER HEADER
// -----------------------------------------------------------------------------------------

@Composable
fun FarmerHeaderSection(
    farmerProfile: FarmerProfile,
    selectedFarm: String,
    weather: WeatherInfo,
    onFarmSelectorClick: () -> Unit,
    onProfileClick: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onNavigateToAlerts: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    val firstName = farmerProfile.name.split(" ").firstOrNull() ?: farmerProfile.name

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth().testTag("farmer_header_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${strings.greetingNamaste}, $firstName 🙏",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanCharcoal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onFarmSelectorClick() }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = selectedFarm,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KisanEmerald
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Farm",
                            tint = KisanEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // System Notification Bell
                    Box {
                        IconButton(
                            onClick = onNavigateToAlerts,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .testTag("home_alerts_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Farm Alerts",
                                tint = KisanEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .align(Alignment.TopEnd)
                                .background(Color(0xFFDC2626), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Hamburger Menu Button
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .testTag("home_hamburger_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Command Center Menu",
                            tint = KisanEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Avatar / Profile Pill
                    Surface(
                        shape = CircleShape,
                        color = KisanEmeraldLight,
                        border = BorderStroke(1.5.dp, KisanEmerald),
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable { onProfileClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = firstName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = KisanDeepForest
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Location & Weather Quick Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = KisanMutedSage,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${farmerProfile.village}, ${farmerProfile.state}",
                        fontSize = 12.sp,
                        color = KisanMutedSage
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF0FDF4)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = KisanHarvestGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${weather.temperatureC}°C • ${weather.condition}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KisanDeepForest
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 2: FARM HEALTH HERO CARD (Score, Breakdown, "Why?")
// -----------------------------------------------------------------------------------------

@Composable
fun FarmHealthHeroCard(
    healthScore: Int,
    trendDelta: String,
    cropScore: Int,
    waterScore: Int,
    diseaseScore: Int,
    weatherScore: Int,
    soilScore: Int
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("farm_health_hero_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Label & Trend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Eco,
                        contentDescription = null,
                        tint = KisanEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FARM HEALTH",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = KisanCharcoal
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = trendDelta,
                        color = Color(0xFF15803D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score & Main Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Ring Score
                Surface(
                    shape = CircleShape,
                    color = KisanEmeraldLight,
                    border = BorderStroke(3.dp, KisanEmerald),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$healthScore",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = KisanDeepForest
                            )
                            Text(
                                text = "/100",
                                fontSize = 10.sp,
                                color = KisanMutedSage,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = KisanEmerald
                    ) {
                        Text(
                            text = "HEALTHY CONDITION",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your crop canopy and vegetative health are performing well today.",
                        fontSize = 13.sp,
                        color = KisanCharcoal.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5 Breakdown Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HealthMetricPill(label = "Crop", score = cropScore)
                HealthMetricPill(label = "Water", score = waterScore)
                HealthMetricPill(label = "Disease", score = diseaseScore)
                HealthMetricPill(label = "Weather", score = weatherScore)
                HealthMetricPill(label = "Soil", score = soilScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable "Why?" Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isExpanded = !isExpanded }
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = KisanEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExpanded) "Hide explanation" else "Why 82/100? View diagnosis",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KisanEmerald
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = KisanEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Positive Factors (+):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF15803D)
                    )
                    Text("• Dense canopy index (NDVI 0.78) indicates robust vegetative growth.", fontSize = 12.sp, color = KisanCharcoal)
                    Text("• Ambient temperature (28.4°C) is within the optimal metabolic range.", fontSize = 12.sp, color = KisanCharcoal)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Risk Factors to Monitor (-):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFEA580C)
                    )
                    Text("• Soil moisture at 28% is below the target 40% flowering threshold.", fontSize = 12.sp, color = KisanCharcoal)
                    Text("• High morning humidity (78%) elevates Early Blight fungal risk.", fontSize = 12.sp, color = KisanCharcoal)
                }
            }
        }
    }
}

@Composable
fun HealthMetricPill(label: String, score: Int) {
    val pillColor = when {
        score >= 80 -> Color(0xFF16A34A)
        score >= 65 -> KisanHarvestGold
        else -> Color(0xFFDC2626)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(56.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = KisanMutedSage, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = pillColor.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "$score",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = pillColor
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 3: TODAY'S PRIORITIES (Max 3 Actionable Cards)
// -----------------------------------------------------------------------------------------

@Composable
fun TodayPrioritiesSection(
    onNavigateToIrrigation: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToMarket: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.todayPrioritiesTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
            )
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "3 Actions",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Priority 1: Water Stress
        PriorityActionCard(
            priorityLabel = "HIGH PRIORITY",
            priorityColor = Color(0xFFEA580C),
            priorityContainerColor = Color(0xFFFFEDD5),
            title = "${strings.waterStress} • Plot 1",
            description = "Soil moisture is 28%. Rain probability is low (20%). Drip cycle required today.",
            actionLabel = strings.actionOpenIrrigation,
            onAction = onNavigateToIrrigation,
            icon = Icons.Default.WaterDrop
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Priority 2: Disease Risk
        PriorityActionCard(
            priorityLabel = "MEDIUM PRIORITY",
            priorityColor = Color(0xFFD97706),
            priorityContainerColor = Color(0xFFFEF3C7),
            title = "${strings.diseaseWatch} • Early Blight",
            description = "Atmospheric humidity is 78% in flowering stage. Inspect lower foliage for brown spots.",
            actionLabel = strings.actionTakeScan,
            onAction = onNavigateToScan,
            icon = Icons.Default.Coronavirus
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Priority 3: Market Opportunity
        PriorityActionCard(
            priorityLabel = "MARKET UPDATE",
            priorityColor = Color(0xFF2563EB),
            priorityContainerColor = Color(0xFFDBEAFE),
            title = "Price Surge +6.2% at Mandi",
            description = "Tomato modal rate is ₹2,450/q at Surat APMC. 7-day outlook indicates steady holding.",
            actionLabel = strings.actionOpenMarket,
            onAction = onNavigateToMarket,
            icon = Icons.AutoMirrored.Filled.TrendingUp
        )
    }
}

@Composable
fun PriorityActionCard(
    priorityLabel: String,
    priorityColor: Color,
    priorityContainerColor: Color,
    title: String,
    description: String,
    actionLabel: String,
    onAction: () -> Unit,
    icon: ImageVector
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = priorityContainerColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = priorityColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = priorityContainerColor
                ) {
                    Text(
                        text = priorityLabel,
                        color = priorityColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = KisanCharcoal
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = KisanMutedSage,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 4: AI FARM COPILOT
// -----------------------------------------------------------------------------------------

@Composable
fun AiFarmCopilotCard(
    onAskCopilot: (String, (String) -> Unit) -> Unit
) {
    var queryText by remember { mutableStateOf("") }
    var copilotResponse by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val promptSuggestions = listOf(
        "Paani kab dena hai?",
        "Fasal ki condition kaisi hai?",
        "Disease ka risk hai?",
        "Aaj ka mandi bhav?",
        "Yield kaise badhau?"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("ai_farm_copilot_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = KisanEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Farm Copilot",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KisanCharcoal
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = KisanEmeraldLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Agricultural AI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggestion chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(promptSuggestions) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.clickable {
                            queryText = prompt
                            isLoading = true
                            copilotResponse = null
                            onAskCopilot(prompt) { res ->
                                copilotResponse = res
                                isLoading = false
                            }
                        }
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = KisanCharcoal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = { Text("Ask anything about your crop, soil, mandi...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KisanEmerald,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (queryText.isNotBlank()) {
                            isLoading = true
                            copilotResponse = null
                            onAskCopilot(queryText) { res ->
                                copilotResponse = res
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(KisanEmerald),
                    enabled = !isLoading && queryText.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Ask", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Structured 4-Part AI Response
            if (copilotResponse != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kissan AI Recommendation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KisanDeepForest)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = copilotResponse ?: "",
                            fontSize = 13.sp,
                            color = KisanCharcoal,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 5: AI SCAN CTA BANNER
// -----------------------------------------------------------------------------------------

@Composable
fun AiScanCtaBanner(
    onLaunchCamera: () -> Unit,
    onOpenGallery: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = KisanDeepForest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("ai_scan_cta_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = KisanEmerald,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scan My Crop",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Instant leaf disease diagnosis & organic remedies",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onLaunchCamera,
                        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Camera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenGallery,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Gallery", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 6: CROP HEALTH SECTION
// -----------------------------------------------------------------------------------------

@Composable
fun CropHealthSection(
    cropName: String,
    stage: String,
    healthPercent: Int,
    diseaseRisk: String,
    yieldPotential: Int,
    onViewCropDetails: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.Default.Grass, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Crop Health", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }
                TextButton(onClick = onViewCropDetails, contentPadding = PaddingValues(0.dp)) {
                    Text("View Crop Details", fontSize = 12.sp, color = KisanEmerald, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Crop", fontSize = 11.sp, color = KisanMutedSage)
                    Text(cropName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                    Text(stage, fontSize = 11.sp, color = KisanEmerald, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Canopy Health", fontSize = 11.sp, color = KisanMutedSage)
                    Text("$healthPercent% (↑ Good)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    Text("Disease Risk: $diseaseRisk", fontSize = 11.sp, color = KisanHarvestGold, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 7: REGISTERED PLOTS / MULTI-FARM
// -----------------------------------------------------------------------------------------

@Composable
fun RegisteredPlotsSection(
    plots: List<PlotInfo>,
    onViewPlot: () -> Unit,
    onAddPlot: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Registered Plots", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
            TextButton(onClick = onAddPlot, contentPadding = PaddingValues(0.dp)) {
                Text("+ Add Plot", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        plots.forEach { plot ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(plot.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                            Text("${plot.crop} • ${plot.area}", fontSize = 12.sp, color = KisanMutedSage)
                        }
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(plot.status, color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricMiniItem(label = "Health", value = "${plot.healthScore}%")
                        MetricMiniItem(label = "Moisture", value = "${plot.moisture}%")
                        MetricMiniItem(label = "Disease Risk", value = plot.diseaseRisk)
                        MetricMiniItem(label = "Yield Est.", value = plot.yield)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onViewPlot,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, KisanEmeraldLight),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("View Farm Intelligence", fontSize = 12.sp, color = KisanEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricMiniItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = KisanMutedSage)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 8: WEATHER + IRRIGATION CARD
// -----------------------------------------------------------------------------------------

@Composable
fun WeatherIrrigationCard(
    temperature: Int,
    condition: String,
    rainProbability: Int,
    soilMoisture: Int,
    targetMoistureRange: String,
    advisory: String,
    advisoryReason: String,
    onViewIrrigation: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Weather & Irrigation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = advisory,
                        color = Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Weather", fontSize = 11.sp, color = KisanMutedSage)
                    Text("${temperature}°C • $condition", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                    Text("Rain Chance: $rainProbability%", fontSize = 11.sp, color = KisanMutedSage)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Soil Moisture", fontSize = 11.sp, color = KisanMutedSage)
                    Text("$soilMoisture%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    Text("Target: $targetMoistureRange", fontSize = 11.sp, color = KisanMutedSage)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = advisoryReason,
                    fontSize = 12.sp,
                    color = KisanCharcoal,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onViewIrrigation,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("View Irrigation Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 9: DISEASE WATCH CARD
// -----------------------------------------------------------------------------------------

@Composable
fun DiseaseWatchCard(
    riskLevel: String,
    humidity: String,
    recentRain: String,
    stage: String,
    actionRecommendation: String,
    onScanCrop: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.Default.Warning, contentDescription = null, tint = KisanHarvestGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Disease Watch", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "RISK: $riskLevel",
                        color = Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Environmental Triggers:", fontSize = 11.sp, color = KisanMutedSage, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("• Humidity: $humidity", fontSize = 12.sp, color = KisanCharcoal)
                Text("• Stage: $stage", fontSize = 12.sp, color = KisanCharcoal)
            }
            Text("• Recent Moisture: $recentRain", fontSize = 12.sp, color = KisanCharcoal)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Action: $actionRecommendation", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = KisanDeepForest)

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onScanCrop,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Crop Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 10: YIELD OUTLOOK CARD
// -----------------------------------------------------------------------------------------

@Composable
fun YieldOutlookCard(
    expectedYield: String,
    potentialYield: String,
    yieldGap: String,
    mainLimitation: String,
    onHowToImprove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Yield Outlook", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Yield Gap: $yieldGap",
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Expected Yield", fontSize = 11.sp, color = KisanMutedSage)
                    Text(expectedYield, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Potential Yield", fontSize = 11.sp, color = KisanMutedSage)
                    Text(potentialYield, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Main Limitation: $mainLimitation", fontSize = 12.sp, color = KisanMutedSage)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onHowToImprove,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, KisanEmerald),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text("How to close yield gap?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 11: MARKET TODAY CARD
// -----------------------------------------------------------------------------------------

@Composable
fun MarketTodayCard(
    cropName: String,
    mandiName: String,
    modalPrice: String,
    priceChange: String,
    outlook: String,
    isLive: Boolean,
    lastUpdated: String,
    onViewMarket: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Market Today", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }

                Surface(
                    color = if (isLive) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isLive) "LIVE e-NAM" else "DEMO MARKET DATA",
                        color = if (isLive) Color(0xFF15803D) else Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("$cropName • $mandiName", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                    Text("Updated $lastUpdated", fontSize = 11.sp, color = KisanMutedSage)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(modalPrice, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    Text(priceChange, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("7-Day Outlook: $outlook", fontSize = 12.sp, color = KisanCharcoal)

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onViewMarket,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Should I Sell?", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onViewMarket,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("View Mandi Rates", fontSize = 12.sp, color = KisanCharcoal)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 12: SATELLITE PREVIEW CARD
// -----------------------------------------------------------------------------------------

@Composable
fun SatellitePreviewCard(
    farmName: String,
    vegetationHealth: String,
    trend: String,
    lastImage: String,
    summary: String,
    onExploreSatellite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.Default.SatelliteAlt, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Satellite Intelligence", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }

                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        trend,
                        color = Color(0xFF15803D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Boundary & NDVI Simulation Visual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                // Simulated multi-zone farm plot
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(0.78f).fillMaxHeight().background(Color(0xFF22C55E).copy(alpha = 0.6f)))
                    Box(modifier = Modifier.weight(0.22f).fillMaxHeight().background(Color(0xFFEAB308).copy(alpha = 0.5f)))
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                ) {
                    Text(
                        text = "NDVI Health: $vegetationHealth • $lastImage",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = summary,
                fontSize = 12.sp,
                color = KisanCharcoal,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onExploreSatellite,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, KisanEmerald),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text("Explore Full Satellite Map", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 13: SMART FARM HARDWARE CARD
// -----------------------------------------------------------------------------------------

@Composable
fun SmartFarmHardwareCard(
    deviceName: String,
    isOnline: Boolean,
    soilMoisture: Int,
    temperature: Double,
    humidity: Int,
    battery: Int,
    lastUpdate: String,
    onViewSensors: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
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
                    Icon(Icons.Default.Sensors, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Smart Farm Hardware", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                }

                Surface(
                    color = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF16A34A) else Color(0xFFDC2626))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOnline) "ONLINE" else "OFFLINE",
                            color = if (isOnline) Color(0xFF15803D) else Color(0xFFDC2626),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$deviceName • Sync $lastUpdate",
                fontSize = 12.sp,
                color = KisanMutedSage
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricMiniItem(label = "Moisture", value = "$soilMoisture%")
                MetricMiniItem(label = "Soil Temp", value = "${temperature}°C")
                MetricMiniItem(label = "Humidity", value = "$humidity%")
                MetricMiniItem(label = "Battery", value = "$battery%")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewSensors,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, KisanEmerald),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text("View Live Telemetry", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// COMPONENT 14: RECENT ACTIVITY TIMELINE
// -----------------------------------------------------------------------------------------

@Composable
fun RecentActivityTimeline(
    recentScans: List<ScanRecordEntity>,
    onNavigateToScan: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Farm Activity", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
            TextButton(onClick = onNavigateToHistory, contentPadding = PaddingValues(0.dp)) {
                Text("View All", fontSize = 13.sp, color = KisanEmerald, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (recentScans.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScan() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = KisanEmerald)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("No recent disease scans", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
                        Text("Tap here to scan your first crop leaf", fontSize = 12.sp, color = KisanMutedSage)
                    }
                }
            }
        } else {
            val scan = recentScans.first()
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (scan.isHealthy) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (scan.isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (scan.isHealthy) Color(0xFF15803D) else Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(scan.diseaseName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                        Text("${scan.cropName} • Just now", fontSize = 12.sp, color = KisanMutedSage)
                    }

                    Surface(
                        color = if (scan.isHealthy) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (scan.isHealthy) "Healthy" else "Infected",
                            color = if (scan.isHealthy) Color(0xFF15803D) else Color(0xFFB45309),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// FARM SELECTOR MODAL
// -----------------------------------------------------------------------------------------

@Composable
fun FarmSelectorModal(
    currentFarm: String,
    plots: List<PlotInfo>,
    onSelectFarm: (String) -> Unit,
    onAddNewPlot: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Active Farm",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KisanCharcoal
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Switching farms instantly reloads weather, sensor feeds, and crop intelligence for that specific land parcel.",
                    fontSize = 12.sp,
                    color = KisanMutedSage
                )
                Spacer(modifier = Modifier.height(4.dp))
                plots.forEach { plot ->
                    val isSelected = currentFarm.contains(plot.name.split(" ").firstOrNull() ?: "")
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) KisanEmeraldLight else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isSelected) KisanEmerald else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectFarm(plot.name) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(plot.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KisanCharcoal)
                                Text("${plot.crop} • ${plot.area}", fontSize = 11.sp, color = KisanMutedSage)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KisanEmerald, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAddNewPlot,
                colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald)
            ) {
                Text("+ Add New Plot")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = KisanMutedSage)
            }
        }
    )
}
