package org.example.project.utilites

import androidx.compose.runtime.Composable

expect class AudioRecorder {
    fun startRecording()
    fun stopRecording()
    fun isRecording(): Boolean
    fun getRecordFilePath(): String?
}

@Composable
expect fun rememberAudioRecorder(): AudioRecorder
