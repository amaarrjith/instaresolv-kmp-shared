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
            mediaPlayer?.release()
            isPrepared = false
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                setOnPreparedListener { 
                    isPrepared = true 
                }
                setOnErrorListener { _, _, _ ->
                    isPrepared = true 
                    true
                }
                prepareAsync()
                setOnCompletionListener {
                    this@AudioPlayer.isPlaying = false
                    this.seekTo(0)
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
            prepare(filePath)
            if (isPrepared) {
                mediaPlayer?.start()
                isPlaying = true
            } else {
                mediaPlayer?.setOnPreparedListener {
                    isPrepared = true
                    it.start()
                    isPlaying = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isPlaying = false
        }
    }

    actual fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            }
        }
    }

    actual fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        isPlaying = false
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
    return remember { AudioPlayer(context) }
}
