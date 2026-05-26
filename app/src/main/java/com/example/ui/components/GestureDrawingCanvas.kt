package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.GesturePoint
import com.example.gesture.GestureRecognizer
import kotlinx.coroutines.launch

@Composable
fun GestureDrawingCanvas(
    modifier: Modifier = Modifier,
    customTemplates: Map<String, List<GesturePoint>> = emptyMap(),
    onGestureCompleted: (name: String, score: Float, rawPoints: List<GesturePoint>) -> Unit
) {
    var rawPoints = remember { mutableStateListOf<GesturePoint>() }
    var matchResult by remember { mutableStateOf<String?>(null) }
    var matchScore by remember { mutableStateOf(0.0f) }
    val trailAlpha = remember { Animatable(1.0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .pointerInput(customTemplates) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Clear previous and start new trail
                        rawPoints.clear()
                        matchResult = null
                        matchScore = 0.0f
                        scope.launch { trailAlpha.snapTo(1.0f) }
                        rawPoints.add(GesturePoint(offset.x, offset.y))
                    },
                    onDragEnd = {
                        if (rawPoints.size >= 4) {
                            val result = GestureRecognizer.recognize(rawPoints.toList(), customTemplates)
                            matchResult = result.name
                            matchScore = result.score
                            onGestureCompleted(result.name, result.score, rawPoints.toList())
                        }
                        // Animate trail fade out
                        scope.launch {
                            trailAlpha.animateTo(0.0f, tween(650))
                            rawPoints.clear()
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            trailAlpha.animateTo(0.0f, tween(300))
                            rawPoints.clear()
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val nextPoint = rawPoints.lastOrNull()?.let { last ->
                            GesturePoint(last.x + dragAmount.x, last.y + dragAmount.y)
                        } ?: GesturePoint(change.position.x, change.position.y)
                        rawPoints.add(nextPoint)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Transparent Overlay Grid Lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color.White.copy(alpha = 0.04f)
            val rows = 8
            val cols = 8

            // Horizontal grid
            for (r in 1 until rows) {
                val y = size.height * r / rows
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.2.dp.toPx())
            }

            // Vertical grid
            for (c in 1 until cols) {
                val x = size.width * c / cols
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.2.dp.toPx())
            }
        }

        // Action Drawing Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (rawPoints.isNotEmpty()) {
                val path = Path().apply {
                    val first = rawPoints.first()
                    moveTo(first.x, first.y)
                    for (i in 1 until rawPoints.size) {
                        val p = rawPoints[i]
                        lineTo(p.x, p.y)
                    }
                }

                // Glowing Neon Drop Shadow under path
                drawPath(
                    path = path,
                    color = Color(0xFF00FFCC).copy(alpha = 0.35f * trailAlpha.value),
                    style = Stroke(
                        width = 24.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // High Contrast Core path
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = trailAlpha.value),
                    style = Stroke(
                        width = 7.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Spark point at brush head
                val endPoint = rawPoints.last()
                drawCircle(
                    color = Color(0xFF00FFCC).copy(alpha = trailAlpha.value),
                    radius = 12.dp.toPx(),
                    center = Offset(endPoint.x, endPoint.y)
                )
            }
        }

        // Realtime floating HUD result overlay
        if (matchResult != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                GlassCard(
                    modifier = Modifier.wrapContentSize(),
                    cornerRadius = 16.dp,
                    borderColor = if (matchScore >= 0.75f) Color(0xFF00FFCC) else Color.Red.copy(alpha = 0.5f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (matchScore >= 0.75f) Icons.Default.Brush else Icons.Default.Info,
                            contentDescription = "Match Status",
                            tint = if (matchScore >= 0.75f) Color(0xFF00FFCC) else Color.White.copy(alpha = 0.7f)
                        )
                        Column {
                            Text(
                                text = "RECOGNIZED: $matchResult",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Matching confidence: ${(matchScore * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else if (rawPoints.isEmpty()) {
            // Friendly Tip
            Column(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = "Brush Input",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "DRAW SIGNATURE SYMBOL OR CODE",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Try drawing C, M, W, S, or V",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
