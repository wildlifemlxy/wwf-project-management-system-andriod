package com.wwf.projectmanagement.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R

/** Bottom strip: animated, tappable page dots. */
@Composable
fun PagerControls(pagerState: PagerState, onGoToPage: (Int) -> Unit, modifier: Modifier = Modifier) {
    PageIndicator(count = pagerState.pageCount, current = pagerState.currentPage, onSelect = onGoToPage, modifier = modifier)
}

/** Animated, tappable dots. */
@Composable
private fun PageIndicator(count: Int, current: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val active = infoHeadingColor()
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { index ->
            val selected = index == current
            val width by animateDpAsState(if (selected) 24.dp else 8.dp, label = "dotWidth")
            val label = stringResource(R.string.action_go_to_page, index + 1)
            // 8dp dot inside a 32dp touch target.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(width = width + 8.dp, height = 32.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = { onSelect(index) })
                    .semantics { contentDescription = label },
            ) {
                Box(
                    Modifier
                        .size(width = width, height = 8.dp)
                        .clip(CircleShape)
                        .background(if (selected) active else active.copy(alpha = 0.3f)),
                )
            }
        }
    }
}
