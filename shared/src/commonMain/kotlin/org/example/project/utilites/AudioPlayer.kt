package org.example.project.utilites

import androidx.compose.runtime.Composable

expect class AudioPlayer {
    fun prepare(filePath: String)
    fun play(filePath: String)
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun getDuration(): Long
    fun getCurrentPosition(): Long
    fun seekTo(position: Long)
}

@Composable
expect fun rememberAudioPlayer(): AudioPlayer
