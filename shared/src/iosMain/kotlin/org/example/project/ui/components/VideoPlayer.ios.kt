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
import platform.Foundation.NSHTTPCookie
import platform.Foundation.NSHTTPCookieDomain
import platform.Foundation.NSHTTPCookieName
import platform.Foundation.NSHTTPCookiePath
import platform.Foundation.NSHTTPCookieValue
import platform.Foundation.NSURL
import platform.UIKit.UIView
import kotlinx.coroutines.delay

@OptIn(ExperimentalForeignApi::class)
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
    val nsUrl = remember(url) { NSURL.URLWithString(url) }
    if (nsUrl == null) return

    val player = remember(url, cookies) {
        val domain = nsUrl.host ?: ""

        // Build NSHTTPCookie list from the cookies map
        val httpCookies = cookies.mapNotNull { (name, value) ->
            NSHTTPCookie.cookieWithProperties(
                mapOf<Any?, Any?>(
                    NSHTTPCookieName to name,
                    NSHTTPCookieValue to value,
                    NSHTTPCookieDomain to domain,
                    NSHTTPCookiePath to "/"
                )
            )
        }

        // Create AVURLAsset with cookies attached so all HLS segment requests include them
        val asset = if (httpCookies.isNotEmpty()) {
            AVURLAsset.URLAssetWithURL(
                nsUrl,
                options = mapOf<Any?, Any?>(AVURLAssetHTTPCookiesKey to httpCookies)
            )
        } else {
            AVURLAsset.URLAssetWithURL(nsUrl, options = null)
        }

        val playerItem = AVPlayerItem(asset = asset)
        AVPlayer(playerItem = playerItem).apply {
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

    LaunchedEffect(player) {
        while (true) {
            val status = player.currentItem?.status
            val timeControlStatus = player.timeControlStatus
            val error = player.currentItem?.error
            
            if (error != null) {
                onIsLoadingChange(false)
                onError(error.localizedDescription ?: "Playback error")
            } else if (status == AVPlayerItemStatusFailed) {
                onIsLoadingChange(false)
                onError("Failed to load video")
            } else {
                val isLoading = timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
                onIsLoadingChange(isLoading)
            }
            delay(250)
        }
    }

    UIKitView(
        factory = {
            playerViewController.view
        },
        modifier = modifier
    )
}
