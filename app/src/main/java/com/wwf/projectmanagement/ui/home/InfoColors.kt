package com.wwf.projectmanagement.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wwf.projectmanagement.ui.components.isDarkTheme

// `.info-content h3 { color: #1a9641 }` and `.info-content p { color: #555 }` on the web (light).
// Dark mode swaps in lighter equivalents so the text stays legible on the dark gradient.
private val InfoHeadingGreen = Color(0xFF1A9641)
private val InfoHeadingGreenDark = Color(0xFF4ADE80)
private val InfoBodyGrey = Color(0xFF555555)
private val InfoBodyGreyDark = Color(0xFFCBD5E1)

@Composable
internal fun infoHeadingColor(): Color = if (isDarkTheme()) InfoHeadingGreenDark else InfoHeadingGreen

@Composable
internal fun infoBodyColor(): Color = if (isDarkTheme()) InfoBodyGreyDark else InfoBodyGrey
