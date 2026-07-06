package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CustomVideoPlayer(
    url: String,
    lastPlaybackTimeSeconds: Int,
    isPlaying: Boolean,
    onProgressUpdate: (currentSeconds: Long, totalSeconds: Long) -> Unit,
    seekToSeconds: Long?,
    onSeekCompleted: () -> Unit,
    modifier: Modifier
) {
    val nsUrl = remember(url) { NSURL.URLWithString(url) }
    if (nsUrl == null) return

    val player = remember(url) {
        AVPlayer(uRL = nsUrl).apply {
            if (lastPlaybackTimeSeconds > 0) {
                val cmTime = CMTimeMakeWithSeconds(lastPlaybackTimeSeconds.toDouble(), 1)
                seekToTime(cmTime)
            }
        }
    }

    val playerViewController = remember(player) {
        AVPlayerViewController().apply {
            this.player = player
            this.showsPlaybackControls = false // Disable native controller overlay
        }
    }

    // Play / Pause side-effects
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            player.play()
        } else {
            player.pause()
        }
    }

    // Seek side-effects
    LaunchedEffect(seekToSeconds) {
        if (seekToSeconds != null) {
            val cmTime = CMTimeMakeWithSeconds(seekToSeconds.toDouble(), 1)
            player.seekToTime(cmTime)
            onSeekCompleted()
        }
    }

    // Periodic progress updates using native iOS observer
    DisposableEffect(player) {
        val interval = CMTimeMakeWithSeconds(0.25, 1000)
        val observer = player.addPeriodicTimeObserverForInterval(interval, null) { time ->
            val current = CMTimeGetSeconds(time)
            val duration = CMTimeGetSeconds(player.currentItem?.duration ?: return@addPeriodicTimeObserverForInterval)
            if (!current.isNaN() && !duration.isNaN() && duration > 0) {
                onProgressUpdate(current.toLong(), duration.toLong())
            }
        }

        onDispose {
            player.pause()
            player.removeTimeObserver(observer)
        }
    }

    UIKitView(
        factory = {
            playerViewController.view
        },
        modifier = modifier
    )
}
