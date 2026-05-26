package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import kotlinx.coroutines.delay

@Composable
fun AppOnboardingScreen(
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }

    // Dynamic state trackers for permissions
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    var hasOverlayPermission by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }

    // Refresh overlay state when screen is clicked or resumed
    LaunchedEffect(currentStep, hasOverlayPermission) {
        while (currentStep == 2) {
            hasOverlayPermission = Settings.canDrawOverlays(context)
            delay(1000)
        }
    }

    // Modern custom scale and alpha intro animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("onboarding_card"),
                cornerRadius = 32.dp,
                borderColor = Color.White.copy(alpha = 0.12f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern step indicators (Pills)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        for (i in 1..3) {
                            val active = i == currentStep
                            Box(
                                modifier = Modifier
                                    .width(if (active) 32.dp else 12.dp)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (active) MaterialTheme.colorScheme.primary
                                        else Color.White.copy(alpha = 0.15f)
                                    )
                                    .animateContentSize()
                            )
                        }
                    }

                    // Content Switcher based on steps
                    when (currentStep) {
                        1 -> WelcomeStepView(
                            onNext = { currentStep = 2 }
                        )
                        2 -> PermissionsStepView(
                            context = context,
                            hasNotificationPermission = hasNotificationPermission,
                            hasOverlayPermission = hasOverlayPermission,
                            onNotificationRequest = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    hasNotificationPermission = true // fallback handled by dialog launcher
                                }
                            },
                            onOverlayRequest = {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            },
                            onNext = { currentStep = 3 },
                            onNotificationStateChanged = { hasNotificationPermission = it }
                        )
                        3 -> ReadyStepView(
                            onFinish = onCompleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeStepView(
    onNext: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scaleMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Icon Container showing off our newly created premium generated app icon!
        Box(
            modifier = Modifier
                .size(110.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulse Glow Background
            Box(
                modifier = Modifier
                    .size(90.dp * scaleMultiplier)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Custom generated launcher logo visualizer
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground_asset_1779801540802),
                contentDescription = "Navigations HUD Launcher Icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "HUD NAVIGATIONS",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "COCKPIT SYNAPSE ONBOARDING",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to HUD Navigations. Harness custom sensor sweeps, automated tracing gestures, and overlay control bubbles to access high-speed actions with absolute zero latency.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("welcome_next_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("INITIALIZE PROTOCOLS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun PermissionsStepView(
    context: Context,
    hasNotificationPermission: Boolean,
    hasOverlayPermission: Boolean,
    onNotificationRequest: () -> Unit,
    onOverlayRequest: () -> Unit,
    onNotificationStateChanged: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    // Permission dialog launcher
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onNotificationStateChanged(isGranted)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "PERMISSION HARMONY",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            color = Color.White
        )
        Text(
            text = "Grant required scopes for seamless system-level performance",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Notification Permission Row Card
        PermissionCard(
            title = "HUD Dynamic Notifications",
            description = "Provides real-time system feedback overlay banners and action confirmation indicators instantly.",
            isGranted = hasNotificationPermission,
            icon = Icons.Default.Notifications,
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onNotificationRequest()
                }
            },
            modifier = Modifier.testTag("perm_notifications_card")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // System Overlay Permission Row Card
        PermissionCard(
            title = "Display Over Other Apps",
            description = "Creates the floating draw canvas capsule trigger, allowing you to trace drawing gestures seamlessly over any screens.",
            isGranted = hasOverlayPermission,
            icon = Icons.Default.FlipToFront,
            onClick = onOverlayRequest,
            modifier = Modifier.testTag("perm_overlay_card")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Auto-enabled features acknowledgment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.02f))
                .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Vibration,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "High-Sampling Haptic Engines",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Motion sensors (Back Tap) and vibrating feedback are pre-configured.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("perm_next_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("NEXT STAGE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Icon(Icons.Default.DoubleArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isGranted) Color(0xFF34D399).copy(alpha = 0.06f) else Color.White.copy(alpha = 0.04f))
            .border(
                width = 1.dp,
                color = if (isGranted) Color(0xFF34D399).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isGranted) Color(0xFF34D399).copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) Color(0xFF34D399) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Checkbox status indicator or call-to-action button
        if (isGranted) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Granted",
                tint = Color(0xFF34D399),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable { onClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "GRANT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ReadyStepView(
    onFinish: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseReady")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF34D399).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Systems Ready",
                tint = Color(0xFF34D399),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SYSTEMS ONLINE",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color(0xFF34D399)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "HUD HARMONY ENGINE ENGAGED",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = Color.White.copy(alpha = alpha)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Onboarding diagnostics are complete. The HUD is calibrated, haptic sensory feedback is linked, and overlays are fully prepared. Welcome to the cockpit.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("onboarding_complete_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF34D399)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "LAUNCH IMMERSIVE HUD",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.Black
            )
        }
    }
}
