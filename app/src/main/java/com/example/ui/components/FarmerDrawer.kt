package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.model.FarmerProfile
import com.example.presentation.theme.*

/**
 * Route destinations for the Kissan AI Side Menu & Command Center.
 */
enum class DrawerDestination(val title: String) {
    DASHBOARD("Command Center"),
    MY_FARMS("My Farms"),
    CROP_HEALTH("Crop Health"),
    AI_PLANT_DOCTOR("AI Plant Doctor"),
    YIELD_INTELLIGENCE("Yield Intelligence"),
    IRRIGATION_ADVISOR("Irrigation Advisor"),
    WEATHER_CLIMATE("Weather & Climate"),
    SATELLITE_INTELLIGENCE("Satellite Intelligence"),
    SMART_FARM("Smart Farm / IoT"),
    MARKET_INTELLIGENCE("Market Intelligence"),
    FARM_PLAN("7-Day Farm Plan"),
    FARM_RECOMMENDATIONS("Farm Recommendations"),
    DISEASE_HISTORY("Disease History"),
    FARM_ACTIVITY_HISTORY("Farm Activity History"),
    YIELD_HISTORY("Yield History"),
    MARKET_HISTORY("Market History"),
    AI_COPILOT("AI Farm Copilot"),
    SCAN_CROP("Scan Crop"),
    COMPARE_CROPS("Compare Crops"),
    FARM_ANALYTICS("Farm Analytics"),
    WHAT_IF_SIMULATOR("What-If Simulator"),
    SETTINGS("Settings"),
    HELP_HOW_IT_WORKS("How Kissan AI Works"),
    HELP_SAFETY("Agriculture Safety"),
    HELP_ABOUT("About Kissan AI"),
    PROBLEM_STATEMENT_DEMO("Problem Statement Demo"),
    COMPLIANCE_MATRIX("Compliance Matrix")
}

@Composable
fun FarmerDrawerContent(
    farmerProfile: FarmerProfile,
    activeRoute: DrawerDestination?,
    onNavigate: (DrawerDestination) -> Unit,
    onLogout: () -> Unit,
    strings: AppStrings,
    activeFarmName: String = "Green Valley Farm",
    farmHealthScore: Int = 82,
    cropHealthScore: Int = 86,
    alertsCount: Int = 3,
    marketPriceFormatted: String = "₹2,450/q ↑",
    deviceStatus: String = "Online",
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        drawerContainerColor = KisanWarmIvory,
        drawerContentColor = KisanCharcoal,
        modifier = modifier
            .widthIn(max = 340.dp)
            .fillMaxWidth(0.86f)
            .fillMaxHeight()
            .testTag("farmer_command_center_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // -------------------------------------------------------------
            // 1. BRAND HEADER
            // -------------------------------------------------------------
            Surface(
                color = KisanDeepForest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = KisanEmerald,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "KISSAN AI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Farmer Command Center",
                                    fontSize = 11.sp,
                                    color = KisanEmeraldLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KisanEmerald.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "v2.5 PRO",
                                fontSize = 10.sp,
                                color = KisanEmeraldLight,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // PROFILE SUMMARY
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = KisanEmerald,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = farmerProfile.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = farmerProfile.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Farm: $activeFarmName",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Active Crop: ${farmerProfile.primaryCrop.ifEmpty { "Tomato" }} • 2 Registered Plots",
                                    color = KisanEmeraldLight,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // -------------------------------------------------------------
            // 2. QUICK STATUS BAR
            // -------------------------------------------------------------
            DrawerSectionHeader(title = strings.sectionQuickStatus)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatusChip(
                    label = strings.chipFarm,
                    value = "$farmHealthScore/100",
                    color = KisanEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(DrawerDestination.DASHBOARD) }
                )
                QuickStatusChip(
                    label = strings.chipCrop,
                    value = "$cropHealthScore%",
                    color = KisanDeepForest,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(DrawerDestination.CROP_HEALTH) }
                )
                QuickStatusChip(
                    label = strings.chipAlerts,
                    value = "$alertsCount Active",
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(DrawerDestination.DISEASE_HISTORY) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatusChip(
                    label = strings.chipMandi,
                    value = marketPriceFormatted,
                    color = Color(0xFF0D9488),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(DrawerDestination.MARKET_INTELLIGENCE) }
                )
                QuickStatusChip(
                    label = strings.chipIot,
                    value = "● $deviceStatus",
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(DrawerDestination.SMART_FARM) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // 3. FARM INTELLIGENCE
            // -------------------------------------------------------------
            DrawerSectionHeader(title = strings.sectionFarmIntelligence)
            DrawerMenuItem(
                icon = Icons.Default.Dashboard,
                label = strings.drawerCommandCenter,
                badge = "COMMAND",
                badgeColor = KisanEmerald,
                isSelected = activeRoute == DrawerDestination.DASHBOARD,
                onClick = { onNavigate(DrawerDestination.DASHBOARD) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Agriculture,
                label = strings.drawerMyFarms,
                isSelected = activeRoute == DrawerDestination.MY_FARMS,
                onClick = { onNavigate(DrawerDestination.MY_FARMS) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Eco,
                label = strings.drawerCropHealth,
                badge = "$cropHealthScore%",
                isSelected = activeRoute == DrawerDestination.CROP_HEALTH,
                onClick = { onNavigate(DrawerDestination.CROP_HEALTH) }
            )
            DrawerMenuItem(
                icon = Icons.Default.CameraAlt,
                label = strings.drawerAiPlantDoctor,
                badge = "2",
                badgeColor = Color(0xFFDC2626),
                isSelected = activeRoute == DrawerDestination.AI_PLANT_DOCTOR,
                onClick = { onNavigate(DrawerDestination.AI_PLANT_DOCTOR) }
            )
            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = strings.drawerYieldIntelligence,
                isSelected = activeRoute == DrawerDestination.YIELD_INTELLIGENCE,
                onClick = { onNavigate(DrawerDestination.YIELD_INTELLIGENCE) }
            )
            DrawerMenuItem(
                icon = Icons.Default.WaterDrop,
                label = strings.drawerIrrigationAdvisor,
                badge = "SOIL 29%",
                badgeColor = Color(0xFF2563EB),
                isSelected = activeRoute == DrawerDestination.IRRIGATION_ADVISOR,
                onClick = { onNavigate(DrawerDestination.IRRIGATION_ADVISOR) }
            )
            DrawerMenuItem(
                icon = Icons.Default.WbSunny,
                label = strings.drawerWeatherClimate,
                isSelected = activeRoute == DrawerDestination.WEATHER_CLIMATE,
                onClick = { onNavigate(DrawerDestination.WEATHER_CLIMATE) }
            )
            DrawerMenuItem(
                icon = Icons.Default.SatelliteAlt,
                label = strings.drawerSatelliteIntelligence,
                badge = "NDVI",
                isSelected = activeRoute == DrawerDestination.SATELLITE_INTELLIGENCE,
                onClick = { onNavigate(DrawerDestination.SATELLITE_INTELLIGENCE) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Sensors,
                label = strings.drawerSmartFarm,
                badge = "●",
                badgeColor = Color(0xFF16A34A),
                isSelected = activeRoute == DrawerDestination.SMART_FARM,
                onClick = { onNavigate(DrawerDestination.SMART_FARM) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Storefront,
                label = strings.drawerMarketIntelligence,
                badge = "₹",
                badgeColor = KisanHarvestGold,
                isSelected = activeRoute == DrawerDestination.MARKET_INTELLIGENCE,
                onClick = { onNavigate(DrawerDestination.MARKET_INTELLIGENCE) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // 4. PLANNING & INSIGHTS
            // -------------------------------------------------------------
            DrawerSectionHeader(title = strings.sectionPlanningInsights)
            DrawerMenuItem(
                icon = Icons.Default.CalendarMonth,
                label = strings.drawer7DayPlan,
                badge = "NEW",
                badgeColor = KisanEmerald,
                isSelected = activeRoute == DrawerDestination.FARM_PLAN,
                onClick = { onNavigate(DrawerDestination.FARM_PLAN) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Lightbulb,
                label = strings.drawerFarmRecommendations,
                isSelected = activeRoute == DrawerDestination.FARM_RECOMMENDATIONS,
                onClick = { onNavigate(DrawerDestination.FARM_RECOMMENDATIONS) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Healing,
                label = strings.drawerDiseaseHistory,
                isSelected = activeRoute == DrawerDestination.DISEASE_HISTORY,
                onClick = { onNavigate(DrawerDestination.DISEASE_HISTORY) }
            )
            DrawerMenuItem(
                icon = Icons.Default.History,
                label = strings.drawerFarmActivityHistory,
                isSelected = activeRoute == DrawerDestination.FARM_ACTIVITY_HISTORY,
                onClick = { onNavigate(DrawerDestination.FARM_ACTIVITY_HISTORY) }
            )
            DrawerMenuItem(
                icon = Icons.Default.ShowChart,
                label = "${strings.drawerYieldIntelligence} ${strings.navHistory}",
                isSelected = activeRoute == DrawerDestination.YIELD_HISTORY,
                onClick = { onNavigate(DrawerDestination.YIELD_HISTORY) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Timeline,
                label = "${strings.drawerMarketIntelligence} ${strings.navHistory}",
                isSelected = activeRoute == DrawerDestination.MARKET_HISTORY,
                onClick = { onNavigate(DrawerDestination.MARKET_HISTORY) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // 5. TOOLS & COMPLIANCE
            // -------------------------------------------------------------
            DrawerSectionHeader(title = strings.sectionToolsDiagnostics)
            DrawerMenuItem(
                icon = Icons.Default.Psychology,
                label = strings.drawerAiCopilot,
                badge = "AI",
                badgeColor = KisanEmerald,
                isSelected = activeRoute == DrawerDestination.AI_COPILOT,
                onClick = { onNavigate(DrawerDestination.AI_COPILOT) }
            )
            DrawerMenuItem(
                icon = Icons.Default.DocumentScanner,
                label = strings.scanNow,
                isSelected = activeRoute == DrawerDestination.SCAN_CROP,
                onClick = { onNavigate(DrawerDestination.SCAN_CROP) }
            )
            DrawerMenuItem(
                icon = Icons.Default.FactCheck,
                label = strings.drawerProblemStatementDemo,
                badge = "LIVE",
                badgeColor = KisanEmerald,
                isSelected = activeRoute == DrawerDestination.PROBLEM_STATEMENT_DEMO,
                onClick = { onNavigate(DrawerDestination.PROBLEM_STATEMENT_DEMO) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Verified,
                label = strings.drawerComplianceMatrix,
                isSelected = activeRoute == DrawerDestination.COMPLIANCE_MATRIX,
                onClick = { onNavigate(DrawerDestination.COMPLIANCE_MATRIX) }
            )
            DrawerMenuItem(
                icon = Icons.Default.CompareArrows,
                label = "Compare Crops",
                isSelected = activeRoute == DrawerDestination.COMPARE_CROPS,
                onClick = { onNavigate(DrawerDestination.COMPARE_CROPS) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Analytics,
                label = strings.drawerFarmAnalytics,
                isSelected = activeRoute == DrawerDestination.FARM_ANALYTICS,
                onClick = { onNavigate(DrawerDestination.FARM_ANALYTICS) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Tune,
                label = strings.drawerWhatIfSimulator,
                badge = "SIM",
                badgeColor = Color(0xFF7C3AED),
                isSelected = activeRoute == DrawerDestination.WHAT_IF_SIMULATOR,
                onClick = { onNavigate(DrawerDestination.WHAT_IF_SIMULATOR) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // 6. SETTINGS
            // -------------------------------------------------------------
            DrawerSectionHeader(title = strings.drawerSettings)
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                label = strings.drawerSettings,
                isSelected = activeRoute == DrawerDestination.SETTINGS,
                onClick = { onNavigate(DrawerDestination.SETTINGS) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------------------
            // 7. HELP & LOGOUT
            // -------------------------------------------------------------
            DrawerSectionHeader(title = "HELP & SUPPORT")
            DrawerMenuItem(
                icon = Icons.Default.HelpOutline,
                label = strings.drawerHowItWorks,
                isSelected = activeRoute == DrawerDestination.HELP_HOW_IT_WORKS,
                onClick = { onNavigate(DrawerDestination.HELP_HOW_IT_WORKS) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Shield,
                label = strings.drawerSafety,
                isSelected = activeRoute == DrawerDestination.HELP_SAFETY,
                onClick = { onNavigate(DrawerDestination.HELP_SAFETY) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Info,
                label = strings.drawerAbout,
                isSelected = activeRoute == DrawerDestination.HELP_ABOUT,
                onClick = { onNavigate(DrawerDestination.HELP_ABOUT) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Logout Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEE2E2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLogout() }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.logout,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = KisanMutedSage,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    badgeColor: Color = KisanEmerald
) {
    val backgroundColor = if (isSelected) KisanEmerald.copy(alpha = 0.12f) else Color.Transparent
    val contentColor = if (isSelected) KisanEmerald else KisanCharcoal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("drawer_item_${label.lowercase().replace(" ", "_")}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickStatusChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, color = KisanMutedSage)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
