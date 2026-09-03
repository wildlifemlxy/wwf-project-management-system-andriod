package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.WindowHeightClass
import com.wwf.projectmanagement.ui.theme.ForestGreen
import com.wwf.projectmanagement.ui.theme.PrimaryBlue
import com.wwf.projectmanagement.ui.theme.PrimaryGreen

/** `.hero-title`: gradient text, clamp(2.5rem, 5vw, 4rem). */
@Composable
fun HeroTitle(text: String) {
    val window = LocalWindowSize.current
    val size = if (window.heightClass == WindowHeightClass.Compact) 34.sp
    else window.scaledSp(34.sp, 46.sp, 58.sp)
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall.copy(
            fontSize = size,
            lineHeight = size * 1.2f,
            fontWeight = FontWeight.ExtraBold,
            brush = Brush.linearGradient(listOf(PrimaryGreen, ForestGreen, PrimaryBlue)),
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.semantics { heading() },
    )
}
