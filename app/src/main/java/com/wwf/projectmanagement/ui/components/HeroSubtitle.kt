package com.wwf.projectmanagement.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.ui.LocalWindowSize

@Composable
fun HeroSubtitle(text: String) {
    val window = LocalWindowSize.current
    Text(
        text = text,
        style = if (window.isCompactWidth) MaterialTheme.typography.bodyLarge
        else MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, lineHeight = 32.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
