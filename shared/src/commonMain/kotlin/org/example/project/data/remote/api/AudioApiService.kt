package org.example.project.data.remote.api

import org.example.project.data.model.AudioUploadData
import org.example.project.data.model.TranslateAudioRequest
import org.example.project.network.NetworkResult

interface AudioApiService {
    suspend fun uploadAudio(
        audioBytes: ByteArray,
        type: Int
    ): NetworkResult<AudioUploadData>

    suspend fun translateAudio(
        request: TranslateAudioRequest
    ): NetworkResult<AudioUploadData>
}
