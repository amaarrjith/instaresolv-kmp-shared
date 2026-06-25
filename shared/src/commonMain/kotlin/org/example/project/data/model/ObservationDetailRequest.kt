package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ObservationDetailRequest(
    val observationId: Int,
    val notificationId: Int = -1
)
