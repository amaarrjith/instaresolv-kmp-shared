package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageUploadData(
    val id: String,
    val imageUrl: String
)
