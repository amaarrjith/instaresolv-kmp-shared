package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuditItemsResponse(
    val updatedTime: String? = null,
    val contents: List<AuditItemContent> = emptyList()
)

@Serializable
data class AuditItemContent(
    val auditItemId: Int,
    val auditItemTitle: String? = null,
    val image: String? = null,
    val formUpdatedTime: String? = null
)
