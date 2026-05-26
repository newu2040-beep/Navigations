package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.viewmodel.GestureViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(viewModel: GestureViewModel) {
    val serviceRunning by viewModel.isBackgroundServiceRunning.collectAsStateWithLifecycle()
    val overlaySystemActive by viewModel.isOverlaySystemActive.collectAsStateWithLifecycle()
    val gamingMode by viewModel.isGamingMode.collectAsStateWithLifecycle()
    val sleepMode by viewModel.isSleepMode.collectAsStateWithLifecycle()

    val flashlightActive by viewModel.flashlightActive.collectAsStateWithLifecycle()
    val musicPlaying by viewModel.musicPlaying.collectAsStateWithLifecycle()
    val musicTrack by viewModel.musicTrack.collectAsStateWithLifecycle()
    val screenshotFlashed by viewModel.screenshotFlashed.collectAsStateWithLifecycle()
    val activeCameraLens by viewModel.activeCameraLensSimulator.collectAsStateWithLifecycle()
    val activeChainSteps by viewModel.activeSimulatingActionList.collectAsStateWithLifecycle()

    val stats by viewModel.aggregatedStats.collectAsStateWithLifecycle()
    val gestures by viewModel.listGestures.collectAsStateWithLifecycle()

    val themeName by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val isAmoled by viewModel.isAmoled.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                FuturisticHeader(
                    title = "Dashboard",
                    subtitle = "Central Gesture Interaction & Integration Cockpit"
                ) {
                    // Fast Theme Indicator Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = themeName,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Quick Status Toggles Block (NeoGlass grid)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "CORE ENGINES STATUS",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusFeatureCard(
                            title = "Engine",
                            subtitle = if (serviceRunning) "ACTIVE" else "STANDBY",
                            icon = Icons.Default.Settings,
                            isActive = serviceRunning,
                            modifier = Modifier.weight(1f).testTag("service_engine_card")
                        ) {
                            viewModel.toggleBackgroundService()
                        }

                        StatusFeatureCard(
                            title = "Overlay",
                            subtitle = if (overlaySystemActive) "DISPLAYED" else "DISABLED",
                            icon = Icons.Default.Layers,
                            isActive = overlaySystemActive,
                            modifier = Modifier.weight(1f).testTag("overlay_system_card")
                        ) {
                            viewModel.toggleOverlaySystem()
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusFeatureCard(
                            title = "Gaming Mode",
                            subtitle = if (gamingMode) "LOW LATENCY" else "RESTING",
                            icon = Icons.Default.SportsEsports,
                            isActive = gamingMode,
                            modifier = Modifier.weight(1f).testTag("gaming_mode_card")
                        ) {
                            viewModel.toggleGamingMode()
                        }

                        StatusFeatureCard(
                            title = "Sleep Automation",
                            subtitle = if (sleepMode) "SILENT" else "STANDBY",
                            icon = Icons.Default.NightsStay,
                            isActive = sleepMode,
                            modifier = Modifier.weight(1f).testTag("sleep_mode_card")
                        ) {
                            viewModel.toggleSleepMode()
                        }
                    }
                }
            }

            // Live Simulator Widgets Dashboard (Reacts directly to simulated system actions!)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "INTEGRATION SIMULATORS (REAL-TIME HUD)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GlassCard {
                        // Action: Flashlight State Simulator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (flashlightActive) Color(0xFFFFB703).copy(alpha = 0.15f) else Color.Transparent)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (flashlightActive) Color(0xFFFFB703) else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (flashlightActive) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                                    contentDescription = "Flashlight",
                                    tint = if (flashlightActive) Color.Black else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dynamic Device Flashlight", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (flashlightActive) "TACTICAL TORCH RUNNING" else "STANDBY (Trigger via Back Tap / V gesture)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            Switch(
                                checked = flashlightActive,
                                onCheckedChange = { viewModel.triggerVibrate(50); viewModel.triggerGestureRecognizedEvent("V") }
                            )
                        }

                        Divider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

                        // Action: Music Player Overlay Widget
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (musicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Music controller",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { viewModel.triggerGestureRecognizedEvent("M") }
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = musicTrack,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (musicPlaying) "PLAYING VIA DEEP SYNTH ENGINE" else "PAUSED (Trigger via M gesture)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }

                            Box(modifier = Modifier.width(60.dp).fillMaxHeight()) {
                                AnimatedWaveform(isPlaying = musicPlaying, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Realtime running chain rule panel
            if (activeChainSteps.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "ACTIVE AUTOMATION SEQUENCE EXECUTING",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        GlassCard(borderColor = MaterialTheme.colorScheme.tertiary) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                activeChainSteps.forEach { step ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Cached,
                                            contentDescription = "Loading step",
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = step, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Analytics Widget (Success arc speedometer & trigger bar stats)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "GESTURE HEATMAP ANALYTICS",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular gauge Canvas
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val arcPercent = 0.94f // 94% accuracy score
                                val primaryRef = MaterialTheme.colorScheme.primary
                                val secondaryRef = MaterialTheme.colorScheme.secondary

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Background Arc
                                    drawArc(
                                        color = Color.White.copy(alpha = 0.08f),
                                        startAngle = 135f,
                                        sweepAngle = 270f,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )

                                    // Score Arc
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(primaryRef, secondaryRef, primaryRef)
                                        ),
                                        startAngle = 135f,
                                        sweepAngle = 270f * arcPercent,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "94%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "RECOG LEVEL",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "System Accuracy: OPTIMIZED",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Resampling 32 spatial anchor coordinates per gesture sweep for millisecond trace recognition.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

                        // Trigger counts breakdown
                        if (stats.isEmpty()) {
                            Text(
                                text = "Zero traces recorded yet. Draw template codes to seed stats.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "MOST TRIGGERED INTERACTION POINTS",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            stats.take(4).forEach { stat ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = when (stat.category) {
                                                    "Back Tap" -> Icons.Default.TouchApp
                                                    "Motion Tap" -> Icons.Default.DirectionsRun
                                                    else -> Icons.Default.Gesture
                                                },
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = stat.identifier,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = "${stat.count} ticks",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Mini bar percent representation
                                    val percentWidth = (stat.count.toFloat() / (stats.maxOfOrNull { it.count } ?: 1)).coerceIn(0.1f, 1.0f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(percentWidth)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            MaterialTheme.colorScheme.primary,
                                                            MaterialTheme.colorScheme.secondary
                                                        )
                                                    )
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Diagnostics Setup Recalibration Card
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "DIAGNOSTICS & SYSTEM SETUP",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    GlassCard {
                        Column {
                            Text(
                                "System Alignment Calibration",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Relaunch the cockpit introduction setup process to review permission declarations and recalibrate HUD gestures.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Button(
                                onClick = { viewModel.resetOnboarding() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .testTag("reset_onboarding_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("RE-RUN INTRO SETUP", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Screenshot Flash Effect Container
        AnimatedVisibility(
            visible = screenshotFlashed,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Set off flash timer close
                LaunchedEffect(screenshotFlashed) {
                    if (screenshotFlashed) {
                        kotlinx.coroutines.delay(120)
                        viewModel.clearScreenshotFlash()
                    }
                }
            }
        }

        // Float screenshot popup banner (sliding up from bottom right contextually)
        AnimatedVisibility(
            visible = screenshotFlashed,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        ) {
            GlassCard(
                modifier = Modifier.width(180.dp),
                borderColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capture Saved", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerHUDNotification("Sharing Frame..."); viewModel.triggerVibrate(30) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                    ) {
                        Text("SHARE", fontSize = 9.sp, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.triggerHUDNotification("Annotation Tool Open..."); viewModel.triggerVibrate(30) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                    ) {
                        Text("EDIT", fontSize = 9.sp, color = Color.White)
                    }
                }
            }
        }

        // Camera simulated mechanical lens viewfinder
        AnimatedVisibility(
            visible = activeCameraLens,
            enter = scaleIn(initialScale = 0.5f) + fadeIn(),
            exit = scaleOut(targetScale = 0.5f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val rotationInfinite = rememberInfiniteTransition(label = "LensRotation")
            val angleStep by rotationInfinite.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotate"
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                // Outer mechanical dial
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(angleStep)
                ) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        style = Stroke(width = 8.dp.toPx())
                    )

                    // Draw grid reference notches
                    for (i in 0 until 12) {
                        val angle = Math.toRadians((i * 30).toDouble())
                        val cos = Math.cos(angle).toFloat()
                        val sin = Math.sin(angle).toFloat()

                        drawLine(
                            color = Color(0xFF00FFCC),
                            start = Offset(center.x + cos * 100f, center.y + sin * 100f),
                            end = Offset(center.x + cos * 115f, center.y + sin * 115f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Simulated Lens",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                viewModel.triggerVibrate(80)
                                viewModel.triggerHUDNotification("Snapshot Captured!")
                                scope.launch {
                                    viewModel.triggerGestureRecognizedEvent("S")
                                    viewModel.clearCameraLens()
                                }
                            }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AUGMENTED HUD FOCUS",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap lens icon to take shot",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                // Close widget button
                IconButton(
                    onClick = { viewModel.clearCameraLens() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .size(28.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Lens", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun StatusFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isActive) {
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.04f),
                            Color.White.copy(alpha = 0.01f)
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(20.dp)
            )
            .clickable {
                scope.launch {
                    scale.animateTo(0.95f, tween(100))
                    scale.animateTo(1f, tween(100))
                }
                onClick()
            }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )

                // Led indicator dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            if (isActive) Color(0xFF00FFCC) else Color.Red.copy(alpha = 0.5f),
                            CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
