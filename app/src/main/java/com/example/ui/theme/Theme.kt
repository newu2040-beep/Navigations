package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun NavigationsTheme(
    themeName: String = "NEON_PURPLE",
    isDark: Boolean = isSystemInDarkTheme(),
    isAmoled: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseScheme = when (themeName) {
        "LAVENDER" -> {
            if (isDark) {
                darkColorScheme(
                    primary = LavenderPrimary,
                    secondary = LavenderSecondary,
                    tertiary = Color(0xFFE2D4F0),
                    background = if (isAmoled) Color.Black else LavenderDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF1B1828),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFECE7F5),
                    onSurface = Color(0xFFECE7F5)
                )
            } else {
                lightColorScheme(
                    primary = LavenderPrimary,
                    secondary = Color(0xFF7061AA),
                    tertiary = Color(0xFF55447C),
                    background = Color(0xFFFAF7FF),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF1D1B20),
                    onSurface = Color(0xFF1D1B20)
                )
            }
        }
        "MINT_GREEN" -> {
            if (isDark) {
                darkColorScheme(
                    primary = MintPrimary,
                    secondary = MintSecondary,
                    tertiary = Color(0xFFD0FFEB),
                    background = if (isAmoled) Color.Black else MintDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF101B17),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFE2F3ED),
                    onSurface = Color(0xFFE2F3ED)
                )
            } else {
                lightColorScheme(
                    primary = MintPrimary,
                    secondary = Color(0xFF267A5C),
                    tertiary = Color(0xFF10533C),
                    background = Color(0xFFF4FFF9),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF151D1A),
                    onSurface = Color(0xFF151D1A)
                )
            }
        }
        "OCEAN_BLUE" -> {
            if (isDark) {
                darkColorScheme(
                    primary = OceanBluePrimary,
                    secondary = OceanBlueSecondary,
                    tertiary = Color(0xFFD4E8F0),
                    background = if (isAmoled) Color.Black else OceanBlueDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF0F1823),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFE3EFF5),
                    onSurface = Color(0xFFE3EFF5)
                )
            } else {
                lightColorScheme(
                    primary = OceanBluePrimary,
                    secondary = Color(0xFF265A84),
                    tertiary = Color(0xFF133E60),
                    background = Color(0xFFF5FAFF),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF1B1B1F),
                    onSurface = Color(0xFF1B1B1F)
                )
            }
        }
        "SAKURA_PINK" -> {
            if (isDark) {
                darkColorScheme(
                    primary = SakuraPinkPrimary,
                    secondary = SakuraPinkSecondary,
                    tertiary = Color(0xFFFBE4E6),
                    background = if (isAmoled) Color.Black else SakuraPinkDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF231416),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFFCEBEC),
                    onSurface = Color(0xFFFCEBEC)
                )
            } else {
                lightColorScheme(
                    primary = SakuraPinkPrimary,
                    secondary = Color(0xFFB45963),
                    tertiary = Color(0xFF7A2933),
                    background = Color(0xFFFFF7F8),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF221A1B),
                    onSurface = Color(0xFF221A1B)
                )
            }
        }
        "PEACH_ORANGE" -> {
            if (isDark) {
                darkColorScheme(
                    primary = PeachPrimary,
                    secondary = PeachSecondary,
                    tertiary = Color(0xFFFAEBE2),
                    background = if (isAmoled) Color.Black else PeachDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF23180F),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFFAF2EB),
                    onSurface = Color(0xFFFAF2EB)
                )
            } else {
                lightColorScheme(
                    primary = PeachPrimary,
                    secondary = Color(0xFFB47011),
                    tertiary = Color(0xFF7A4805),
                    background = Color(0xFFFFFBF7),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF221E1A),
                    onSurface = Color(0xFF221E1A)
                )
            }
        }
        "ARCTIC_WHITE" -> {
            if (isDark) {
                darkColorScheme(
                    primary = ArcticPrimary,
                    secondary = ArcticSecondary,
                    tertiary = Color(0xFFD8DEE9),
                    background = if (isAmoled) Color.Black else ArcticDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF2E3440),
                    onPrimary = Color.Black,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFECEFF4),
                    onSurface = Color(0xFFECEFF4)
                )
            } else {
                lightColorScheme(
                    primary = ArcticPrimary,
                    secondary = Color(0xFF4C566A),
                    tertiary = Color(0xFF3B4252),
                    background = Color(0xFFF9FBFF),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF2E3440),
                    onSurface = Color(0xFF2E3440)
                )
            }
        }
        else -> { // NEON_PURPLE (default)
            if (isDark) {
                darkColorScheme(
                    primary = NeonPurplePrimary,
                    secondary = NeonPurpleSecondary,
                    tertiary = AccentGlowColor,
                    background = if (isAmoled) Color.Black else NeonPurpleDarkBg,
                    surface = if (isAmoled) Color(0xFF121212) else Color(0xFF12131C),
                    onPrimary = Color.White,
                    onSecondary = Color.Black,
                    onBackground = Color(0xFFF0E5FF),
                    onSurface = Color(0xFFF0E5FF)
                )
            } else {
                lightColorScheme(
                    primary = NeonPurplePrimary,
                    secondary = Color(0xFF7B2CBF),
                    tertiary = Color(0xFF5A189A),
                    background = Color(0xFFFCF7FF),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFF1B0B2E),
                    onSurface = Color(0xFF1B0B2E)
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = baseScheme,
        typography = Typography,
        content = content
    )
}
