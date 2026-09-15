import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.abs

// ... later in the file ...

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
