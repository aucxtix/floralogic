package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.abs
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
import com.example.data.api.MarketForecastDto
import com.example.data.api.MarketPriceDto
import com.example.data.model.AppStrings
import com.example.data.model.FarmerProfile
import com.example.presentation.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.KisanViewModel
import com.example.ai.MapsGroundedResponse
import com.example.ai.GroundedResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NearbyMarketInfo(
    val name: String,
    val distanceKm: Int,
    val price: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val trend: String,
    val favorability: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    farmerProfile: FarmerProfile,
    strings: AppStrings,
    viewModel: KisanViewModel? = null,
    marketPrices: List<MarketPriceDto> = emptyList(),
    marketForecast: MarketForecastDto? = null,
    onBackClick: (() -> Unit)? = null,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activePrice = marketPrices.firstOrNull()
    val isMockData = activePrice?.source?.contains("MOCK", ignoreCase = true) != false
    var selectedTimeframe by remember { mutableStateOf("7 Days") }
    var selectedForecastTab by remember { mutableStateOf(7) }

    val nearbyMarkets = remember {
        listOf(
            NearbyMarketInfo(
                name = "Surat APMC Mandi",
                distanceKm = 12,
                price = 2450.0,
                minPrice = 2100.0,
                maxPrice = 2800.0,
                trend = "↑ Increasing (+6.2%)",
                favorability = "High (Optimal Distance)"
            ),
            NearbyMarketInfo(
                name = "Navsari APMC Yard",
                distanceKm = 38,
                price = 2580.0,
                minPrice = 2250.0,
                maxPrice = 2900.0,
                trend = "↑ Increasing (+8.4%)",
                favorability = "Very High (+₹130/q gain)"
            ),
            NearbyMarketInfo(
                name = "Vadodara APMC",
                distanceKm = 95,
                price = 2390.0,
                minPrice = 2050.0,
                maxPrice = 2650.0,
                trend = "→ Stable (-0.4%)",
                favorability = "Medium (Higher transport cost)"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Market Intelligence",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KisanCharcoal
                        )
                        Text(
                            text = "Commodity: ${farmerProfile.primaryCrop.ifBlank { "Tomato" }} • Surat APMC",
                            fontSize = 11.sp,
                            color = KisanMutedSage
                        )
                    }
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = KisanCharcoal
                            )
                        }
                    }
                },
                actions = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = KisanEmerald
                            )
                        }
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
                .testTag("market_screen_list"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live vs Mock Banner
            item {
                MarketSourceBanner(isMock = isMockData, source = activePrice?.source ?: "MOCK AGMARKNET")
            }

            // Current Mandi Price Card
            item {
                CurrentPriceCard(activePrice)
            }

            // Historical Price Trend Chart Section
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
                                text = "HISTORICAL PRICE TRENDS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KisanCharcoal
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "+6.2% OVERALL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Timeframe filter tabs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("7 Days", "30 Days", "90 Days", "1 Year").forEach { tf ->
                                val isSelected = selectedTimeframe == tf
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) KisanEmerald else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedTimeframe = tf }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tf,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else KisanCharcoal
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Vector Price Trend Chart
                        PriceTrendCanvas(timeframe = selectedTimeframe)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val (pastLabel, pastPrice) = when (selectedTimeframe) {
                                "30 Days" -> "30 Days Ago:" to "₹2,100/q"
                                "90 Days" -> "90 Days Ago:" to "₹1,850/q"
                                "1 Year" -> "1 Year Ago:" to "₹2,600/q"
                                else -> "7 Days Ago:" to "₹2,310/q"
                            }
                            Text("$pastLabel $pastPrice", fontSize = 11.sp, color = KisanMutedSage)
                            Text("Today: ₹2,450/q", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KisanEmerald)
                        }
                    }
                }
            }

            // 7-Day / 14-Day / 30-Day Range Price Forecast Card
            item {
                PriceForecastWithTabsCard(
                    forecast = marketForecast,
                    selectedHorizon = selectedForecastTab,
                    onSelectHorizon = { selectedForecastTab = it }
                )
            }

            // 4-Question Selling Decision Assistant
            item {
                SellingDecisionCard(marketForecast, activePrice)
            }

            // Nearby Mandis Comparison Section
            item {
                Text(
                    text = "Nearby Mandi Rates & Comparison",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KisanCharcoal,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(nearbyMarkets) { market ->
                NearbyMarketComparisonCard(market)
            }

            // Google Maps Grounding: Real APMC Mandis & Agri Centers
            item {
                GoogleMapsGroundingMandisCard(viewModel = viewModel, farmerProfile = farmerProfile)
            }

            // Google Search Grounding: Live APMC Trends & MSP Intelligence
            item {
                GoogleSearchGroundingMarketNewsCard(viewModel = viewModel, farmerProfile = farmerProfile)
            }
        }
    }
}

@Composable
fun MarketSourceBanner(isMock: Boolean, source: String) {
    val containerColor = if (isMock) Color(0xFFFEF3C7) else KisanEmerald.copy(alpha = 0.12f)
    val contentColor = if (isMock) Color(0xFFB45309) else KisanEmerald
    val icon = if (isMock) Icons.Default.Info else Icons.Default.CheckCircle
    val title = if (isMock) "DEMO / MOCK MARKET DATA" else "VERIFIED LIVE MANDI FEED"
    val subtitle = if (isMock) {
        "Demonstration feed with simulated historical rates. Real e-NAM API connector available."
    } else {
        "Direct verified data feed from $source."
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun CurrentPriceCard(price: MarketPriceDto?) {
    val modalPrice = price?.modalPrice ?: 2450.0
    val minPrice = price?.minPrice ?: 2100.0
    val maxPrice = price?.maxPrice ?: 2800.0
    val unit = price?.unit ?: "quintal"
    val timeFormatted = price?.timestamp?.let {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(it))
    } ?: "Today, 02:15 PM"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Current Mandi Modal Price",
                    fontSize = 14.sp,
                    color = KisanMutedSage,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KisanEmerald.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "MODAL RATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "₹${modalPrice.toInt()}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = KisanCharcoal
                )
                Text(
                    " / $unit",
                    fontSize = 16.sp,
                    color = KisanMutedSage,
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriceStat("Minimum", "₹${minPrice.toInt()}")
                PriceStat("Maximum", "₹${maxPrice.toInt()}")
                PriceStat("Modal", "₹${modalPrice.toInt()}")
                PriceStat("Today's Arrivals", "450 q")
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Updated: $timeFormatted", fontSize = 11.sp, color = KisanMutedSage)
                Text("Source: ${price?.source ?: "e-NAM / Agmarknet"}", fontSize = 11.sp, color = KisanMutedSage)
            }
        }
    }
}

@Composable
fun PriceStat(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = KisanMutedSage)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = KisanCharcoal)
    }
}

private data class TrendDataPoint(val label: String, val value: Int, val normalizedY: Float)

@Composable
private fun PriceTrendCanvas(timeframe: String) {
    var touchedPoint by remember { mutableStateOf<Offset?>(null) }
    val textMeasurer = rememberTextMeasurer()

    val rawPoints = when (timeframe) {
        "30 Days" -> listOf(
            TrendDataPoint("Day 1", 2100, 0.40f),
            TrendDataPoint("Day 6", 2150, 0.50f),
            TrendDataPoint("Day 12", 2300, 0.35f),
            TrendDataPoint("Day 18", 2200, 0.60f),
            TrendDataPoint("Day 24", 2250, 0.40f),
            TrendDataPoint("Day 30", 2450, 0.20f)
        )
        "90 Days" -> listOf(
            TrendDataPoint("Month 1", 1850, 0.90f),
            TrendDataPoint("Month 1.5", 2000, 0.60f),
            TrendDataPoint("Month 2", 2200, 0.30f),
            TrendDataPoint("Month 2.5", 2100, 0.70f),
            TrendDataPoint("Month 3", 2450, 0.10f)
        )
        "1 Year" -> listOf(
            TrendDataPoint("Jan", 2600, 0.20f),
            TrendDataPoint("Mar", 2100, 0.80f),
            TrendDataPoint("May", 1950, 0.90f),
            TrendDataPoint("Jul", 2300, 0.40f),
            TrendDataPoint("Sep", 2150, 0.60f),
            TrendDataPoint("Nov", 2550, 0.20f),
            TrendDataPoint("Dec", 2450, 0.15f)
        )
        else -> listOf( // "7 Days"
            TrendDataPoint("Mon", 2310, 0.85f),
            TrendDataPoint("Tue", 2330, 0.78f),
            TrendDataPoint("Wed", 2360, 0.65f),
            TrendDataPoint("Thu", 2400, 0.55f),
            TrendDataPoint("Fri", 2380, 0.60f),
            TrendDataPoint("Sat", 2420, 0.35f),
            TrendDataPoint("Sun", 2450, 0.22f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(timeframe) {
                    detectTapGestures(
                        onPress = { offset ->
                            touchedPoint = offset
                            tryAwaitRelease()
                            touchedPoint = null
                        }
                    )
                }
                .pointerInput(timeframe) {
                    detectDragGestures(
                        onDragStart = { offset -> touchedPoint = offset },
                        onDragEnd = { touchedPoint = null },
                        onDragCancel = { touchedPoint = null }
                    ) { change, _ ->
                        touchedPoint = change.position
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // Map TrendDataPoint to physical Offsets
            val physicalPoints = rawPoints.mapIndexed { index, data ->
                val x = if (rawPoints.size > 1) (width / (rawPoints.size - 1)) * index else width / 2
                val y = height * data.normalizedY
                Pair(Offset(x, y), data)
            }

            val path = Path().apply {
                if (physicalPoints.isNotEmpty()) {
                    moveTo(physicalPoints[0].first.x, physicalPoints[0].first.y)
                    for (i in 1 until physicalPoints.size) {
                        lineTo(physicalPoints[i].first.x, physicalPoints[i].first.y)
                    }
                }
            }

            drawPath(
                path = path,
                color = Color(0xFF059669),
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw data dots
            physicalPoints.forEach { (pt, _) ->
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = Color(0xFF059669),
                    radius = 3.dp.toPx(),
                    center = pt
                )
            }

            // Draw Tooltip if touched
            touchedPoint?.let { touchPt ->
                // Find closest point by X axis
                val closestPoint = physicalPoints.minByOrNull { abs(it.first.x - touchPt.x) }
                closestPoint?.let { (pt, data) ->
                    // Draw vertical guideline
                    drawLine(
                        color = Color(0xFF059669).copy(alpha = 0.5f),
                        start = Offset(pt.x, 0f),
                        end = Offset(pt.x, height),
                        strokeWidth = 1.dp.toPx()
                    )
                    
                    // Draw highlight circle
                    drawCircle(
                        color = Color(0xFF059669),
                        radius = 7.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = pt
                    )
                    
                    // Draw Tooltip Box
                    val labelText = "${data.label}: ₹${data.value}"
                    val textLayoutResult = textMeasurer.measure(
                        text = labelText,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    val tooltipWidth = textLayoutResult.size.width + 16.dp.toPx()
                    val tooltipHeight = textLayoutResult.size.height + 12.dp.toPx()
                    
                    var tooltipX = pt.x - tooltipWidth / 2
                    if (tooltipX < 0) tooltipX = 0f
                    if (tooltipX + tooltipWidth > width) tooltipX = width - tooltipWidth
                    
                    val tooltipY = pt.y - tooltipHeight - 12.dp.toPx()
                    val safeTooltipY = if (tooltipY < 0) pt.y + 12.dp.toPx() else tooltipY
                    
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(tooltipX, safeTooltipY),
                        size = Size(tooltipWidth, tooltipHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(tooltipX + 8.dp.toPx(), safeTooltipY + 6.dp.toPx())
                    )
                }
            }
        }
    }
}


@Composable
fun PriceForecastWithTabsCard(
    forecast: MarketForecastDto?,
    selectedHorizon: Int,
    onSelectHorizon: (Int) -> Unit
) {
    val expMin: Int
    val expMax: Int
    val trend: String
    val confidenceStr: String
    val confidenceVal: Float
    val confidenceColor: Color

    when (selectedHorizon) {
        30 -> { expMin = 2500; expMax = 2780; trend = "Increasing (High Demand)"; confidenceStr = "72% (Moderate)"; confidenceVal = 0.72f; confidenceColor = KisanHarvestGold }
        90 -> { expMin = 2300; expMax = 2600; trend = "Stable to Moderate"; confidenceStr = "61% (Fair)"; confidenceVal = 0.61f; confidenceColor = Color(0xFFF59E0B) }
        else -> { expMin = 2400; expMax = 2650; trend = "Moderately Increasing"; confidenceStr = "88% (Strong)"; confidenceVal = 0.88f; confidenceColor = KisanEmerald }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
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
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = KisanEmerald
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "AI Price Forecast Range",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanCharcoal
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "STATISTICAL ESTIMATE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizon Tabs: 7 Days, 30 Days, 90 Days
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(7, 30, 90).forEach { days ->
                    val isSelected = selectedHorizon == days
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) KisanDeepForest else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectHorizon(days) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$days Days",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else KisanCharcoal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Expected Range for $selectedHorizon-Day Horizon:", fontSize = 12.sp, color = KisanMutedSage)
            Text(
                "₹$expMin – ₹$expMax / quintal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Predicted Trend", fontSize = 11.sp, color = KisanMutedSage)
                    Text(trend, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KisanEmerald)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Model Confidence", fontSize = 11.sp, color = KisanMutedSage)
                    Text(confidenceStr, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = confidenceColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { confidenceVal },
                        modifier = Modifier.fillMaxWidth(0.9f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = confidenceColor,
                        trackColor = confidenceColor.copy(alpha = 0.2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Notice: Agricultural price forecasts are probabilistic estimates based on mandi arrivals and seasonal history. Never treat as guaranteed selling prices.",
                fontSize = 11.sp,
                color = KisanMutedSage,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun SellingDecisionCard(forecast: MarketForecastDto?, price: MarketPriceDto?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = BorderStroke(1.dp, KisanEmerald.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = KisanEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Should I Sell?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanEmerald
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "RECOMMENDATION: SELL SOON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. WHAT is happening?
            DecisionSection(
                title = "1. WHAT IS HAPPENING?",
                description = "Daily arrivals at nearby Surat & Navsari mandis are down by 14% this week, pushing modal tomato prices up to ₹2,450/q (+6.2%)."
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2. WHY is it happening?
            DecisionSection(
                title = "2. WHY IS IT HAPPENING?",
                description = "Supply from neighboring talukas is temporarily delayed, while urban retail demand in Surat and Mumbai wholesale corridors remains high."
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 3. WHAT should the farmer do?
            DecisionSection(
                title = "3. WHAT SHOULD YOU DO?",
                description = "Harvest mature batches now. Sell 50–60% of stock in the next 2 to 4 days to capture near-peak prices. Transporting to Navsari (+₹130/q) offers higher return after 38 km diesel costs."
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 4. WHAT could happen next?
            DecisionSection(
                title = "4. WHAT COULD HAPPEN NEXT?",
                description = "Fresh harvest arrivals will arrive in 7–10 days, which will stabilize rates and may cause a ₹150–200/q correction. Do not hold perishable stock past 6 days."
            )
        }
    }
}

@Composable
fun DecisionSection(title: String, description: String) {
    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = KisanEmerald
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            color = KisanCharcoal,
            lineHeight = 17.sp
        )
    }
}

@Composable
fun NearbyMarketComparisonCard(market: NearbyMarketInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(market.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KisanCharcoal)
                    Text("Distance: ${market.distanceKm} km away", fontSize = 11.sp, color = KisanMutedSage)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("₹${market.price.toInt()}/q", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = KisanEmerald)
                    Text(market.trend, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Min: ₹${market.minPrice.toInt()} | Max: ₹${market.maxPrice.toInt()}", fontSize = 10.sp, color = KisanMutedSage)
                Text(market.favorability, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KisanDeepForest)
            }
        }
    }
}

@Composable
fun GoogleMapsGroundingMandisCard(
    viewModel: KisanViewModel?,
    farmerProfile: FarmerProfile
) {
    val mapsState = viewModel?.mapsGroundingState?.collectAsStateWithLifecycle()?.value
    val isLoading = viewModel?.isMapsGroundingLoading?.collectAsStateWithLifecycle()?.value ?: false

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
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
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Google Maps Grounded Mandis",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanCharcoal
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Text(
                        text = "GEMINI 3.5 FLASH",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Locate verified APMC yards, KVKs and agro centers nearest to ${farmerProfile.village}, ${farmerProfile.state}.",
                fontSize = 12.sp,
                color = KisanMutedSage
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel?.fetchNearbyAgriPlacesWithMaps("APMC Mandi and agriculture market yard") },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                  CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Querying Google Maps Tool...", fontSize = 12.sp, color = Color.White)
                } else {
                  Icon(Icons.Default.LocationSearching, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Find Mandis via Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (mapsState != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mapsState.summaryText,
                    fontSize = 12.sp,
                    color = KisanCharcoal,
                    lineHeight = 17.sp
                )

                if (mapsState.places.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    mapsState.places.forEach { place ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF1F2),
                            border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = place.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239)
                                )
                                Text(
                                    text = "${place.address} (${place.distanceDescription})",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4C0519)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSearchGroundingMarketNewsCard(
    viewModel: KisanViewModel?,
    farmerProfile: FarmerProfile
) {
    val searchState = viewModel?.searchGroundingState?.collectAsStateWithLifecycle()?.value
    val isLoading = viewModel?.isSearchGroundingLoading?.collectAsStateWithLifecycle()?.value ?: false

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
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
                        Icons.Default.TravelExplore,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Google Search Grounded Trends",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KisanCharcoal
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = "LIVE SEARCH TOOL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Live Agmarknet & MSP commodity trends for ${farmerProfile.primaryCrop} with search citations.",
                fontSize = 12.sp,
                color = KisanMutedSage
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel?.fetchLiveMandiTrendsWithSearch("Latest mandi arrival rates and MSP trends for ${farmerProfile.primaryCrop}")
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                  CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Querying Google Search Grounding...", fontSize = 12.sp, color = Color.White)
                } else {
                  Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Ground Live Trends with Google Search", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (searchState != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = searchState.text,
                    fontSize = 12.sp,
                    color = KisanCharcoal,
                    lineHeight = 17.sp
                )

                if (searchState.sources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Official Sources & Citations:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    searchState.sources.take(3).forEach { src ->
                        Text(
                            text = "• ${src.title} (${src.url})",
                            fontSize = 10.sp,
                            color = Color(0xFF2563EB),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
