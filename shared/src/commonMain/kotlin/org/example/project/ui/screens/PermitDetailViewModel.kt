package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.PermitDetailData
import org.example.project.data.model.PermitDetailRequest
import org.example.project.domain.repository.PermitRepository
import org.example.project.network.NetworkResult

import org.example.project.data.model.PermitFormUserType
import org.example.project.data.settings.AuthPreferences
import org.example.project.data.model.PermitStatus
import org.example.project.domain.repository.ProjectRepository
import org.example.project.data.model.GroupUser
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.toGroupUser
import org.example.project.utilities.convertTo24HourFormat
import org.example.project.utilities.formatTimestamp
import kotlin.time.Clock

sealed class PermitDetailUiState {
    object Loading : PermitDetailUiState()
    data class Success(val data: PermitDetailData, val submitError: String? = null) : PermitDetailUiState()
    data class Error(val message: String) : PermitDetailUiState()
}

class PermitDetailViewModel(
    private val repository: PermitRepository,
    private val authPreferences: AuthPreferences,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PermitDetailUiState>(PermitDetailUiState.Loading)
    val uiState: StateFlow<PermitDetailUiState> = _uiState.asStateFlow()
    
    private val _userType = MutableStateFlow(PermitFormUserType.NONE)
    val userType: StateFlow<PermitFormUserType> = _userType.asStateFlow()

    private val _authorizerName = MutableStateFlow(authPreferences.getLoggedInUser()?.name ?: "")
    val authorizerName: StateFlow<String> = _authorizerName.asStateFlow()

    private val _hsePersons = MutableStateFlow<List<GroupUser>>(emptyList())
    val hsePersons: StateFlow<List<GroupUser>> = _hsePersons.asStateFlow()

    private val _selectedHsePerson = MutableStateFlow<GroupUser?>(null)
    val selectedHsePerson: StateFlow<GroupUser?> = _selectedHsePerson.asStateFlow()

    private val _msraNumber = MutableStateFlow("")
    val msraNumber: StateFlow<String> = _msraNumber.asStateFlow()

    private val _signatureUrl = MutableStateFlow<String?>(null)
    val signatureUrl: StateFlow<String?> = _signatureUrl.asStateFlow()

    private val _signatureDate = MutableStateFlow("")
    val signatureDate: StateFlow<String> = _signatureDate.asStateFlow()

    private val _signatureTime = MutableStateFlow("")
    val signatureTime: StateFlow<String> = _signatureTime.asStateFlow()

    private val _additionalPrecautions = MutableStateFlow("")
    val additionalPrecautions: StateFlow<String> = _additionalPrecautions.asStateFlow()

    private val _canCancelOrSuspend = MutableStateFlow(false)
    val canCancelOrSuspend: StateFlow<Boolean> = _canCancelOrSuspend.asStateFlow()

    private val _canReactivate = MutableStateFlow(false)
    val canReactivate: StateFlow<Boolean> = _canReactivate.asStateFlow()

    fun onHsePersonSelected(user: GroupUser) {
        _selectedHsePerson.value = user
    }

    fun onMsraNumberChanged(number: String) {
        _msraNumber.value = number
    }

    fun onSignatureUploaded(url: String) {
        _signatureUrl.value = url
        val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month = now.monthNumber.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')
        _signatureDate.value = "${now.year}-$month-$day"
        
        val hour = now.hour.toString().padStart(2, '0')
        val min = now.minute.toString().padStart(2, '0')
        val sec = now.second.toString().padStart(2, '0')
        _signatureTime.value = "$hour:$min:$sec"
    }

    fun onRemoveSignatureClick() {
        _signatureUrl.value = null
        _signatureDate.value = ""
        _signatureTime.value = ""
    }

    fun onAdditionalPrecautionsChanged(precautions: String) {
        _additionalPrecautions.value = precautions
    }

    private fun fetchHSEUsers(groupId: Int, groupCode: String) {
        viewModelScope.launch {
            // using designationType = 1 as default
            val request = org.example.project.data.model.PermitUserListRequest(
                groupId = groupId,
                groupCode = groupCode,
                designationType = 4
            )
            when (val result = repository.getPermitUserList(request)) {
                is NetworkResult.Success -> {
                    val mappedUsers = result.data.users.map { it.toGroupUser() }
                    _hsePersons.value = mappedUsers
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun authorizePermit(permitId: Int) {
        val authorizer = _authorizerName.value
        val msra = _msraNumber.value
        val hsePersonId = _selectedHsePerson.value?.userId ?: 0
        val signature = _signatureUrl.value ?: ""
        val date = _signatureDate.value
        val time = _signatureTime.value
        val notes = _additionalPrecautions.value
        val state = _uiState.value as? PermitDetailUiState.Success ?: return
        if (hsePersonId == 0) {
            _uiState.value = state.copy(submitError = "Please select an HSE person")
            return
        }
        if (msra.isBlank()) {
            _uiState.value = state.copy(submitError = "Please enter MSRA number")
            return
        }
        if (signature.isBlank()) {
            _uiState.value = state.copy(submitError = "Please add your signature")
            return
        }
        var formattedDate = date
        var formattedTime = time
        if (date.isNotBlank() && time.isNotBlank()) {
            try {
                val localDateTimeStr = "${date}T${time}"
                val instant = kotlinx.datetime.LocalDateTime.parse(localDateTimeStr).toInstant(kotlinx.datetime.TimeZone.currentSystemDefault())
                val utcDateTime = instant.toLocalDateTime(kotlinx.datetime.TimeZone.UTC)
                
                val day = utcDateTime.day.toString().padStart(2, '0')
                val month = utcDateTime.month.number.toString().padStart(2, '0')
                formattedDate = "$day-$month-${utcDateTime.year}"
                
                val hour = utcDateTime.hour.toString().padStart(2, '0')
                val min = utcDateTime.minute.toString().padStart(2, '0')
                val sec = utcDateTime.second.toString().padStart(2, '0')
                formattedTime = "$hour:$min:$sec"
            } catch (e: Exception) {
                val parts = date.split("-")
                if (parts.size == 3) {
                    formattedDate = "${parts[2]}-${parts[1]}-${parts[0]}"
                }
            }
        }

        viewModelScope.launch {
            isAuthorizing.value = true
            val request = org.example.project.data.model.PermitAuthorizationSubmitRequest(
                permitId = permitId,
                authorizationRequest = org.example.project.data.model.PermitAuthorizationRequestPayload(
                    authorizerName = authorizer,
                    msraNumber = msra,
                    responsibleHSEPersonId = hsePersonId,
                    signatureImageUrl = signature,
                    authorizedTime = formattedTime,
                    authorizedDate = formattedDate,
                    notes = notes
                )
            )
            println("Request: $request")
            val result = repository.submitPermitAuthorization(request)
            isAuthorizing.value = false
            when (result) {
                is NetworkResult.Success -> {
                    submitSuccessMessage.value = result.data.statusMessage ?: "Success"
                }
                is NetworkResult.Error -> {
                    _uiState.value = PermitDetailUiState.Error(result.message ?: "Failed to authorize permit")
                }
            }
        }
    }

    private val _showActionDialog = MutableStateFlow(false)
    val showActionDialog: StateFlow<Boolean> = _showActionDialog.asStateFlow()

    val actionLoadingState = MutableStateFlow(-1)
    val isAuthorizing = MutableStateFlow(false)
    
    val submitSuccessMessage = MutableStateFlow<String?>(null)
    
    fun onDismissSubmitSuccess() {
        submitSuccessMessage.value = null
    }

    fun clearSubmitError() {
        val currentState = _uiState.value
        if (currentState is PermitDetailUiState.Success) {
            _uiState.update { currentState.copy(submitError = null) }
        }
    }

    private val _actionRemarks = MutableStateFlow("")
    val actionRemarks: StateFlow<String> = _actionRemarks.asStateFlow()

    private val _actionImages = MutableStateFlow<List<org.example.project.data.model.PermitImage>>(emptyList())
    val actionImages: StateFlow<List<org.example.project.data.model.PermitImage>> = _actionImages.asStateFlow()
    
    val isWorkCompletedVerified = MutableStateFlow(false)
    val closureRemarks = MutableStateFlow("")
    val closureImages = MutableStateFlow<List<org.example.project.data.model.PermitImage>>(listOf(org.example.project.data.model.PermitImage(image = null, description = "")))
    val closureSignatureUrl = MutableStateFlow<String?>(null)
    val closureSignatureDate = MutableStateFlow("")
    val closureSignatureTime = MutableStateFlow("")
    val isSubmittingClosure = MutableStateFlow(false)
    
    val certificateClosureSignatureUrl = MutableStateFlow<String?>(null)
    val certificateClosureDate = MutableStateFlow("")
    val certificateClosureTime = MutableStateFlow("")
    val isSubmittingCertificateClosure = MutableStateFlow(false)
    
    private val _selectedAction = MutableStateFlow(-1)

    fun onActionClick(action: Int) {
        _selectedAction.value = action
        _showActionDialog.value = true
    }

    fun onActionDialogDismiss() {
        _showActionDialog.value = false
        _actionRemarks.value = ""
        _actionImages.value = emptyList()
        _selectedAction.value = -1
    }

    fun onActionRemarksChanged(remarks: String) {
        _actionRemarks.value = remarks
    }

    fun onImageDescriptionChange(index: Int, description: String) {
        val currentList = _actionImages.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(description = description)
            _actionImages.value = currentList
        }
    }

    fun onImageSelected(index: Int, url: String) {
        val currentList = _actionImages.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = currentList[index].copy(image = url)
            _actionImages.value = currentList
        }
    }

    fun onImageRemoved(index: Int) {
        val currentList = _actionImages.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _actionImages.value = currentList
        }
    }

    fun onAddImageSlot() {
        if (_actionImages.value.size < 6) {
            _actionImages.update { it + org.example.project.data.model.PermitImage(image = "", description = "") }
        }
    }
    
    fun onWorkCompletedVerifiedChanged(verified: Boolean) {
        isWorkCompletedVerified.value = verified
    }
    fun onClosureRemarksChanged(remarks: String) {
        closureRemarks.value = remarks
    }
    fun onClosureImageDescriptionChange(index: Int, description: String) {
        val current = closureImages.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(description = description)
            closureImages.value = current
        }
    }
    fun onClosureImageSelected(index: Int, url: String) {
        val current = closureImages.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(image = url)
            closureImages.value = current
        }
    }
    fun onClosureImageRemoved(index: Int) {
        val current = closureImages.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            closureImages.value = current
        }
    }
    fun onAddClosureImageSlot() {
        if (closureImages.value.size < 6) {
            closureImages.update { it + org.example.project.data.model.PermitImage(image = null, description = "") }
        }
    }
    fun onClosureSignatureUploaded(url: String) {
        closureSignatureUrl.value = url
        val now = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        val month = now.monthNumber.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')
        closureSignatureDate.value = "${now.year}-$month-$day"
        
        val hour = now.hour.toString().padStart(2, '0')
        val min = now.minute.toString().padStart(2, '0')
        val sec = now.second.toString().padStart(2, '0')
        closureSignatureTime.value = "$hour:$min:$sec"
    }
    fun onRemoveClosureSignatureClick() {
        closureSignatureUrl.value = null
        closureSignatureDate.value = ""
        closureSignatureTime.value = ""
    }
    fun submitPermitClosureRequest(permitId: Int, isWorkCompletedVerified: Boolean) {
        val signatureUrl = closureSignatureUrl.value
        val remarks = closureRemarks.value
        val state = _uiState.value as? PermitDetailUiState.Success ?: return
        
        if (!isWorkCompletedVerified) {
            _uiState.value = state.copy(submitError = "Please verify work completed")
            return
        }
        if (signatureUrl.isNullOrBlank()) {
            _uiState.value = state.copy(submitError = "Please provide a signature")
            return
        }

        viewModelScope.launch {
            isSubmittingClosure.value = true
            
            val validImages = closureImages.value.filter { !it.image.isNullOrBlank() }
            
            val request = org.example.project.data.model.PermitClosureSubmitRequest(
                permitId = permitId,
                requestForCertificateClosure = org.example.project.data.model.RequestForCertificateClosureSubmit(
                    remarks = remarks,
                    signatureImageUrl = signatureUrl,
                    requestTime = closureSignatureTime.value,
                    requestDate = closureSignatureDate.value,
                    images = validImages
                )
            )

            when (val result = repository.submitPermitClosureRequest(request)) {
                is org.example.project.network.NetworkResult.Success -> {
                    submitSuccessMessage.value = result.data.statusMessage ?: "Closure Request Submitted Successfully"
                    loadPermitDetail(permitId)
                }
                is org.example.project.network.NetworkResult.Error -> {
                    _uiState.value = state.copy(submitError = result.message)
                }
            }
            isSubmittingClosure.value = false
        }
    }

    fun onCertificateClosureSignatureUploaded(url: String) {
        certificateClosureSignatureUrl.value = url
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        certificateClosureDate.value = "${currentDateTime.year}-${currentDateTime.monthNumber.toString().padStart(2, '0')}-${currentDateTime.dayOfMonth.toString().padStart(2, '0')}"
        certificateClosureTime.value = "${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}:${currentDateTime.second.toString().padStart(2, '0')}"
    }

    fun onRemoveCertificateClosureSignatureClick() {
        certificateClosureSignatureUrl.value = null
        certificateClosureDate.value = ""
        certificateClosureTime.value = ""
    }

    fun submitPermitCertificateClosure(permitId: Int) {
        val contractorName = authPreferences.getLoggedInUser()?.name ?: ""
        val signatureUrl = certificateClosureSignatureUrl.value
        val state = _uiState.value as? PermitDetailUiState.Success ?: return

        if (signatureUrl.isNullOrBlank()) {
            _uiState.value = state.copy(submitError = "Please provide your signature")
            return
        }

        viewModelScope.launch {
            isSubmittingCertificateClosure.value = true

            val request = org.example.project.data.model.PermitCertificateClosureSubmitRequest(
                permitId = permitId,
                certificateClosure = org.example.project.data.model.CertificateClosureSubmit(
                    contractorName = contractorName,
                    signatureImageUrl = signatureUrl,
                    closureTime = certificateClosureTime.value,
                    closureDate = certificateClosureDate.value
                )
            )

            when (val result = repository.submitPermitCertificateClosure(request)) {
                is org.example.project.network.NetworkResult.Success -> {
                    submitSuccessMessage.value = result.data.statusMessage ?: "Closure Done Successfully"
                    loadPermitDetail(permitId)
                }
                is org.example.project.network.NetworkResult.Error -> {
                    _uiState.value = state.copy(submitError = result.message)
                }
            }
            isSubmittingCertificateClosure.value = false
        }
    }

    fun submitPermitAction(permitId: Int) {
        val user = authPreferences.getLoggedInUser() ?: return
        val action = _selectedAction.value
        if (action == -1) return
        
        viewModelScope.launch {
            onActionDialogDismiss()
            actionLoadingState.value = action
            val request = org.example.project.data.model.PermitActionRequest(
                permitId = permitId,
                action = action,
                actionBy = user,
                remarks = _actionRemarks.value,
                images = _actionImages.value
            )
            val result = repository.submitPermitAction(request)
            actionLoadingState.value = -1
            when (result) {
                is NetworkResult.Success -> {
                    val statusMsg = result.data.statusMessage ?: "Success"
                    submitSuccessMessage.value = statusMsg
                }
                is NetworkResult.Error -> {
                    _uiState.value = PermitDetailUiState.Error(result.message ?: "Failed to perform action")
                }
            }
        }
    }

    fun loadPermitDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = PermitDetailUiState.Loading
            when (val result = repository.getPermitDetail(PermitDetailRequest(id))) {
                is NetworkResult.Success -> {
                    val data = result.data
                    if (data != null) {
                        setValuesForForm(data)
                        _uiState.value = PermitDetailUiState.Success(data)
                    } else {
                        _uiState.value = PermitDetailUiState.Error("Data is null")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = PermitDetailUiState.Error(result.message ?: "Failed to load details")
                }
            }
        }
    }
    
    private fun setValuesForForm(permit: PermitDetailData) {
        val userRole = permit.permitRequestedUser?.userRole ?: 1
        when (userRole) {
            1 -> handleUserTypeForAlNasr(permit)
            2 -> handleUserTypeForSubContractor(permit)
            else -> handleUserTypeForAlNasr(permit)
        }
        
        if (_userType.value == PermitFormUserType.AUTHORIZER) {
            val groupCode = permit.certificateValidity?.project?.groupCode
            if (groupCode != null) {
                val extractedGroupId = groupCode.substringAfter("-").toIntOrNull() ?: -1
                fetchHSEUsers(extractedGroupId, groupCode)
            }
        }

        val loggedInUserId = authPreferences.getLoggedInUser()?.userId
        val isAuthorizedPerson = loggedInUserId != null && loggedInUserId == permit.certificateValidity?.authorizedPerson?.userId
        val isHsePerson = loggedInUserId != null && loggedInUserId == permit.authorizationRequest?.responsibleHSEPerson?.userId
        
        _canCancelOrSuspend.value = permit.permitStatus == 2 && (isAuthorizedPerson || isHsePerson)

        val isLastSuspendedUser = loggedInUserId != null && loggedInUserId == permit.permitSuspendedUsers?.lastOrNull()?.userId
        _canReactivate.value = permit.permitStatus == 4 && isLastSuspendedUser
    }
    
    private fun handleUserTypeForAlNasr(permit: PermitDetailData) {
        val loggedInUser = authPreferences.getLoggedInUser()
        val isAuthorizedUser = loggedInUser?.userId == permit.certificateValidity?.authorizedPerson?.userId
        val isRequstedUser = loggedInUser?.userId == permit.permitRequestedUser?.userId
        
        val requestForCertificateClosureSignature = permit.requestForCertificateClosure?.signatureImageUrl
        val certificateRequestSign = permit.certificateValidity?.signatureImageUrl
        val authSignature = permit.authorizationRequest?.signatureImageUrl
        val certClosureSignature = permit.certificateClosure?.signatureImageUrl

        val status = permit.permitStatus?.let { PermitStatus.fromValue(it) }

        when (status) {
            PermitStatus.PENDING_ALNASR_AUTHORIZATION, PermitStatus.PENDING_ALNASR_CLOSURE -> {
                if (isAuthorizedUser && authSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (isAuthorizedUser && certClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.CERTIFICATE_CLOSURE
                    return
                }
                if (authSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (certClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE_VIEWER
                    return
                }
            }
            PermitStatus.OPEN -> {
                if (isRequstedUser) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                } else {
                    _userType.value = PermitFormUserType.AUTHORIZER_VIEWER
                }
            }
            PermitStatus.CANCELLED -> {
                _userType.value = PermitFormUserType.AUTHORIZER_VIEWER
            }
            PermitStatus.SUSPENDED -> {
                _userType.value = PermitFormUserType.AUTHORIZER_VIEWER
            }
            PermitStatus.EXPIRED -> {
                if (certificateRequestSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (authSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (requestForCertificateClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.AUTHORIZER_VIEWER
                    return
                }
                if (certClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE_VIEWER
                    return
                }
            }
            PermitStatus.CLOSED -> {
                _userType.value = PermitFormUserType.NONE
            }
            null -> {
                _userType.value = PermitFormUserType.NONE
            }
            else -> {
                if (certificateRequestSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (authSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (requestForCertificateClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.AUTHORIZER_VIEWER
                    return
                }
                if (certClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE_VIEWER
                    return
                }
            }
        }
    }
    
    private fun handleUserTypeForSubContractor(permit: PermitDetailData) {
        val currentUserId = authPreferences.getLoggedInUser()?.userId ?: return

        val isAuthorizedSubContractor = currentUserId == permit.certificateValidity?.authorizedPerson?.userId
        val isAuthorizedAlNasr = currentUserId == permit.subContractorAuthorization?.alnasrAuthorizer?.userId
        val isRequestedUser = currentUserId == permit.permitRequestedUser?.userId

        val certificateRequestSign = permit.certificateValidity?.signatureImageUrl
        val subAuthSign = permit.subContractorAuthorization?.signatureImageUrl
        val authSign = permit.authorizationRequest?.signatureImageUrl
        val subClosureSign = permit.subContractorClosure?.signatureImageUrl
        val certClosureSign = permit.certificateClosure?.signatureImageUrl
        val requestForCertificateClosureSign = permit.requestForCertificateClosure?.signatureImageUrl

        val status = permit.permitStatus?.let { PermitStatus.fromValue(it) }

        when (status) {
            PermitStatus.PENDING_ALNASR_CLOSURE, PermitStatus.PENDING_ALNASR_AUTHORIZATION, PermitStatus.PENDING_SUBCONTRACTOR_CLOSURE, PermitStatus.PENDING_SUBCONTRACTOR_AUTHORIZATION -> {
                if (isAuthorizedSubContractor && subAuthSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_AUTHORIZER
                    return
                }
                if (subAuthSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (isAuthorizedAlNasr && authSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (authSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_AUTHORIZER
                    return
                }
                if (isRequestedUser && requestForCertificateClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                    return
                }
                if (requestForCertificateClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (isAuthorizedSubContractor && subClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_CLOSURE
                    return
                }
                if (subClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                    return
                }
                if (isAuthorizedAlNasr && certClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.CERTIFICATE_CLOSURE
                    return
                }
                if (certClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_CLOSURE
                    return
                }
                if (isRequestedUser && subAuthSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (certClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                    return
                }
            }
            PermitStatus.OPEN -> {
                if (isRequestedUser) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                } else {
                    _userType.value = PermitFormUserType.AUTHORIZER
                }
            }
            PermitStatus.CANCELLED, PermitStatus.SUSPENDED -> {
                _userType.value = PermitFormUserType.AUTHORIZER
            }
            PermitStatus.CLOSED, null -> {
                _userType.value = PermitFormUserType.NONE
            }
            PermitStatus.EXPIRED -> {
                if (certificateRequestSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (subAuthSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (authSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_AUTHORIZER
                    return
                }
                if (certificateRequestSign.isNullOrEmpty()) { 
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (subClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                    return
                }
                if (certClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_CLOSURE
                    return
                }
            }
            else -> {
                if (certificateRequestSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (subAuthSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUESTOR
                    return
                }
                if (authSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_AUTHORIZER
                    return
                }
                if (certificateRequestSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (subClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                    return
                }
                if (certClosureSign.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.SUBCONTRACTOR_CLOSURE
                    return
                }
            }
        }
    }
}
