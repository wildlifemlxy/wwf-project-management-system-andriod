package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Frosted-glass pill background used by the stat / tag chips; adapts to light and dark mode. */
@Composable
fun Modifier.glassPill(tint: Color = MaterialTheme.colorScheme.primary): Modifier {
    val dark = isDarkTheme()
    val fill = if (dark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.65f)
    val stroke = tint.copy(alpha = if (dark) 0.35f else 0.25f)
    return clip(CircleShape).background(fill).border(1.dp, stroke, CircleShape)
}
