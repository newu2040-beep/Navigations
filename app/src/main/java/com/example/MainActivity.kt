package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.NavigationsTheme
import com.example.ui.viewmodel.GestureViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GestureViewModel = viewModel()
            val themeName by viewModel.selectedTheme.collectAsStateWithLifecycle()
            val amoledActive by viewModel.isAmoled.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val hudMessage by viewModel.hudNotification.collectAsStateWithLifecycle()
            val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

            var currentTab by remember { mutableStateOf("DASHBOARD") }
            var showThemeSheet by remember { mutableStateOf(false) }

            NavigationsTheme(
                themeName = themeName,
                isDark = true, // Force premium futuristic dark experience, matching cosmic aesthetic
                isAmoled = amoledActive
            ) {
                CosmicBackground {
                    if (!onboardingCompleted) {
                        AppOnboardingScreen(
                            onCompleted = { viewModel.completeOnboarding() }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = Color.Transparent,
                            bottomBar = {
                                GlassBottomNavigation(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    onThemePressed = { showThemeSheet = true }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                // Instant Low Latency Screen Crossfade transitions
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                                    },
                                    label = "ScreenTransition"
                                ) { tab ->
                                    when (tab) {
                                        "DASHBOARD" -> DashboardScreen(viewModel = viewModel)
                                        "GESTURES" -> GesturesScreen(viewModel = viewModel)
                                        "SENSORS" -> BackTapScreen(viewModel = viewModel)
                                        "AUTOMATION" -> AutomationScreen(viewModel = viewModel)
                                        "FLOATING_HUB" -> FloatingHubScreen(viewModel = viewModel)
                                        else -> DashboardScreen(viewModel = viewModel)
                                    }
                                }

                                // Dynamic Global Overlay Notifications HUD
                                FloatingHUDNotification(
                                    message = hudMessage,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                        }

                        // Futuristic HUD Theme Changer Panel Dialog
                        if (showThemeSheet) {
                            ThemeSelectionDialog(
                                currentTheme = themeName,
                                isAmoled = amoledActive,
                                onThemeSelected = { viewModel.setSelectedTheme(it) },
                                onToggleAmoled = { viewModel.setAmoledActive(it) },
                                onDismiss = { showThemeSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onThemePressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
                .background(Color(0xFF08090C).copy(alpha = 0.85f)) // Translucent Space Black from Design HTML
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavBarItem(
                label = "HUD",
                icon = Icons.Default.Dataset,
                isSelected = currentTab == "DASHBOARD",
                onClick = { onTabSelected("DASHBOARD") },
                modifier = Modifier.testTag("nav_dashboard")
            )

            NavBarItem(
                label = "Traces",
                icon = Icons.Default.Gesture,
                isSelected = currentTab == "GESTURES",
                onClick = { onTabSelected("GESTURES") },
                modifier = Modifier.testTag("nav_gestures")
            )

            NavBarItem(
                label = "Sensors",
                icon = Icons.Default.EdgesensorHigh,
                isSelected = currentTab == "SENSORS",
                onClick = { onTabSelected("SENSORS") },
                modifier = Modifier.testTag("nav_sensors")
            )

            NavBarItem(
                label = "Macros",
                icon = Icons.Default.TrackChanges,
                isSelected = currentTab == "AUTOMATION",
                onClick = { onTabSelected("AUTOMATION") },
                modifier = Modifier.testTag("nav_automation")
            )

            NavBarItem(
                label = "Float",
                icon = Icons.Default.BubbleChart,
                isSelected = currentTab == "FLOATING_HUB",
                onClick = { onTabSelected("FLOATING_HUB") },
                modifier = Modifier.testTag("nav_floating_hub")
            )

            // Theme Settings Button
            IconButton(
                onClick = onThemePressed,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .size(36.dp)
                    .testTag("theme_picker_trigger")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Skins Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RowScope.NavBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .weight(1f)
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        // Active capsule/pill from Immersive UI design (w-14 h-8 bg-indigo-500/20)
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f),
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: String,
    isAmoled: Boolean,
    onThemeSelected: (String) -> Unit,
    onToggleAmoled: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val themeList = listOf(
        "NEON_PURPLE" to "Cyber Neon Purple",
        "LAVENDER" to "Calming Lavender",
        "MINT_GREEN" to "Refresh Mint",
        "OCEAN_BLUE" to "Deep Ocean Blue",
        "SAKURA_PINK" to "Sakura Flower Pink",
        "PEACH_ORANGE" to "Glow Peach Orange",
        "ARCTIC_WHITE" to "Norse Arctic Silver"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(10.dp))
                Text("THEMES & SKINS CONTEXT", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Select custom AMOLED or pastel ambient theme wrappers instantly:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))

                // AMOLED true-black toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("True AMOLED Black Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Low battery screens draw values", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                    Switch(checked = isAmoled, onCheckedChange = onToggleAmoled, modifier = Modifier.testTag("amoled_switch"))
                }

                // Themes listings
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    themeList.forEach { (keyword, desc) ->
                        val selected = currentTheme == keyword
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.02f))
                                .clickable { onThemeSelected(keyword) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Theme small dot
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            when (keyword) {
                                                "LAVENDER" -> Color(0xFF907FD3)
                                                "MINT_GREEN" -> Color(0xFF3EB489)
                                                "OCEAN_BLUE" -> Color(0xFF3A86C8)
                                                "SAKURA_PINK" -> Color(0xFFFFB7B2)
                                                "PEACH_ORANGE" -> Color(0xFFFF9F1C)
                                                "ARCTIC_WHITE" -> Color(0xFF81A1C1)
                                                else -> Color(0xFFA020F0)
                                            },
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(desc, fontSize = 12.sp, color = Color.White, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            }
                            RadioButton(selected = selected, onClick = { onThemeSelected(keyword) })
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("DONE")
                }
            }
        }
    )
}
