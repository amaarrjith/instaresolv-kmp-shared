package org.example.project.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ImageAnalyzerResponse(
    @SerialName("imageDescription")
    val imageDescription: String
)
