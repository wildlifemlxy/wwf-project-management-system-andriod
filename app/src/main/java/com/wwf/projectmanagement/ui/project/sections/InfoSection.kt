package com.wwf.projectmanagement.ui.project.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.data.ProjectInfo
import com.wwf.projectmanagement.data.remote.StatsLoad
import com.wwf.projectmanagement.data.remote.StatsRepository
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.components.MaxSubtitleWidth
import com.wwf.projectmanagement.ui.components.isDarkTheme
import com.wwf.projectmanagement.ui.components.reveal
import com.wwf.projectmanagement.ui.home.infoBodyColor
import com.wwf.projectmanagement.ui.home.infoHeadingColor

/**
 * `.info-section`: project background copy, the four statistics tiles and the artwork with its
 * caption overlay. Statistics come from the backend ([StatsRepository]); they show "…" while
 * loading and "—" if the request fails.
 */
@Composable
fun InfoSection(projectId: String, info: ProjectInfo, modifier: Modifier = Modifier) {
    val window = LocalWindowSize.current
    val stats by remember(projectId) { StatsRepository.get().stats(projectId) }.collectAsState()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(info.titleRes),
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
            color = infoHeadingColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(0).semantics { heading() },
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(info.descriptionRes),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 29.sp),
            color = infoBodyColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(1).widthIn(max = MaxSubtitleWidth),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(info.detailRes),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 29.sp),
            color = infoBodyColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(2).widthIn(max = MaxSubtitleWidth),
        )
        Spacer(Modifier.height(window.sectionSpacing))
        StatsGrid(stats = stats, showLocations = info.showLocations, modifier = Modifier.reveal(3))
        Spacer(Modifier.height(window.sectionSpacing))
        Painting(info, modifier = Modifier.reveal(4))
    }
}

/** `.info-stats`: 2-column grid of `.stat-item` tiles (single column on narrow phones). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsGrid(stats: StatsLoad, showLocations: Boolean, modifier: Modifier = Modifier) {
    val ready = (stats as? StatsLoad.Ready)?.stats
    val placeholder = stringResource(if (stats is StatsLoad.Failed) R.string.stat_unavailable else R.string.stat_loading)
    val tiles = buildList {
        add(R.string.stat_observations to (ready?.observations ?: placeholder))
        if (showLocations) add(R.string.stat_locations to (ready?.locations ?: placeholder))
        add(R.string.stat_volunteers to (ready?.volunteers ?: placeholder))
        add(R.string.stat_years_active to (ready?.yearsActive ?: placeholder))
    }
    val dark = isDarkTheme()
    val shape = RoundedCornerShape(12.dp)
    FlowRow(
        modifier = modifier.widthIn(max = 640.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2,
    ) {
        tiles.forEach { (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .shadow(if (dark) 0.dp else 4.dp, shape)
                    .clip(shape)
                    .background(if (dark) Color.White.copy(alpha = 0.06f) else Color.White)
                    .padding(vertical = 20.dp, horizontal = 12.dp),
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** `.info-image` + `.painting-overlay`: artwork with a dark gradient caption at the bottom. */
@Composable
private fun Painting(info: ProjectInfo, modifier: Modifier = Modifier) {
    val painter = painterResource(info.paintingRes)
    val ratio = painter.intrinsicSize.let { if (it.height > 0f) it.width / it.height else 1f }
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .widthIn(max = 520.dp)
            .fillMaxWidth()
            .aspectRatio(ratio)
            .shadow(16.dp, shape)
            .clip(shape),
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(info.paintingDescriptionRes),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .padding(top = 24.dp),
        ) {
            Text(
                text = stringResource(info.captionTitleRes),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                color = Color.White,
            )
            info.captionCreditRes?.let {
                Spacer(Modifier.height(4.dp))
                Text(stringResource(it), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(info.captionBodyRes),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = Color.White.copy(alpha = 0.9f),
            )
        }
    }
}
