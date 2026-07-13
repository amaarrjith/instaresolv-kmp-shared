package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PermitValiditySubmitRequest(
    val permitTypeId: Int,
    val requestDate: String,
    val certificateDate: String,
    val requestTime: String,
    val validFrom: String,
    val endTime: String,
    val contractorName: String,
    val requestContractor: String,
    val signatureImageUrl: String,
    val images: List<PermitValidityImage> = emptyList(),
    val project: PermitValidityProject,
    val authorizedPersonId: Int,
    val certificateValiditySections: List<PermitValiditySection>,
    val generalConditions: List<PermitValidityCondition>,
    val remarks: String? = ""
)

@Serializable
data class PermitValidityProject(
    val groupId: String,
    val groupCode: String,
    val groupName: String,
    val groupImage: String,
    val userRole: Int,
    val isAdmin: Boolean,
    val isSelected: Boolean = true
)

@Serializable
data class PermitValiditySection(
    val id: Int,
    val title: String,
    val answer: String? = null
)

@Serializable
data class PermitValidityCondition(
    val id: Int,
    val title: String,
    val answer: Int,
    val remarks: String? = ""
)

@Serializable
data class PermitValidityImage(
    val id: Int? = null,
    val image: String? = null,
    val imageCount: Int? = null,
    val description: String? = null
)

@Serializable
data class PermitValiditySubmitResponse(
    val statusMessage: String
)
