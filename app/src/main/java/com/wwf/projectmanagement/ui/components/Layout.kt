package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.WindowSize
import com.wwf.projectmanagement.ui.WindowWidthClass
import com.wwf.projectmanagement.ui.rememberWindowSize

/** Max content width, matching `.hero-content { max-width: 1200px }` on the website. */
val MaxContentWidth = 1200.dp
val MaxSubtitleWidth = 800.dp

/** Measures the window and exposes [LocalWindowSize] to its content. */
@Composable
fun WindowSizeProvider(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowSize = rememberWindowSize(maxWidth, maxHeight)
        CompositionLocalProvider(LocalWindowSize provides windowSize, content = content)
    }
}

fun WindowSize.scaledSp(compact: TextUnit, medium: TextUnit, expanded: TextUnit): TextUnit =
    when (widthClass) {
        WindowWidthClass.Compact -> compact
        WindowWidthClass.Medium -> medium
        WindowWidthClass.Expanded -> expanded
    }
