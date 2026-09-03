package com.wwf.projectmanagement.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import com.wwf.projectmanagement.ui.LocalWindowSize
import kotlin.math.absoluteValue

/** Signed distance of [page] from the viewport centre, in pages (0 = fully visible). */
internal fun PagerState.offsetFor(page: Int): Float = (currentPage - page) + currentPageOffsetFraction

/**
 * One swipe page: content is centred in the viewport and becomes vertically scrollable when it
 * is taller than the window. Fades and shrinks slightly while being swiped away for a smooth,
 * modern transition. Gutters and system insets are respected.
 */
@Composable
fun SwipePage(pagerState: PagerState, page: Int, content: @Composable () -> Unit) {
    val window = LocalWindowSize.current
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val direction = LocalLayoutDirection.current

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                val distance = pagerState.offsetFor(page).absoluteValue.coerceIn(0f, 1f)
                alpha = 1f - 0.5f * distance
                val scale = 1f - 0.08f * distance
                scaleX = scale
                scaleY = scale
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = maxHeight)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = insets.calculateStartPadding(direction) + window.pagePadding,
                    end = insets.calculateEndPadding(direction) + window.pagePadding,
                    top = insets.calculateTopPadding() + window.sectionSpacing,
                    bottom = window.sectionSpacing,
                ),
        ) {
            content()
        }
    }
}
