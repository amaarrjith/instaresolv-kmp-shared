package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.example.project.data.model.AudioUploadData
import org.example.project.data.model.TranslateAudioRequest
import org.example.project.network.ApiEndpoints
import org.example.project.network.NetworkResult
import org.example.project.network.jsonBody
import org.example.project.network.safeApiCall

class AudioApiServiceImpl(
    private val httpClient: HttpClient
) : AudioApiService {

    override suspend fun uploadAudio(
        audioBytes: ByteArray,
        type: Int
    ): NetworkResult<AudioUploadData> = safeApiCall {
        httpClient.post(ApiEndpoints.UPLOAD_AUDIO) {
            setBody(
                io.ktor.client.request.forms.MultiPartFormDataContent(
                    io.ktor.client.request.forms.formData {
                        append("type", type.toString())
                        append("audio", audioBytes, io.ktor.http.Headers.build {
                            append(io.ktor.http.HttpHeaders.ContentType, "audio/wav")
                            append(io.ktor.http.HttpHeaders.ContentDisposition, "filename=\"audio.wav\"")
                        })
                    }
                )
            )
        }
    }

    override suspend fun translateAudio(
        request: TranslateAudioRequest
    ): NetworkResult<AudioUploadData> = safeApiCall {
        httpClient.post(ApiEndpoints.TRANSLATE_AUDIO) {
            jsonBody(request)
        }
    }
}
