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
    val answer: String = ""
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

@Serializable
data class CreatePermitDraftRequest(
    val draftId: Long? = null,
    val userId: Int,
    val permitTypeId: Int,
    val permitType: PermitTypeItem? = null,
    val selectedProject: Project? = null,
    val selectedUser: GroupUser? = null,
    val permitDateMillis: Long? = null,
    val startTime: String = "",
    val endTime: String = "",
    val certificateValidityAnswers: Map<Int, String> = emptyMap(),
    val generalConditionAnswers: Map<Int, String> = emptyMap(),
    val generalConditionRemarks: Map<Int, String> = emptyMap(),
    val signatureUrl: String? = null,
    val signatureDateMillis: Long? = null,
    val signatureTime: String = "",
    val reportedBy: String = "",
    val contractorName: String = "",
    val createdAt: String? = null
)
