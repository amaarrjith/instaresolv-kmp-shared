package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioUploadData(
    @SerialName("audioUrl")
    val audioUrl: String? = null
)
