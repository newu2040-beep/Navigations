package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.viewmodel.GestureViewModel
import kotlin.math.roundToInt

@Composable
fun FloatingHubScreen(viewModel: GestureViewModel) {
    // Panel simulations toggles
    var isBallMenuExpanded by remember { mutableStateOf(false) }
    var isRadialWheelVisible by remember { mutableStateOf(false) }
    var isDockExpanded by remember { mutableStateOf(false) }
    var ballOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    // Dynamic Island simulation triggered by notifications
    var isDynamicIslandExpanded by remember { mutableStateOf(false) }
    var dynamicIslandText by remember { mutableStateOf("Navigations standby...") }

    // Floating bubble state
    var selectedIslandApp by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Headers
        item {
            FuturisticHeader(
                title = "Floating Hub",
                subtitle = "Expandable Quick-Action Bubbles & Radial Navigation Knobs"
            )
        }

        // Central Phone Screen Simulator
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "VIRTUAL DESKTOP ENGINE (INTERACT TO RUN)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // The Simulated Desktop Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F0A1A),
                                    Color(0xFF2E1B4E)
                                )
                            )
                        )
                ) {
                    // 1. Grid Background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val space = 20.dp.toPx()
                        for (i in 0..(size.width / space).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.015f),
                                start = Offset(i * space, 0f),
                                end = Offset(i * space, size.height)
                            )
                        }
                        for (i in 0..(size.height / space).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.015f),
                                start = Offset(0f, i * space),
                                end = Offset(size.width, i * space)
                            )
                        }
                    }

                    // 2. Simulated Dynamic Island cutout at top center
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = true,
                            enter = fadeIn()
                        ) {
                            val islandWidth by animateDpAsState(
                                targetValue = if (isDynamicIslandExpanded) 240.dp else 110.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "islandWidth"
                            )
                            val islandHeight by animateDpAsState(
                                targetValue = if (isDynamicIslandExpanded) 65.dp else 28.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "islandHeight"
                            )

                            Row(
                                modifier = Modifier
                                    .size(width = islandWidth, height = islandHeight)
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(Color.Black)
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(30.dp))
                                    .clickable { isDynamicIslandExpanded = !isDynamicIslandExpanded }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isDynamicIslandExpanded) {
                                    Icon(
                                        imageVector = Icons.Default.Cyclone,
                                        contentDescription = null,
                                        tint = Color(0xFF00FFCC),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "GESTURE RADAR ACTIVE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00FFCC),
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = dynamicIslandText,
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }
                                } else {
                                    // Cutout camera lens details
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0F172A))
                                    )
                                    Spacer(modifier = Modifier.width(36.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF105353))
                                    )
                                }
                            }
                        }
                    }

                    // 3. Ambient Clock Widget on simulated Desktop
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 90.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "12:49",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "TUE, MAY 26, 2026",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        )
                    }

                    // 4. Simulated Edge Controls Trigger Zone
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(8.dp)
                            .fillMaxHeight(0.4f)
                            .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                            .background(Color(0xFF00FFCC).copy(alpha = 0.4f))
                            .clickable {
                                viewModel.triggerVibrate(30)
                                isRadialWheelVisible = !isRadialWheelVisible
                                if (isRadialWheelVisible) {
                                    dynamicIslandText = "Radial Swiped opened"
                                    isDynamicIslandExpanded = true
                                }
                            }
                    )

                    // 5. Simulated Swipe Radial Wheel (Pulls out curved arc from center right)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isRadialWheelVisible,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.Black.copy(alpha = 0.9f))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("RADIAL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RadialIconButton(Icons.Default.CameraAlt, "Camera") {
                                    isRadialWheelVisible = false
                                    viewModel.triggerGestureRecognizedEvent("C")
                                }
                                RadialIconButton(Icons.Default.FlashlightOn, "Flash") {
                                    isRadialWheelVisible = false
                                    viewModel.triggerGestureRecognizedEvent("V")
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RadialIconButton(Icons.Default.MusicNote, "Music") {
                                    isRadialWheelVisible = false
                                    viewModel.triggerGestureRecognizedEvent("M")
                                }
                                RadialIconButton(Icons.Default.CropFree, "Screenshot") {
                                    isRadialWheelVisible = false
                                    viewModel.triggerGestureRecognizedEvent("S")
                                }
                            }
                        }
                    }

                    // 6. Interactive Floating Navigation Ball (Free dragging coordinates!)
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(ballOffset.x.roundToInt(), ballOffset.y.roundToInt()) }
                            .size(if (isBallMenuExpanded) 110.dp else 48.dp)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { viewModel.triggerVibrate(20) },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        ballOffset = Offset(
                                            x = (ballOffset.x + dragAmount.x).coerceIn(-150f, 150f),
                                            y = (ballOffset.y + dragAmount.y).coerceIn(-180f, 180f)
                                        )
                                    }
                                )
                            }
                            .clickable {
                                isBallMenuExpanded = !isBallMenuExpanded
                                viewModel.triggerVibrate(40)
                                dynamicIslandText = if (isBallMenuExpanded) "Floating Ball opened" else "Floating Ball minimized"
                                isDynamicIslandExpanded = true
                            }
                            .testTag("floating_navigation_ball"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBallMenuExpanded) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    IconButton(
                                        onClick = {
                                            isBallMenuExpanded = false
                                            viewModel.triggerGestureRecognizedEvent("M")
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            isBallMenuExpanded = false
                                            viewModel.triggerGestureRecognizedEvent("S")
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Screenshot, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    IconButton(
                                        onClick = {
                                            isBallMenuExpanded = false
                                            viewModel.triggerGestureRecognizedEvent("V")
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { isBallMenuExpanded = false },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        } else {
                            // Touch knob core
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF00FFCC),
                                                MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    // 7. Simulated Expandable Floating Dock
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    ) {
                        val dockWidth by animateDpAsState(
                            targetValue = if (isDockExpanded) 220.dp else 60.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "dockWidth"
                        )

                        Row(
                            modifier = Modifier
                                .size(width = dockWidth, height = 48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.Black.copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                                .clickable { isDockExpanded = !isDockExpanded }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            if (isDockExpanded) {
                                IconButton(onClick = { viewModel.triggerGestureRecognizedEvent("M") }) {
                                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                                }
                                IconButton(onClick = { viewModel.triggerGestureRecognizedEvent("C") }) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                                }
                                IconButton(onClick = { viewModel.triggerGestureRecognizedEvent("W") }) {
                                    Icon(Icons.Default.Forum, contentDescription = null, tint = Color.White)
                                }
                                IconButton(onClick = { isDockExpanded = false }) {
                                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.Red)
                                }
                            } else {
                                Icon(Icons.Default.Apps, contentDescription = "Expand Dock", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Instructions details card
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "SANDBOX CONTROL KEYS DIRECTORY",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                GlassCard {
                    BulletInfoRow("Navigation Ball Knob", "Drag and slide the ball anywhere inside the desktop. Tap to expand it contextually for quick macros triggers.")
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletInfoRow("Left-Right Swiping Bars", "Tap the cyber glow turquoise indicator line at the right border to swipe pull-out curved wheel launchers.")
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletInfoRow("Simulated Dynamic Island Cutout", "Tap the camera black capsule at the top of screen to inspect sensor triggers, overlays states contextually.")
                }
            }
        }
    }
}

@Composable
fun BulletInfoRow(header: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(16.dp))
        Column {
            Text(header, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        }
    }
}

@Composable
fun RadialIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(0.5.dp, Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
    }
}
