package com.wwf.projectmanagement.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Staggered entrance: fades and slides content up the first time it appears. [index] delays the
 * animation so successive blocks reveal one after another (dashboard style).
 */
@Composable
fun Modifier.reveal(index: Int, enabled: Boolean = true): Modifier {
    var shown by remember { mutableStateOf(!enabled) }
    LaunchedEffect(Unit) { shown = true }
    val progress by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(durationMillis = 450, delayMillis = index * 70),
        label = "reveal",
    )
    return graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * 24.dp.toPx()
    }
}
