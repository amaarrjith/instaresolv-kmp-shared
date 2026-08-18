package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadedImageData(
    val id: String? = null,
    val image: String? = null,
    val description: String? = null,
    val translatedImageDescription: String? = null,
    val isAiGeneratedDescription: Boolean? = null,
    val imageCount: Int? = null
)
