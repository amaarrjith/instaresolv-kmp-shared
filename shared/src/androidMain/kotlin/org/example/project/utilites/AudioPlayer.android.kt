package org.example.project.utilites

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentFilePath: String? = null

    private var isPrepared = false

    actual fun prepare(filePath: String) {
        if (currentFilePath != filePath || mediaPlayer == null) {
            try {
                mediaPlayer?.release()
            } catch (_: Exception) {}
            isPrepared = false
            isPlaying = false
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                setOnPreparedListener { 
                    isPrepared = true 
                }
                setOnErrorListener { mp, _, _ ->
                    this@AudioPlayer.isPrepared = false
                    this@AudioPlayer.isPlaying = false
                    try {
                        mp.reset()
                    } catch (_: Exception) {}
                    true
                }
                prepareAsync()
                setOnCompletionListener {
                    this@AudioPlayer.isPlaying = false
                    try {
                        this.seekTo(0)
                    } catch (_: Exception) {}
                }
            }
            currentFilePath = filePath
        }
    }

    actual fun play(filePath: String) {
        if (isPlaying) {
            pause()
        }
        
        try {
            val wasPrepared = isPrepared && currentFilePath == filePath
            prepare(filePath)
            if (wasPrepared && isPrepared) {
                mediaPlayer?.start()
                isPlaying = true
            } else {
                mediaPlayer?.setOnPreparedListener { mp ->
                    isPrepared = true
                    try {
                        mp.start()
                        this@AudioPlayer.isPlaying = true
                    } catch (e: Exception) {
                        this@AudioPlayer.isPlaying = false
                    }
                }
            }
        } catch (e: Exception) {
            isPlaying = false
        }
    }

    actual fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    isPlaying = false
                }
            }
        } catch (_: Exception) {
            isPlaying = false
        }
    }

    actual fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
        isPlaying = false
        isPrepared = false
        currentFilePath = null
    }

    actual fun isPlaying(): Boolean = isPlaying

    actual fun getDuration(): Long {
        if (!isPrepared) return 0L
        return try {
            mediaPlayer?.duration?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    actual fun getCurrentPosition(): Long {
        if (!isPrepared) return 0L
        return try {
            mediaPlayer?.currentPosition?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    actual fun seekTo(position: Long) {
        if (isPrepared) {
            mediaPlayer?.seekTo(position.toInt())
        }
    }
}

@Composable
actual fun rememberAudioPlayer(): AudioPlayer {
    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }
    androidx.compose.runtime.DisposableEffect(audioPlayer) {
        onDispose {
            audioPlayer.stop()
        }
    }
    return audioPlayer
}

