package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundGlow")
    val animOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val backgroundThemeColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundThemeColor)
            .drawBehind {
                // Atmos Orb 1 (Top-Left): bg-indigo-600/20 rounded-full blur-[80px]
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF4F46E5).copy(alpha = 0.18f), Color.Transparent),
                        radius = 850f
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        x = -150f + (animOffset1 - 500f) / 4f,
                        y = -150f + (animOffset1 - 500f) / 10f
                    )
                )

                // Atmos Orb 2 (Center-Right): bg-purple-600/20 rounded-full blur-[60px]
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF9333EA).copy(alpha = 0.18f), Color.Transparent),
                        radius = 700f
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        x = size.width + 150f - (animOffset1 - 500f) / 5f,
                        y = size.height * 0.45f + (animOffset1 - 500f) / 8f
                    )
                )

                // Atmos Orb 3 (Bottom-Left/Center): bg-emerald-500/10 rounded-full blur-[100px]
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF10B981).copy(alpha = 0.08f), Color.Transparent),
                        radius = 1000f
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        x = size.width * 0.25f,
                        y = size.height + 150f
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 16.dp,
    cornerRadius: Dp = 28.dp, // Extract rounded-[28px] from HTML Design Card
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.White.copy(alpha = 0.08f), // Extract border-white/[0.08] from HTML Design Card
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(cornerRadius),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.03f), // Extract bg-white/[0.03] from HTML Design Card
                        Color.White.copy(alpha = 0.01f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        borderColor.copy(alpha = 0.03f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun FuturisticHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (RowScope.() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "EngineActivePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Glowing Pulse Active Indicator (Extracted from HTML top-header area)
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Color(0xFF34D399).copy(alpha = alpha), CircleShape)
                        .border(1.dp, Color(0xFF34D399), CircleShape)
                )
                Text(
                    text = "ENGINE ACTIVE  •  $subtitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF34D399).copy(alpha = 0.9f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
        if (trailing != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                trailing()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FloatingHUDNotification(
    message: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 72.dp),
            contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = slideInVertically(initialOffsetY = { -150 }) + fadeIn() + scaleIn(initialScale = 0.9f),
            exit = slideOutVertically(targetOffsetY = { -150 }) + fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Row(
                modifier = Modifier
                    .shadow(16.dp, RoundedCornerShape(50.dp))
                    .clip(RoundedCornerShape(50.dp))
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        RoundedCornerShape(50.dp)
                    )
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Glowing Cyber Indicator Dot
                val transition = rememberInfiniteTransition(label = "DotGlow")
                val pulseRatio by transition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = EaseInOutBack),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = pulseRatio),
                            RoundedCornerShape(50.dp)
                        )
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun AnimatedWaveform(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val barCount = 12
    val animValues = List(barCount) {
        remember { Animatable(0.2f) }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val random = java.util.Random()
            while (true) {
                animValues.forEach { anim ->
                    anim.animateTo(
                        targetValue = 0.1f + random.nextFloat() * 0.9f,
                        animationSpec = tween(
                            durationMillis = 180 + random.nextInt(120),
                            easing = LinearEasing
                        )
                    )
                }
            }
        } else {
            animValues.forEach { anim ->
                anim.animateTo(0.15f, tween(300))
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        animValues.forEach { value ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(value.value)
                    .background(color, RoundedCornerShape(10.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            )
        }
    }
}
