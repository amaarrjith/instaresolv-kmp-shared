package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateObservationRequest(
    val location: String = "",
    val imageDescription: List<ImageDescriptionRequest> = emptyList(),
    val groupSpecified: Int = -1,
    val groupId: Int = -1,
    val reportedBy: String = "",
    val customResponsiblePerson: CustomResponsiblePersonRequest = CustomResponsiblePersonRequest(),
    val notificationTo: List<Int> = emptyList(),
    val saveAsDraft: Boolean = false,
    val description: String = "",
    val observationId: Int = -1,
    val responsiblePerson: Int? = null,
    val responsiblePersonName: String = "",
    val observationTitle: String = "",
    val responsiblePersonEmail: String = ""
)

@Serializable
data class ImageDescriptionRequest(
    val isAiGeneratedDescription: Boolean = false,
    val image: String = "",
    val description: String = "",
    val imageCount: Int = 1
)

@Serializable
data class CustomResponsiblePersonRequest(
    val name: String = ""
)

@Serializable
data class CreateObservationResponse(
    val observationId: Int,
    val statusMessage: String
)
