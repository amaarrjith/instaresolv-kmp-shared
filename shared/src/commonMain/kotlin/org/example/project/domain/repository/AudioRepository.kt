package org.example.project.domain.repository

import org.example.project.data.model.AudioUploadData
import org.example.project.data.model.TranslateAudioRequest
import org.example.project.network.NetworkResult

interface AudioRepository {
    suspend fun uploadAudio(
        audioBytes: ByteArray,
        type: Int = 1
    ): NetworkResult<AudioUploadData>

    suspend fun translateAudio(
        request: TranslateAudioRequest
    ): NetworkResult<AudioUploadData>
}
