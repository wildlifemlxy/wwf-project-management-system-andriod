package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.ui.theme.PrimaryGreen
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DateTimeFormat = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)

/** `.hero-datetime`: ticks every second, `en-SG` 24h format like the website. */
@Composable
fun LiveDateTime(modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1_000L - (System.currentTimeMillis() % 1_000L))
        }
    }
    val shape = RoundedCornerShape(8.dp)
    Text(
        text = now.format(DateTimeFormat),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(shape)
            .background(PrimaryGreen.copy(alpha = 0.15f))
            .border(1.dp, PrimaryGreen.copy(alpha = 0.25f), shape)
            .defaultMinSize(minWidth = 200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
