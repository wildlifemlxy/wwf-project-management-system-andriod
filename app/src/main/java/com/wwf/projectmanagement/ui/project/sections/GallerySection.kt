package com.wwf.projectmanagement.ui.project.sections

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.data.remote.GalleryMedia
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.components.ActionButton
import com.wwf.projectmanagement.ui.components.ActionButtonStyle
import com.wwf.projectmanagement.ui.components.isDarkTheme
import com.wwf.projectmanagement.ui.components.pressScale
import com.wwf.projectmanagement.ui.components.reveal
import com.wwf.projectmanagement.ui.project.gallery.DownloadProgress
import com.wwf.projectmanagement.ui.project.gallery.FullScreenGallery
import com.wwf.projectmanagement.ui.project.gallery.GalleryUiState
import com.wwf.projectmanagement.ui.project.gallery.GalleryViewModel
import com.wwf.projectmanagement.ui.project.gallery.GalleryViewModelFactory
import com.wwf.projectmanagement.ui.project.gallery.MediaTile
import com.wwf.projectmanagement.ui.theme.PrimaryGreen

/** `.gallery-filter-buttons`: All / Photos / Videos, matched on the backend's MIME type. */
private enum class MediaFilter(val labelRes: Int) {
    All(R.string.gallery_filter_all),
    Photos(R.string.gallery_filter_photos),
    Videos(R.string.gallery_filter_videos);

    fun accepts(media: GalleryMedia) = when (this) {
        All -> true
        Photos -> !media.isVideo
        Videos -> media.isVideo
    }
}

private val TileGap = 12.dp

/**
 * `.gallery-wrapper`: "Gallery" header, media filter chips and a responsive grid (2 columns on
 * phones, 4 on wider screens) of the photos and videos the WWF backend serves for [projectId]
 * (Straw-headed Bulbul: `POST /gallery`; Rifle Range Road: survey `Image URL`s), every item
 * shown at once. Tapping a tile opens the full-screen viewer, which swipes between items,
 * zooms photos and plays videos.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GallerySection(projectId: String, modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: GalleryViewModel = viewModel(key = projectId, factory = GalleryViewModelFactory(application, projectId))
    val window = LocalWindowSize.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var filter by rememberSaveable { mutableStateOf(MediaFilter.All) }
    var openIndex by rememberSaveable { mutableIntStateOf(-1) }
    val all = (state as? GalleryUiState.Loaded)?.items.orEmpty()
    val visible = remember(all, filter) { all.filter(filter::accepts) }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.gallery_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = if (window.isCompactWidth) 28.sp else 36.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(0).semantics { heading() },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.gallery_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(1).widthIn(max = 600.dp),
        )
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.reveal(2)) {
            MediaFilter.entries.forEach { option ->
                FilterChip(
                    text = stringResource(option.labelRes),
                    selected = option == filter,
                    onClick = { filter = option },
                )
            }
        }
        Spacer(Modifier.height(window.sectionSpacing))

        when (val current = state) {
            GalleryUiState.Loading -> StatusBox(Modifier.reveal(3)) {
                DownloadProgress(percent = null)
                Spacer(Modifier.height(12.dp))
                StatusText(stringResource(R.string.gallery_loading))
            }
            is GalleryUiState.Error -> StatusBox(Modifier.reveal(3)) {
                StatusText(stringResource(R.string.gallery_error))
                Spacer(Modifier.height(16.dp))
                ActionButton(
                    text = stringResource(R.string.gallery_retry),
                    onClick = viewModel::refresh,
                    style = ActionButtonStyle.Primary,
                )
            }
            is GalleryUiState.Loaded -> if (visible.isEmpty()) {
                StatusBox(Modifier.reveal(3)) { StatusText(stringResource(R.string.gallery_empty)) }
            } else {
                val columns = if (window.isCompactWidth) 2 else 4
                BoxWithConstraints(Modifier.reveal(3).widthIn(max = 900.dp).fillMaxWidth()) {
                    // Explicit tile size so every row (including the last) has equal squares.
                    val tileSize = (maxWidth - TileGap * (columns - 1)) / columns
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(TileGap),
                        verticalArrangement = Arrangement.spacedBy(TileGap),
                        maxItemsInEachRow = columns,
                    ) {
                        visible.forEachIndexed { index, item ->
                            MediaTile(
                                media = item,
                                repository = viewModel.repository,
                                onClick = { openIndex = index },
                                modifier = Modifier.width(tileSize),
                            )
                        }
                    }
                }
            }
        }
    }

    if (openIndex in visible.indices) {
        FullScreenGallery(
            items = visible,
            startIndex = openIndex,
            repository = viewModel.repository,
            onDismiss = { openIndex = -1 },
        )
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(8.dp)
    val dark = isDarkTheme()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .pressScale(interaction, pressedScale = 0.95f)
            .clip(shape)
            .background(
                when {
                    selected -> PrimaryGreen
                    dark -> Color.White.copy(alpha = 0.08f)
                    else -> Color.White
                },
            )
            .border(BorderStroke(1.dp, if (selected) PrimaryGreen else Color(0xFFCBD5E1)), shape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Tab, onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** `.gallery-empty-state`: dashed-style box used for loading, error and "no media" messages. */
@Composable
private fun StatusBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
            .clip(shape)
            .background(if (isDarkTheme()) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f))
            .border(2.dp, Color(0xFFCBD5E1), shape)
            .padding(24.dp),
    ) {
        content()
    }
}

@Composable
private fun StatusText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
        color = Color(0xFF94A3B8),
        textAlign = TextAlign.Center,
    )
}
