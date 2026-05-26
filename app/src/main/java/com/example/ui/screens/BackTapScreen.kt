package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.database.AutomationRuleEntity
import com.example.ui.components.*
import com.example.ui.viewmodel.GestureViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BackTapScreen(viewModel: GestureViewModel) {
    val scope = rememberCoroutineScope()
    val rules by viewModel.listRules.collectAsStateWithLifecycle()

    // Animation drivers
    var tapRippleState by remember { mutableStateOf(false) } // double/triple tap ripple indicator trigger
    var shakeAnimationState by remember { mutableStateOf(false) } // phone tilt trigger

    val phoneRotation = remember { Animatable(0f) }
    val rippleProgress = remember { Animatable(0f) }

    // Map bound actions for UI labels
    val doubleTapRule = rules.find { it.triggerType == "BACK_TAP_DOUBLE" }
    val tripleTapRule = rules.find { it.triggerType == "BACK_TAP_TRIPLE" }
    val shakeRule = rules.find { it.triggerType == "SHAKE_PHONE" }

    LaunchedEffect(shakeAnimationState) {
        if (shakeAnimationState) {
            // High-frequency shake sway sequence
            phoneRotation.animateTo(12f, tween(100, easing = LinearEasing))
            phoneRotation.animateTo(-12f, tween(100, easing = LinearEasing))
            phoneRotation.animateTo(8f, tween(100, easing = LinearEasing))
            phoneRotation.animateTo(-8f, tween(100, easing = LinearEasing))
            phoneRotation.animateTo(0f, tween(150, easing = LinearEasing))
            shakeAnimationState = false
        }
    }

    LaunchedEffect(tapRippleState) {
        if (tapRippleState) {
            rippleProgress.snapTo(0f)
            rippleProgress.animateTo(1f, tween(600, easing = EaseOutQuad))
            tapRippleState = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Design Header
        item {
            FuturisticHeader(
                title = "Tap & Shake",
                subtitle = "Inertial Gravity Sensors & Device Body Triggers"
            )
        }

        // Mechanical Phone Canvas Interactive Area
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing Phone Vector
                        Canvas(
                            modifier = Modifier
                                .width(120.dp)
                                .height(200.dp)
                                .rotate(phoneRotation.value)
                        ) {
                            val rectWidth = 100.dp.toPx()
                            val rectHeight = 180.dp.toPx()
                            val left = (size.width - rectWidth) / 2
                            val top = (size.height - rectHeight) / 2

                            // 1. Phone metal casing
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF32284C),
                                        Color(0xFF140E26)
                                    )
                                ),
                                topLeft = Offset(left, top),
                                size = Size(rectWidth, rectHeight),
                                cornerRadius = CornerRadius(16.dp.toPx()),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Glass shine specular lines
                            drawLine(
                                color = Color.White.copy(alpha = 0.12f),
                                start = Offset(left + 15.dp.toPx(), top),
                                end = Offset(left + rectWidth - 30.dp.toPx(), top + rectHeight),
                                strokeWidth = 1.dp.toPx()
                            )

                            // 2. Simulated Camera Module bump on Back
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.08f),
                                topLeft = Offset(left + 12.dp.toPx(), top + 12.dp.toPx()),
                                size = Size(28.dp.toPx(), 44.dp.toPx()),
                                cornerRadius = CornerRadius(6.dp.toPx())
                            )
                            drawCircle(
                                color = Color(0xFF00FFCC).copy(alpha = 0.6f),
                                radius = 6.dp.toPx(),
                                center = Offset(left + 26.dp.toPx(), top + 24.dp.toPx())
                            )
                            drawCircle(
                                color = Color(0xFFA020F0).copy(alpha = 0.6f),
                                radius = 6.dp.toPx(),
                                center = Offset(left + 26.dp.toPx(), top + 42.dp.toPx())
                            )

                            // 3. Back Tap Expanding Ripple Animation
                            if (rippleProgress.value > 0f && rippleProgress.value < 1f) {
                                drawCircle(
                                    color = Color(0xFF00FFCC).copy(alpha = 0.45f * (1f - rippleProgress.value)),
                                    radius = 70.dp.toPx() * rippleProgress.value,
                                    center = Offset(left + rectWidth / 2, top + rectHeight / 2),
                                    style = Stroke(width = 3.dp.toPx())
                                )
                                drawCircle(
                                    color = Color(0xFFA020F0).copy(alpha = 0.3f * (1f - rippleProgress.value)),
                                    radius = 110.dp.toPx() * rippleProgress.value,
                                    center = Offset(left + rectWidth / 2, top + rectHeight / 2),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }

                        // Floating sensor telemetry text overlays
                        Text(
                            text = "IMU TELEMETRY: ACTIVE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FFCC),
                            letterSpacing = 1.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        )

                        Text(
                            text = if (phoneRotation.value != 0f) "ACCEL TILT: ${phoneRotation.value.toInt()}°" else "STABLE ON DESK",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        // Live Simulation Triggers Control Center
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "MANUAL TELEMETRY EMULATORS",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            tapRippleState = true
                            viewModel.triggerBackTapSimulated(isDouble = true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("double_tap_trigger_btn")
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Db-Tap Back", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            tapRippleState = true
                            viewModel.triggerBackTapSimulated(isDouble = false)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("triple_tap_trigger_btn")
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tr-Tap Back", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            shakeAnimationState = true
                            viewModel.triggerShakeSimulated()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp)
                            .testTag("shake_trigger_btn")
                    ) {
                        Icon(Icons.Default.EdgesensorHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Shake Phone", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // Active Bindings Summaries Matrix
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "ACTIVE SENSOR ACTION BINDINGS",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SensorBindingRow(
                        trigger = "Double Tap Back",
                        actionDesc = if (doubleTapRule != null) "Automation Chain: ${doubleTapRule.name}" else "Toggle Flashlight (Built-in Toggle)",
                        icon = Icons.Default.TouchApp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    SensorBindingRow(
                        trigger = "Triple Tap Back",
                        actionDesc = if (tripleTapRule != null) "Automation Chain: ${tripleTapRule.name}" else "Play / Pause Music Deck",
                        icon = Icons.Default.TouchApp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    SensorBindingRow(
                        trigger = "Shake Phone",
                        actionDesc = if (shakeRule != null) "Automation Chain: ${shakeRule.name}" else "Capture Screenshot Sim",
                        icon = Icons.Default.EdgesensorHigh,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun SensorBindingRow(
    trigger: String,
    actionDesc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = trigger,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = actionDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
