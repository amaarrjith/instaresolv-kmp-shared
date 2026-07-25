package org.example.project.data.repository

import org.example.project.data.model.AudioUploadData
import org.example.project.data.model.TranslateAudioRequest
import org.example.project.data.remote.api.AudioApiService
import org.example.project.domain.repository.AudioRepository
import org.example.project.network.NetworkResult

class AudioRepositoryImpl(
    private val apiService: AudioApiService
) : AudioRepository {
    override suspend fun uploadAudio(
        audioBytes: ByteArray,
        type: Int
    ): NetworkResult<AudioUploadData> {
        return apiService.uploadAudio(audioBytes, type)
    }

    override suspend fun translateAudio(
        request: TranslateAudioRequest
    ): NetworkResult<AudioUploadData> {
        return apiService.translateAudio(request)
    }
}
