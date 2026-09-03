package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.wwf.projectmanagement.ui.theme.DarkBgEnd
import com.wwf.projectmanagement.ui.theme.DarkBgMid
import com.wwf.projectmanagement.ui.theme.DarkBgStart
import com.wwf.projectmanagement.ui.theme.HomeGradientEnd
import com.wwf.projectmanagement.ui.theme.HomeGradientMid
import com.wwf.projectmanagement.ui.theme.HomeGradientStart
import com.wwf.projectmanagement.ui.theme.PrimaryGreen

/** Page background from `.home-container` + `.hero-background` radial glow. */
@Composable
fun PageBackground(content: @Composable () -> Unit) {
    val bg = MaterialTheme.colorScheme.background
    val dark = (0.299f * bg.red + 0.587f * bg.green + 0.114f * bg.blue) < 0.5f
    val gradient = if (dark) listOf(DarkBgStart, DarkBgMid, DarkBgEnd)
    else listOf(HomeGradientStart, HomeGradientMid, HomeGradientEnd)

    Box(Modifier.fillMaxSize().background(Brush.linearGradient(gradient))) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(PrimaryGreen.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(0.2f, 0.3f),
                    radius = Float.POSITIVE_INFINITY,
                ),
            ),
        )
        content()
    }
}
