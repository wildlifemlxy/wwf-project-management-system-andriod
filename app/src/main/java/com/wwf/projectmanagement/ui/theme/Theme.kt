package com.wwf.projectmanagement.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = ForestGreenDark,
    secondary = PrimaryBlue,
    onSecondary = Color.White,
    tertiary = ForestGreen,
    background = BgSecondary,
    onBackground = TextPrimary,
    surface = BgPrimary,
    onSurface = TextPrimary,
    surfaceVariant = HomeGradientMid,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = ForestGreenDark,
    primaryContainer = ForestGreen,
    onPrimaryContainer = Color.White,
    secondary = PrimaryBlue,
    onSecondary = Color.Black,
    tertiary = PrimaryGreen,
    background = DarkBgMid,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF334155),
)

@Composable
fun WwfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Fixed brand palette so the app matches the WWF Project Platform website on every device.
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, typography = WwfTypography, content = content)
}
