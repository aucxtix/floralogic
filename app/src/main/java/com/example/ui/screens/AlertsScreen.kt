package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppStrings
import com.example.presentation.theme.*

enum class AlertPriority(val label: String, val color: Color, val containerColor: Color) {
    ALL("All", KisanCharcoal, Color(0xFFF1F5F9)),
    CRITICAL("Critical", Color(0xFFDC2626), Color(0xFFFEE2E2)),
    HIGH("High", Color(0xFFEA580C), Color(0xFFFFEDD5)),
    MEDIUM("Medium", Color(0xFFD97706), Color(0xFFFEF3C7)),
    INFO("Info", Color(0xFF2563EB), Color(0xFFDBEAFE))
}

data class FarmAlertItem(
    val id: String,
    val title: String,
    val message: String,
    val category: String,
    val priority: AlertPriority,
    val timestamp: String,
    var isRead: Boolean = false,
    val actionLabel: String,
    val onAction: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    strings: AppStrings,
    onNavigateToScan: () -> Unit = {},
    onNavigateToIrrigation: () -> Unit = {},
    onNavigateToMarket: () -> Unit = {},
    onNavigateToHardware: () -> Unit = {},
    onSendDummyNotification: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedPriority by remember { mutableStateOf(AlertPriority.ALL) }
    var selectedDummyType by remember { mutableStateOf("WATER_STRESS") }
    var showSentSuccessBanner by remember { mutableStateOf(false) }
    
    // Consolidated meaningful alerts
    var alertsList by remember {
        mutableStateOf(
            listOf(
                FarmAlertItem(
                    id = "alert_1",
                    title = "WATER STRESS DETECTED",
                    message = "Soil moisture is currently at 28%, significantly below the 40% target for flowering tomatoes. Rain probability is low (20%).",
                    category = "IRRIGATION",
                    priority = AlertPriority.HIGH,
                    timestamp = "15 min ago",
                    isRead = false,
                    actionLabel = "Check Irrigation",
                    onAction = onNavigateToIrrigation
                ),
                FarmAlertItem(
                    id = "alert_2",
                    title = "DISEASE VIGILANCE ADVISORY",
                    message = "Atmospheric humidity is 78% following recent morning dew. Weather conditions favor Early Blight spore germination. Inspect lower foliage.",
                    category = "DISEASE",
                    priority = AlertPriority.MEDIUM,
                    timestamp = "1 hour ago",
                    isRead = false,
                    actionLabel = "Scan Crop",
                    onAction = onNavigateToScan
                ),
                FarmAlertItem(
                    id = "alert_3",
                    title = "MANDI PRICE SPIKE",
                    message = "Tomato prices have surged +6.2% to ₹2,450/quintal at Surat APMC. Daily mandi arrivals dropped 14%. Favorable holding window.",
                    category = "MARKET",
                    priority = AlertPriority.INFO,
                    timestamp = "3 hours ago",
                    isRead = false,
                    actionLabel = "View Market",
                    onAction = onNavigateToMarket
                ),
                FarmAlertItem(
                    id = "alert_4",
                    title = "ESP32 SENSOR NODE #001 ONLINE",
                    message = "Field telemetry device re-established LoRa / WiFi sync. Battery is at 92% with strong signal strength (-64 dBm).",
                    category = "HARDWARE",
                    priority = AlertPriority.INFO,
                    timestamp = "5 hours ago",
                    isRead = true,
                    actionLabel = "View Sensors",
                    onAction = onNavigateToHardware
                )
            )
        )
    }

    val unreadCount = alertsList.count { !it.isRead }
    val filteredAlerts = if (selectedPriority == AlertPriority.ALL) {
        alertsList
    } else {
        alertsList.filter { it.priority == selectedPriority }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Farm Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = KisanCharcoal
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFDC2626),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                alertsList = alertsList.map { it.copy(isRead = true) }
                            }
                        ) {
                            Text(
                                "Mark All Read",
                                color = KisanEmerald,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(KisanWarmIvory)
                .padding(innerPadding)
        ) {
            // Priority Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AlertPriority.values().forEach { priority ->
                    val isSelected = selectedPriority == priority
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPriority = priority },
                        label = {
                            Text(
                                priority.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (priority == AlertPriority.ALL) KisanEmerald else priority.containerColor,
                            selectedLabelColor = if (priority == AlertPriority.ALL) Color.White else priority.color,
                            containerColor = Color.White,
                            labelColor = KisanMutedSage
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) Color.Transparent else Color(0xFFE2E8F0),
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            // Dummy System Notification Tester Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = KisanEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Phone System Notification Panel Test",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = KisanCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trigger real Android status bar push notifications to test alert priority & deep linking.",
                        fontSize = 12.sp,
                        color = KisanMutedSage
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "WATER_STRESS" to "🚨 Water Stress",
                            "MARKET" to "📈 Mandi Surge",
                            "PEST" to "⚠️ Pest Warning"
                        ).forEach { (type, label) ->
                            val isSelected = selectedDummyType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDummyType = type },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KisanEmerald,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onSendDummyNotification(selectedDummyType)
                            showSentSuccessBanner = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send Notification to Phone System Panel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showSentSuccessBanner) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dispatched to phone system panel! Swipe down status bar.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Alerts Feed
            if (filteredAlerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = KisanMutedSage,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Alerts in this category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = KisanCharcoal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Your farm telemetry and intelligence are running smoothly.",
                            fontSize = 13.sp,
                            color = KisanMutedSage,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAlerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            onMarkAsRead = {
                                alertsList = alertsList.map {
                                    if (it.id == alert.id) it.copy(isRead = true) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    alert: FarmAlertItem,
    onMarkAsRead: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isRead) Color.White else Color(0xFFFCFDFD)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (alert.isRead) 1.dp else 1.5.dp,
            color = if (alert.isRead) Color(0xFFE2E8F0) else alert.priority.color.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alert.isRead) 1.dp else 2.dp),
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
                        color = alert.priority.containerColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = alert.priority.label.uppercase(),
                            color = alert.priority.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alert.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KisanMutedSage
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.timestamp,
                        fontSize = 11.sp,
                        color = KisanMutedSage
                    )
                    if (!alert.isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(alert.priority.color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alert.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = KisanCharcoal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.message,
                fontSize = 13.sp,
                color = KisanCharcoal.copy(alpha = 0.85f),
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onMarkAsRead()
                        alert.onAction()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KisanEmerald),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(alert.actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }

                if (!alert.isRead) {
                    TextButton(
                        onClick = onMarkAsRead,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Dismiss",
                            fontSize = 12.sp,
                            color = KisanMutedSage
                        )
                    }
                }
            }
        }
    }
}
