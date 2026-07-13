package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import kotlinx.datetime.toLocalDateTime

sealed class PermitDetailUiState {
    object Loading : PermitDetailUiState()
    data class Success(val data: PermitDetailData) : PermitDetailUiState()
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

    private fun fetchGroupUsers(groupId: Int, groupCode: String) {
        viewModelScope.launch {
            val result = projectRepository.getGroupUsers(groupId, groupCode)
            if (result is NetworkResult.Success) {
                _hsePersons.value = result.data?.users ?: emptyList()
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
            val groupId = permit.certificateValidity?.project?.groupId
            val groupCode = permit.certificateValidity?.project?.groupCode
            if (groupId != null && groupCode != null) {
                fetchGroupUsers(groupId.toIntOrNull() ?: -1, groupCode)
            }
        }
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
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                    return
                }
            }
            PermitStatus.OPEN -> {
                if (isRequstedUser) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
                } else {
                    _userType.value = PermitFormUserType.AUTHORIZER
                }
            }
            PermitStatus.CANCELLED -> {
                _userType.value = PermitFormUserType.AUTHORIZER
            }
            PermitStatus.SUSPENDED -> {
                _userType.value = PermitFormUserType.AUTHORIZER
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
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (certClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
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
                    _userType.value = PermitFormUserType.AUTHORIZER
                    return
                }
                if (certClosureSignature.isNullOrEmpty()) {
                    _userType.value = PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE
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
