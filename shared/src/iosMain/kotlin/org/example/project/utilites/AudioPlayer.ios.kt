package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.play
import platform.AVFoundation.pause
import platform.AVFoundation.seekToTime
import platform.AVFAudio.*
import platform.AVFoundation.*
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL
import kotlinx.cinterop.CValue

@OptIn(ExperimentalForeignApi::class)
actual class AudioPlayer {
    private var avPlayer: AVPlayer? = null
    private var isPlaying = false
    private var currentFilePath: String? = null

    actual fun prepare(filePath: String) {
        if (currentFilePath != filePath || avPlayer == null) {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, error = null)
            audioSession.setActive(true, error = null)

            val url = if (filePath.startsWith("http")) {
                NSURL.URLWithString(filePath)!!
            } else {
                NSURL.fileURLWithPath(filePath)
            }
            avPlayer = AVPlayer(uRL = url)
            currentFilePath = filePath
        }
    }

    actual fun play(filePath: String) {
        if (isPlaying) {
            pause()
        }
        
        try {
            prepare(filePath)
            avPlayer?.play()
            isPlaying = true
        } catch (e: Exception) {
            e.printStackTrace()
            isPlaying = false
        }
    }

    actual fun pause() {
        avPlayer?.let {
            it.pause()
            isPlaying = false
        }
    }

    actual fun stop() {
        avPlayer?.let {
            it.pause()
        }
        avPlayer = null
        isPlaying = false
    }

    actual fun isPlaying(): Boolean {
        return isPlaying
    }

    actual fun getDuration(): Long {
        val duration = avPlayer?.currentItem?.duration
        if (duration != null) {
            @Suppress("UNCHECKED_CAST")
            val seconds = CMTimeGetSeconds(duration as CValue<CMTime>)
            if (!seconds.isNaN()) {
                return (seconds * 1000).toLong()
            }
        }
        return 0L
    }

    actual fun getCurrentPosition(): Long {
        val time = avPlayer?.currentTime()
        if (time != null) {
            @Suppress("UNCHECKED_CAST")
            val seconds = CMTimeGetSeconds(time as CValue<CMTime>)
            if (!seconds.isNaN()) {
                return (seconds * 1000).toLong()
            }
        }
        return 0L
    }

    actual fun seekTo(position: Long) {
        avPlayer?.seekToTime(CMTimeMakeWithSeconds(position.toDouble() / 1000.0, 1000))
    }
}

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    return remember { AudioPlayer() }
}
