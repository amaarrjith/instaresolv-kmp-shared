package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.*
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID

@OptIn(ExperimentalForeignApi::class)
actual class AudioRecorder {
    private var audioRecorder: AVAudioRecorder? = null
    private var isRecording = false
    private var currentFilePath: String? = null

    actual fun startRecording() {
        if (isRecording) return

        val audioSession = AVAudioSession.sharedInstance()
        try {
            audioSession.setCategory(AVAudioSessionCategoryRecord, error = null)
            audioSession.setActive(true, error = null)
            
            val fileName = "AudioRecord_${NSUUID().UUIDString}.wav"
            val filePath = NSTemporaryDirectory() + fileName
            currentFilePath = filePath
            
            val url = NSURL.fileURLWithPath(filePath)
            
            val settings = mapOf<Any?, Any>(
                AVFormatIDKey to platform.CoreAudioTypes.kAudioFormatLinearPCM,
                AVSampleRateKey to 44100.0,
                AVNumberOfChannelsKey to 1,
                AVLinearPCMBitDepthKey to 16,
                AVLinearPCMIsFloatKey to false,
                AVLinearPCMIsBigEndianKey to false
            )

            audioRecorder = AVAudioRecorder(url, settings, error = null)
            audioRecorder?.prepareToRecord()
            audioRecorder?.record()
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
            isRecording = false
        }
    }

    actual fun stopRecording() {
        if (!isRecording) return
        audioRecorder?.stop()
        audioRecorder = null
        isRecording = false
    }

    actual fun isRecording(): Boolean = isRecording

    actual fun getRecordFilePath(): String? = currentFilePath
}

@Composable
actual fun rememberAudioRecorder(): AudioRecorder {
    return remember { AudioRecorder() }
}
