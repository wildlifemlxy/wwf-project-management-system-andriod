package com.wwf.projectmanagement.ui.project.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.data.remote.GalleryMedia
import com.wwf.projectmanagement.data.remote.GalleryRepository
import com.wwf.projectmanagement.data.remote.MediaLoad
import com.wwf.projectmanagement.ui.theme.PrimaryGreen

/**
 * Lightbox over the whole screen: swipe left/right between items, pinch or double-tap to zoom
 * photos, play videos inline, tap a photo or the close button to dismiss. Shows "n / total" and
 * Items still streaming show their download percentage.
 */
@Composable
fun FullScreenGallery(
    items: List<GalleryMedia>,
    startIndex: Int,
    repository: GalleryRepository,
    onDismiss: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = startIndex) { items.size }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            securePolicy = SecureFlagPolicy.SecureOn,
        ),
    ) {
        Box(Modifier.fillMaxSize().background(Color.Black).safeDrawingPadding()) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 1) { page ->
                ViewerPage(
                    media = items[page],
                    repository = repository,
                    active = pagerState.settledPage == page,
                    onTap = onDismiss,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${items.size}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.gallery_close),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewerPage(media: GalleryMedia, repository: GalleryRepository, active: Boolean, onTap: () -> Unit) {
    val load by remember(media.id) { repository.load(media) }.collectAsState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = load) {
            is MediaLoad.Ready -> if (media.isVideo) {
                VideoPlayer(
                    bytes = state.bytes,
                    active = active,
                    modifier = Modifier.fillMaxSize().padding(vertical = 72.dp),
                )
            } else {
                val bitmap by rememberDecodedMedia(media.id, state.bytes, isVideo = false, MediaDecoder.FULL_SIZE)
                bitmap?.let { ZoomablePhoto(bitmap = it, title = media.title, onTap = onTap) }
                    ?: DownloadProgress(percent = null, ringSize = 72, tint = Color.White)
            }
            is MediaLoad.Downloading -> DownloadProgress(percent = state.percent, ringSize = 88, tint = Color.White)
            is MediaLoad.Idle -> DownloadProgress(percent = null, ringSize = 72, tint = Color.White)
            is MediaLoad.Failed -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Text(
                    text = stringResource(R.string.gallery_error),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                IconButton(onClick = { repository.retry(media) }) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.gallery_retry), tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                }
            }
        }
    }
}

/** One photo in the viewer: pinch to zoom (1x-4x), drag to pan while zoomed, double-tap toggles. */
@Composable
private fun ZoomablePhoto(bitmap: ImageBitmap, title: String, onTap: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset = if (scale > 1f) offset + panChange else Offset.Zero
    }
    Image(
        bitmap = bitmap,
        contentDescription = title,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2.5f
                        offset = Offset.Zero
                    },
                )
            }
            // Only pan while zoomed in so horizontal swipes reach the pager at 1x.
            .transformable(transformState, canPan = { scale > 1f })
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
            .padding(horizontal = 16.dp, vertical = 72.dp),
    )
}
