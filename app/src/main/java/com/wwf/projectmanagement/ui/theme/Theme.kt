package com.wwf.projectmanagement.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val WwfGreen = Color(0xFF0A8F4E)
private val WwfGreenDark = Color(0xFF05633A)

private val LightColors = lightColorScheme(
    primary = WwfGreen,
    onPrimary = Color.White,
    secondary = WwfGreenDark,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6FD99E),
    onPrimary = Color.Black,
    secondary = WwfGreen,
)

@Composable
fun WwfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Material You dynamic color on Android 12+, brand palette on older devices.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
