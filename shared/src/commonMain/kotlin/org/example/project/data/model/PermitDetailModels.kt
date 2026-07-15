package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PermitDetailRequest(
    val permitId: Int
)



@Serializable
data class PermitDetailData(
    val permitId: Int? = null,
    val permitRequestedUser: PermitDetailUser? = null,
    val permitType: PermitDetailType? = null,
    val permitCode: String? = null,
    val permitStatus: Int? = null,
    val certificateValidity: PermitCertificateValidity? = null,
    val subContractorAuthorization: PermitSubContractorAuthorization? = null,
    val authorizationRequest: PermitAuthorizationRequest? = null,
    val permitCancelledUsers: List<PermitDetailUser>? = null,
    val permitSuspendedUsers: List<PermitDetailUser>? = null,
    val permitReactivationDetails: List<PermitActionDetails>? = null,
    val permitSuspensionDetails: List<PermitActionDetails>? = null,
    val permitCancellationDetails: PermitActionDetails? = null,
    val requestForCertificateClosure: PermitCertificateClosureRequest? = null,
    val subContractorClosure: PermitSubContractorClosure? = null,
    val certificateClosure: PermitCertificateClosure? = null,
    val facility: PermitFacilityDetail? = null,
    val createdAt: String? = null
)

@Serializable
data class PermitDetailUser(
    val uuid: String? = null,
    val userId: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val profileImage: String? = null,
    val designation: String? = null,
    val company: String? = null,
    val userType: Int? = null,
    val userRole: Int? = null,
    val projectDesignation: List<Int>? = null
)

@Serializable
data class PermitDetailType(
    val permitTypeId: Int? = null,
    val permitTypeTitle: String? = null
)

@Serializable
data class PermitFacilityDetail(
    val groupId: String? = null,
    val groupCode: String? = null,
    val groupName: String? = null,
    val groupImage: String? = null,
    val userRole: Int? = null,
    val description: String? = null,
    val isAdmin: Boolean? = null
)

@Serializable
data class PermitCertificateValidity(
    val project: PermitFacilityDetail? = null,
    val contractorName: String? = null,
    val certificateDate: String? = null,
    val validFrom: String? = null,
    val endTime: String? = null,
    val location: String? = null,
    val description: String? = null,
    val remarks: String? = null,
    val images: List<PermitImage>? = null,
    val certificateValiditySections: List<PermitValiditySectionDetail>? = null,
    val generalConditions: List<PermitGeneralConditionDetail>? = null,
    val authorizedPerson: PermitAuthorizedPerson? = null,
    val requestContractor: String? = null,
    val signatureImageUrl: String? = null,
    val requestTime: String? = null,
    val requestDate: String? = null
)

@Serializable
data class PermitImage(
    val id: Int? = null,
    val image: String? = null,
    val imageCount: Int? = null,
    val description: String? = null
)

@Serializable
data class PermitValiditySectionDetail(
    val id: Int? = null,
    val title: String? = null,
    val answer: String? = null
)

@Serializable
data class PermitGeneralConditionDetail(
    val id: Int? = null,
    val title: String? = null,
    val answer: Int? = null,
    val remarks: String? = null
)

@Serializable
data class PermitAuthorizedPerson(
    val userId: Int? = null,
    val image: String? = null,
    val name: String? = null,
    val email: String? = null,
    val role: Int? = null,
    val designation: List<Int>? = null
)

@Serializable
data class PermitSubContractorAuthorization(
    val subContractorAuthorizerName: String? = null,
    val alnasrAuthorizer: PermitAuthorizedPerson? = null,
    val signatureImageUrl: String? = null,
    val authorizationTime: String? = null,
    val authorizationDate: String? = null
)

@Serializable
data class PermitAuthorizationRequest(
    val authorizerName: String? = null,
    val msraNumber: String? = null,
    val responsibleHSEPerson: PermitAuthorizedPerson? = null,
    val signatureImageUrl: String? = null,
    val authorizedTime: String? = null,
    val authorizedDate: String? = null,
    val notes: String? = null
)

@Serializable
data class PermitActionDetails(
    val remarks: String? = null,
    val images: List<PermitImage>? = null,
    val actionedBy: PermitDetailUser? = null
)

@Serializable
data class PermitCertificateClosureRequest(
    val images: List<PermitImage>? = null,
    val remarks: String? = null,
    val signatureImageUrl: String? = null,
    val requestTime: String? = null,
    val requestDate: String? = null
)

@Serializable
data class PermitSubContractorClosure(
    val authorizerName: String? = null,
    val signatureImageUrl: String? = null,
    val closureTime: String? = null,
    val closureDate: String? = null
)

@Serializable
data class PermitCertificateClosure(
    val contractorName: String? = null,
    val signatureImageUrl: String? = null,
    val closureTime: String? = null,
    val closureDate: String? = null
)

@Serializable
data class PermitAuthorizationSubmitRequest(
    val permitId: Int,
    val authorizationRequest: PermitAuthorizationRequestPayload
)

@Serializable
data class PermitAuthorizationRequestPayload(
    val authorizerName: String,
    val msraNumber: String,
    val responsibleHSEPersonId: Int,
    val signatureImageUrl: String,
    val authorizedTime: String,
    val authorizedDate: String,
    val notes: String
)

@Serializable
data class PermitAuthorizationSubmitResponse(
    val statusMessage: String
)

enum class PermitFormUserType {
    REQUESTOR,
    AUTHORIZER,

    AUTHORIZER_VIEWER,
    CERTIFICATE_CLOSURE,
    REQUEST_FOR_CERTIFICATE_CLOSURE,
    SUBCONTRACTOR_AUTHORIZER,
    SUBCONTRACTOR_CLOSURE,
    NONE
}
