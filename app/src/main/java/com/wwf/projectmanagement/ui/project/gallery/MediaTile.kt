package com.wwf.projectmanagement.ui.project.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.data.remote.GalleryMedia
import com.wwf.projectmanagement.data.remote.GalleryRepository
import com.wwf.projectmanagement.data.remote.MediaLoad
import com.wwf.projectmanagement.ui.components.isDarkTheme
import com.wwf.projectmanagement.ui.components.pressScale

/**
 * `.gallery-card-item`: square tile (radius 12) for one backend photo or video. Photos start
 * streaming as soon as the tile appears and show a progress ring with the percentage, a retry
 * affordance if the download fails, and the thumbnail once cached. Videos (10 MB+) are only
 * downloaded when opened, so their tile shows a play badge until then and a frame afterwards.
 */
@Composable
fun MediaTile(
    media: GalleryMedia,
    repository: GalleryRepository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val load by remember(media.id) { repository.load(media) }.collectAsState()
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .pressScale(interaction, pressedScale = 0.96f)
            .shadow(6.dp, shape)
            .clip(shape)
            .background(if (isDarkTheme()) Color.White.copy(alpha = 0.06f) else Color.White)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Image,
                onClick = { if (load is MediaLoad.Failed) repository.retry(media) else onClick() },
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (val state = load) {
            is MediaLoad.Ready -> {
                val bitmap by rememberDecodedMedia(media.id, state.bytes, media.isVideo, MediaDecoder.THUMB_SIZE)
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = media.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } ?: DownloadProgress(percent = null)
                if (media.isVideo) PlayBadge(Modifier.align(Alignment.Center))
            }
            is MediaLoad.Downloading -> DownloadProgress(percent = state.percent)
            is MediaLoad.Idle -> DownloadProgress(percent = null)
            is MediaLoad.Failed -> RetryHint()
        }
    }
}

/** Translucent circular play button like the web's video tiles. */
@Composable
fun PlayBadge(modifier: Modifier = Modifier, size: Int = 48) {
    Box(
        modifier
            .size(size.dp)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = stringResource(R.string.gallery_video),
            tint = Color.White,
            modifier = Modifier.size((size * 0.6f).dp),
        )
    }
}

/** Progress ring with the percentage in the middle; indeterminate when [percent] is null. */
@Composable
fun DownloadProgress(percent: Int?, modifier: Modifier = Modifier, ringSize: Int = 44, tint: Color = MaterialTheme.colorScheme.primary) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (percent == null) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(ringSize.dp),
                    color = tint,
                    strokeWidth = 3.dp,
                )
            } else {
                androidx.compose.material3.CircularProgressIndicator(
                    progress = { percent / 100f },
                    modifier = Modifier.size(ringSize.dp),
                    color = tint,
                    trackColor = tint.copy(alpha = 0.2f),
                    strokeWidth = 3.dp,
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = (ringSize / 4).sp),
                    color = tint,
                )
            }
        }
    }
}

@Composable
private fun RetryHint() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.gallery_retry),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
