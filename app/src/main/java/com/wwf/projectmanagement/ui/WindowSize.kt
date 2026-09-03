package com.wwf.projectmanagement.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material window-size breakpoints. Width drives layout, so a phone in landscape or a tablet in
 * portrait each get the appropriate arrangement automatically.
 *
 * Compact  : < 600dp  (phones portrait)
 * Medium   : 600–839dp (large phones landscape, small tablets portrait, foldables)
 * Expanded : >= 840dp (tablets, landscape tablets, desktop windows)
 */
enum class WindowWidthClass { Compact, Medium, Expanded }

enum class WindowHeightClass { Compact, Medium, Expanded }

data class WindowSize(
    val width: Dp,
    val height: Dp,
) {
    val widthClass: WindowWidthClass = when {
        width < 600.dp -> WindowWidthClass.Compact
        width < 840.dp -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }

    val heightClass: WindowHeightClass = when {
        height < 480.dp -> WindowHeightClass.Compact
        height < 900.dp -> WindowHeightClass.Medium
        else -> WindowHeightClass.Expanded
    }

    val isCompactWidth get() = widthClass == WindowWidthClass.Compact
    val isExpandedWidth get() = widthClass == WindowWidthClass.Expanded
    val isLandscape get() = width > height

    /** Horizontal page padding, akin to the site's responsive section padding. */
    val pagePadding: Dp
        get() = when (widthClass) {
            WindowWidthClass.Compact -> 20.dp
            WindowWidthClass.Medium -> 32.dp
            WindowWidthClass.Expanded -> 48.dp
        }

    /** Vertical rhythm between major blocks; tightens on short (landscape phone) windows. */
    val sectionSpacing: Dp
        get() = when {
            heightClass == WindowHeightClass.Compact -> 16.dp
            widthClass == WindowWidthClass.Compact -> 24.dp
            else -> 32.dp
        }

    /** Scales a dp value between compact and expanded targets. */
    fun scaled(compact: Dp, medium: Dp, expanded: Dp): Dp = when (widthClass) {
        WindowWidthClass.Compact -> compact
        WindowWidthClass.Medium -> medium
        WindowWidthClass.Expanded -> expanded
    }
}

val LocalWindowSize = compositionLocalOf { WindowSize(360.dp, 640.dp) }

@Composable
fun rememberWindowSize(width: Dp, height: Dp): WindowSize = remember(width, height) { WindowSize(width, height) }
