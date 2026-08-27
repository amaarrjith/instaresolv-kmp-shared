package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.delay

@Composable
actual fun CustomVideoPlayer(
    url: String,
    lastPlaybackTimeSeconds: Int,
    isPlaying: Boolean,
    onProgressUpdate: (currentSeconds: Long, totalSeconds: Long) -> Unit,
    onIsLoadingChange: (Boolean) -> Unit,
    onError: (String) -> Unit,
    seekToSeconds: Long?,
    onSeekCompleted: () -> Unit,
    cookies: Map<String, String>,
    modifier: Modifier
) {
    val context = LocalContext.current

    val exoPlayer = remember(url) {
        // Build cookie header string from the cookies map
        val cookieHeader = cookies.entries.joinToString("; ") { "${it.key}=${it.value}" }

        val builder = ExoPlayer.Builder(context)

        if (cookieHeader.isNotEmpty()) {
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setDefaultRequestProperties(mapOf("Cookie" to cookieHeader))
            builder.setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
        }

        builder.build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            if (lastPlaybackTimeSeconds > 0) {
                seekTo(lastPlaybackTimeSeconds * 1000L)
            }
            
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    val isBuffering = playbackState == Player.STATE_BUFFERING
                    onIsLoadingChange(isBuffering)
                }

                override fun onPlayerError(error: PlaybackException) {
                    onIsLoadingChange(false)
                    onError(error.message ?: "Playback error")
                }
            })
            
            playWhenReady = isPlaying
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Handle Play / Pause from Compose State
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    // Handle Seek requests from Compose State
    LaunchedEffect(seekToSeconds) {
        if (seekToSeconds != null) {
            exoPlayer.seekTo(seekToSeconds * 1000L)
            onSeekCompleted()
        }
    }

    // Periodically update playback progress
    LaunchedEffect(exoPlayer) {
        while (true) {
            val duration = exoPlayer.duration
            val current = exoPlayer.currentPosition
            if (duration > 0) {
                onProgressUpdate(current / 1000L, duration / 1000L)
            }
            delay(250)
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false // Disable built-in controls
            }
        },
        modifier = modifier
    )
}
