package com.wwf.projectmanagement.ui.project.gallery

import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.wwf.projectmanagement.R

/**
 * Plays an in-memory gallery video with ExoPlayer's standard controls (play / pause, seek).
 * Playback only runs while [active] (the page currently shown in the viewer) and pauses when
 * the app goes to the background; the player is released when the composable leaves.
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(bytes: ByteArray, active: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember(bytes) {
        val source = ProgressiveMediaSource.Factory { ByteArrayDataSource(bytes) }
            .createMediaSource(MediaItem.fromUri(Uri.EMPTY))
        ExoPlayer.Builder(context).build().apply {
            setMediaSource(source)
            repeatMode = Player.REPEAT_MODE_OFF
            prepare()
        }
    }

    LaunchedEffect(active) {
        player.playWhenReady = active
        if (!active) player.seekTo(0)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(player, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.pause()
                Lifecycle.Event.ON_RESUME -> if (active) player.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            (LayoutInflater.from(ctx).inflate(R.layout.gallery_player_view, FrameLayout(ctx), false) as PlayerView).apply {
                this.player = player
            }
        },
        update = { it.player = player },
    )
}
