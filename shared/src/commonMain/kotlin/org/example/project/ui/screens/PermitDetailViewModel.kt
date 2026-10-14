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
import org.example.project.ui.screens.AuthorizerFormSection
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

    private val _authorizerPersons = MutableStateFlow<List<GroupUser>>(emptyList())
    val authorizerPersons: StateFlow<List<GroupUser>> = _authorizerPersons.asStateFlow()

    private val _selectedAuthorizerPerson = MutableStateFlow<GroupUser?>(null)
    val selectedAuthorizerPerson: StateFlow<GroupUser?> = _selectedAuthorizerPerson.asStateFlow()

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

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf.asStateFlow()

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl.asStateFlow()

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage.asStateFlow()

    fun generatePdf(permitId: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GeneratePermitPdfRequest(permitId = permitId)
            when (val result = repository.generatePermitPdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data.resolvedPdfUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data.statusMessage ?: "Failed to generate Permit PDF"
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfToastMessage.value = result.message ?: "Failed to generate Permit PDF"
                }
            }
        }
    }

    fun setPdfToastMessage(message: String) {
        _pdfToastMessage.update { message }
    }

    fun clearPdfToastMessage() {
        _pdfToastMessage.update { null }
    }

    fun clearPdfUrl() {
        _pdfUrl.update { null }
    }

    private val _canReactivate = MutableStateFlow(false)
    val canReactivate: StateFlow<Boolean> = _canReactivate.asStateFlow()

    fun onHsePersonSelected(user: GroupUser) {
        _selectedHsePerson.value = user
    }

    fun onAuthorizerPersonSelected(user: GroupUser) {
        _selectedAuthorizerPerson.value = user
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

    private fun fetchAuthorizers(groupId: Int, groupCode: String) {
        viewModelScope.launch {
            // using designationType = 1 as default
            val request = org.example.project.data.model.PermitUserListRequest(
                groupId = groupId,
                groupCode = groupCode,
                designationType = 1
            )
            when (val result = repository.getPermitUserList(request)) {
                is NetworkResult.Success -> {
                    val mappedUsers = result.data.users.filter { it.userRole == 1 }.map { it.toGroupUser() }
                    _authorizerPersons.value = mappedUsers
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun submitSubContractorAuthorization(permitId: Int) {
        val authorizer = _authorizerName.value
        val authorizerPersonId = _selectedAuthorizerPerson.value?.userId ?: 0
        val signature = _signatureUrl.value ?: ""
        val date = _signatureDate.value
        val time = _signatureTime.value
        val state = _uiState.value as? PermitDetailUiState.Success ?: return

        if (authorizerPersonId == 0) {
            _uiState.value = state.copy(submitError = "Please select an authorizer person")
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
            val request = org.example.project.data.model.PermitSubContractorAuthorizationSubmitRequest(
                permitId = permitId,
                subContractorAuthorizationRequest = org.example.project.data.model.PermitSubContractorAuthorizationRequestPayload(
                    subContractorAuthorizerName = authorizer,
                    alnasrAuthorizerUserId = authorizerPersonId.toString(),
                    signatureImageUrl = signature,
                    authorizationTime = formattedTime,
                    authorizationDate = formattedDate
                )
            )
            println("SubContractor Authorization Request: $request")
            val result = repository.submitPermitSubContractorAuthorization(request)
            isAuthorizing.value = false
            when (result) {
                is NetworkResult.Success -> {
                    submitSuccessMessage.value = result.data.statusMessage ?: "Subcontractor Authorization Done Successfully"
                    loadPermitDetail(permitId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = state.copy(submitError = result.message ?: "Failed to authorize permit")
                }
            }
        }
    }

    fun authorizePermit(permitId: Int) {
        if (_userType.value == PermitFormUserType.SUBCONTRACTOR_AUTHORIZER) {
            submitSubContractorAuthorization(permitId)
            return
        }
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
                    loadPermitDetail(permitId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = state.copy(submitError = result.message ?: "Failed to authorize permit")
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

    val subContractorClosureAuthorizerName = MutableStateFlow(authPreferences.getLoggedInUser()?.name ?: "")
    val subContractorClosureSignatureUrl = MutableStateFlow<String?>(null)
    val subContractorClosureDate = MutableStateFlow("")
    val subContractorClosureTime = MutableStateFlow("")
    val isSubmittingSubContractorClosure = MutableStateFlow(false)
    
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

    fun onSubContractorClosureAuthorizerNameChanged(name: String) {
        subContractorClosureAuthorizerName.value = name
    }

    fun onSubContractorClosureSignatureUploaded(url: String) {
        subContractorClosureSignatureUrl.value = url
        val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month = now.monthNumber.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')
        subContractorClosureDate.value = "${now.year}-$month-$day"

        val hour = now.hour.toString().padStart(2, '0')
        val min = now.minute.toString().padStart(2, '0')
        val sec = now.second.toString().padStart(2, '0')
        subContractorClosureTime.value = "$hour:$min:$sec"
    }

    fun onRemoveSubContractorClosureSignatureClick() {
        subContractorClosureSignatureUrl.value = null
        subContractorClosureDate.value = ""
        subContractorClosureTime.value = ""
    }

    fun submitPermitSubContractorClosure(permitId: Int) {
        val authorizer = subContractorClosureAuthorizerName.value
        val signatureUrl = subContractorClosureSignatureUrl.value
        val state = _uiState.value as? PermitDetailUiState.Success ?: return

        if (authorizer.isBlank()) {
            _uiState.value = state.copy(submitError = "Please enter authorizer name")
            return
        }
        if (signatureUrl.isNullOrBlank()) {
            _uiState.value = state.copy(submitError = "Please provide your signature")
            return
        }

        val date = subContractorClosureDate.value
        val time = subContractorClosureTime.value

        var utcDate = date
        var utcTime = time
        if (date.isNotBlank() && time.isNotBlank()) {
            try {
                val localDateTimeStr = "${date}T${time}"
                val instant = kotlinx.datetime.LocalDateTime.parse(localDateTimeStr)
                    .toInstant(TimeZone.currentSystemDefault())
                val utcDateTime = instant.toLocalDateTime(TimeZone.UTC)

                val utcMonth = utcDateTime.monthNumber.toString().padStart(2, '0')
                val utcDay = utcDateTime.dayOfMonth.toString().padStart(2, '0')
                utcDate = "${utcDateTime.year}-$utcMonth-$utcDay"

                val utcHour = utcDateTime.hour.toString().padStart(2, '0')
                val utcMin = utcDateTime.minute.toString().padStart(2, '0')
                val utcSec = utcDateTime.second.toString().padStart(2, '0')
                utcTime = "$utcHour:$utcMin:$utcSec"
            } catch (e: Exception) {
                val currentUtc = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.UTC)
                val utcMonth = currentUtc.monthNumber.toString().padStart(2, '0')
                val utcDay = currentUtc.dayOfMonth.toString().padStart(2, '0')
                utcDate = "${currentUtc.year}-$utcMonth-$utcDay"

                val utcHour = currentUtc.hour.toString().padStart(2, '0')
                val utcMin = currentUtc.minute.toString().padStart(2, '0')
                val utcSec = currentUtc.second.toString().padStart(2, '0')
                utcTime = "$utcHour:$utcMin:$utcSec"
            }
        }

        viewModelScope.launch {
            isSubmittingSubContractorClosure.value = true

            val request = org.example.project.data.model.PermitSubContractorClosureSubmitRequest(
                permitId = permitId,
                subContractorClosureRequest = org.example.project.data.model.SubContractorClosureRequestPayload(
                    authorizerName = authorizer,
                    signatureImageUrl = signatureUrl,
                    closureTime = utcTime,
                    closureDate = utcDate
                )
            )

            when (val result = repository.submitPermitSubContractorClosure(request)) {
                is org.example.project.network.NetworkResult.Success -> {
                    submitSuccessMessage.value = result.data.statusMessage ?: "Subcontractor Closure Done Successfully"
                    loadPermitDetail(permitId)
                }
                is org.example.project.network.NetworkResult.Error -> {
                    _uiState.value = state.copy(submitError = result.message ?: "Failed to submit closure")
                }
            }
            isSubmittingSubContractorClosure.value = false
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

        if (_userType.value == PermitFormUserType.SUBCONTRACTOR_AUTHORIZER) {
            val groupCode = permit.certificateValidity?.project?.groupCode
            if (groupCode != null) {
                val extractedGroupId = groupCode.substringAfter("-").toIntOrNull() ?: -1
                fetchAuthorizers(extractedGroupId, groupCode)
            }
        }

        if (
            _userType.value == PermitFormUserType.AUTHORIZER ||
            _userType.value == PermitFormUserType.INSTARESOLV_AUTHORIZER
        ) {
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
        val tag = "PermitUserTypeDebug"

        fun log(message: String) {
            println("[$tag] $message")
        }

        fun logResult(userType: PermitFormUserType, reason: String) {
            _userType.value = userType
            log("FINAL USER TYPE: $userType | Reason: $reason")
        }

        log("========== START handleUserTypeForSubContractor ==========")

        val loggedInUser = authPreferences.getLoggedInUser()
        val currentUserId = loggedInUser?.userId

        log("Logged-in user ID: $currentUserId")

        if (currentUserId == null) {
            log("EXIT: Logged-in user ID is null")
            return
        }

        // User identification
        val authorizedSubContractorId =
            permit.certificateValidity?.authorizedPerson?.userId

        val authorizedAlNasrId =
            permit.subContractorAuthorization?.alnasrAuthorizer?.userId

        val requestedUserId = permit.permitRequestedUser?.userId

        val isAuthorizedSubContractor =
            currentUserId == authorizedSubContractorId

        val isAuthorizedAlNasr =
            currentUserId == authorizedAlNasrId

        val isRequestedUser =
            currentUserId == requestedUserId

        log("USER IDENTIFICATION:")
        log("Current user ID: $currentUserId")
        log("Authorized subcontractor ID: $authorizedSubContractorId")
        log("Authorized AlNasr ID: $authorizedAlNasrId")
        log("Requested user ID: $requestedUserId")
        log("isAuthorizedSubContractor: $isAuthorizedSubContractor")
        log("isAuthorizedAlNasr: $isAuthorizedAlNasr")
        log("isRequestedUser: $isRequestedUser")

        // Signature information
        val certificateRequestSign =
            permit.certificateValidity?.signatureImageUrl

        val subAuthSign =
            permit.subContractorAuthorization?.signatureImageUrl

        val authSign =
            permit.authorizationRequest?.signatureImageUrl

        val subClosureSign =
            permit.subContractorClosure?.signatureImageUrl

        val certClosureSign =
            permit.certificateClosure?.signatureImageUrl

        val requestForCertificateClosureSign =
            permit.requestForCertificateClosure?.signatureImageUrl

        log("SIGNATURE INFORMATION:")
        log("certificateRequestSign exists: ${!certificateRequestSign.isNullOrEmpty()}")
        log("subAuthSign exists: ${!subAuthSign.isNullOrEmpty()}")
        log("authSign exists: ${!authSign.isNullOrEmpty()}")
        log("subClosureSign exists: ${!subClosureSign.isNullOrEmpty()}")
        log("certClosureSign exists: ${!certClosureSign.isNullOrEmpty()}")
        log("requestForCertificateClosureSign exists: ${!requestForCertificateClosureSign.isNullOrEmpty()}")

        // Permit status
        val rawStatus = permit.permitStatus
        val status = rawStatus?.let { PermitStatus.fromValue(it) }

        log("PERMIT INFORMATION:")
        log("Permit ID: ${permit.permitId}")
        log("Permit code: ${permit.permitCode}")
        log("Raw permit status: $rawStatus")
        log("Parsed permit status: $status")

        log("EVALUATING STATUS BRANCH: $status")

        when (status) {
            PermitStatus.PENDING_ALNASR_CLOSURE,
            PermitStatus.PENDING_ALNASR_AUTHORIZATION,
            PermitStatus.PENDING_SUBCONTRACTOR_CLOSURE,
            PermitStatus.PENDING_SUBCONTRACTOR_AUTHORIZATION -> {

                log("BRANCH: Pending authorization/closure")

                log("CHECK 1: isAuthorizedSubContractor=$isAuthorizedSubContractor, subAuthSignEmpty=${subAuthSign.isNullOrEmpty()}")
                if (isAuthorizedSubContractor && subAuthSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_AUTHORIZER,
                        "Authorized subcontractor; subcontractor authorization signature missing"
                    )
                    return
                }

                log("CHECK 2: subAuthSignEmpty=${subAuthSign.isNullOrEmpty()}")
                if (subAuthSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUESTOR,
                        "Subcontractor authorization signature missing"
                    )
                    return
                }

                log("CHECK 3: isAuthorizedAlNasr=$isAuthorizedAlNasr, authSignEmpty=${authSign.isNullOrEmpty()}")
                if (isAuthorizedAlNasr && authSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.INSTARESOLV_AUTHORIZER,
                        "Authorized AlNasr user; authorization signature missing"
                    )
                    return
                }

                log("CHECK 4: authSignEmpty=${authSign.isNullOrEmpty()}")
                if (authSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_AUTHORIZER_VIEWER,
                        "Authorization signature missing; viewer fallback"
                    )
                    return
                }

                log("CHECK 5: isRequestedUser=$isRequestedUser, requestForCertificateClosureSignEmpty=${requestForCertificateClosureSign.isNullOrEmpty()}")
                if (isRequestedUser && requestForCertificateClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE,
                        "Requested user; certificate closure request signature missing"
                    )
                    return
                }

                log("CHECK 6: requestForCertificateClosureSignEmpty=${requestForCertificateClosureSign.isNullOrEmpty()}")
                if (requestForCertificateClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.INSTARESOLV_AUTHORIZER,
                        "Certificate closure request signature missing"
                    )
                    return
                }

                log("CHECK 7: isAuthorizedSubContractor=$isAuthorizedSubContractor, subClosureSignEmpty=${subClosureSign.isNullOrEmpty()}")
                if (isAuthorizedSubContractor && subClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_CLOSURE,
                        "Authorized subcontractor; subcontractor closure signature missing"
                    )
                    return
                }

                log("CHECK 8: subClosureSignEmpty=${subClosureSign.isNullOrEmpty()}")
                if (subClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE_VIEWER,
                        "Subcontractor closure signature missing; viewer fallback"
                    )
                    return
                }

                log("CHECK 9: isAuthorizedAlNasr=$isAuthorizedAlNasr, certClosureSignEmpty=${certClosureSign.isNullOrEmpty()}")
                if (isAuthorizedAlNasr && certClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.CERTIFICATE_CLOSURE,
                        "Authorized AlNasr user; certificate closure signature missing"
                    )
                    return
                }

                log("CHECK 10: certClosureSignEmpty=${certClosureSign.isNullOrEmpty()}")
                if (certClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_CLOSURE_VIEWER,
                        "Certificate closure signature missing; fallback"
                    )
                    return
                }

                log("No condition matched in pending branch")
            }

            PermitStatus.OPEN -> {
                log("BRANCH: OPEN")
                log("isRequestedUser=$isRequestedUser")

                if (isRequestedUser) {
                    logResult(
                        PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE,
                        "Permit OPEN; logged-in user is requestor"
                    )
                } else {
                    logResult(
                        PermitFormUserType.INSTARESOLV_AUTHORIZER_VIEWER,
                        "Permit OPEN; logged-in user is not requestor"
                    )
                }
            }

            PermitStatus.CANCELLED,
            PermitStatus.SUSPENDED -> {
                log("BRANCH: CANCELLED/SUSPENDED")

                logResult(
                    PermitFormUserType.AUTHORIZER,
                    "Permit is cancelled or suspended"
                )
            }

            PermitStatus.CLOSED, null -> {
                log("BRANCH: CLOSED or unknown status")

                logResult(
                    PermitFormUserType.NONE,
                    "Permit is closed or status could not be parsed"
                )
            }

            PermitStatus.EXPIRED -> {
                log("BRANCH: EXPIRED")

                log("CHECK 1: certificateRequestSignEmpty=${certificateRequestSign.isNullOrEmpty()}")
                if (certificateRequestSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUESTOR,
                        "Expired permit; certificate request signature missing"
                    )
                    return
                }

                log("CHECK 2: subAuthSignEmpty=${subAuthSign.isNullOrEmpty()}")
                if (subAuthSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUESTOR,
                        "Expired permit; subcontractor authorization signature missing"
                    )
                    return
                }

                log("CHECK 3: isAuthorizedAlNasr=$isAuthorizedAlNasr, authSignEmpty=${authSign.isNullOrEmpty()}")
                if (isAuthorizedAlNasr && authSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.AUTHORIZER,
                        "Expired permit; authorized AlNasr user; authorization signature missing"
                    )
                    return
                }

                log("CHECK 4: authSignEmpty=${authSign.isNullOrEmpty()}")
                if (authSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_AUTHORIZER_VIEWER,
                        "Expired permit; authorization signature missing"
                    )
                    return
                }

                log("CHECK 5: certificateRequestSignEmpty=${certificateRequestSign.isNullOrEmpty()}")
                if (certificateRequestSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.AUTHORIZER,
                        "Expired permit; certificate request signature missing"
                    )
                    return
                }

                log("CHECK 6: subClosureSignEmpty=${subClosureSign.isNullOrEmpty()}")
                if (subClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE,
                        "Expired permit; subcontractor closure signature missing"
                    )
                    return
                }

                log("CHECK 7: certClosureSignEmpty=${certClosureSign.isNullOrEmpty()}")
                if (certClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_CLOSURE,
                        "Expired permit; certificate closure signature missing"
                    )
                    return
                }

                log("No condition matched in EXPIRED branch")
            }

            PermitStatus.PENDING_ALNASR_AUTHORIZATION_AND_EXPIRED -> {
                log("BRANCH: PENDING_ALNASR_AUTHORIZATION_AND_EXPIRED")

                logResult(
                    PermitFormUserType.SUBCONTRACTOR_AUTHORIZER_VIEWER,
                    "AlNasr authorization pending on expired permit"
                )
            }

            PermitStatus.PENDING_SUBCONTRACTOR_AUTHORIZATION_AND_EXPIRED -> {
                log("BRANCH: PENDING_SUBCONTRACTOR_AUTHORIZATION_AND_EXPIRED")

                logResult(
                    PermitFormUserType.REQUESTOR,
                    "Subcontractor authorization pending on expired permit"
                )
            }

            else -> {
                log("BRANCH: ELSE, status=$status")

                log("CHECK 1: certificateRequestSignEmpty=${certificateRequestSign.isNullOrEmpty()}")
                if (certificateRequestSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUESTOR,
                        "Certificate request signature missing"
                    )
                    return
                }

                log("CHECK 2: subAuthSignEmpty=${subAuthSign.isNullOrEmpty()}")
                if (subAuthSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUESTOR,
                        "Subcontractor authorization signature missing"
                    )
                    return
                }

                log("CHECK 3: isAuthorizedAlNasr=$isAuthorizedAlNasr, authSignEmpty=${authSign.isNullOrEmpty()}")
                if (isAuthorizedAlNasr && authSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.AUTHORIZER,
                        "Authorized AlNasr user; authorization signature missing"
                    )
                    return
                }

                log("CHECK 4: authSignEmpty=${authSign.isNullOrEmpty()}")
                if (authSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_AUTHORIZER_VIEWER,
                        "Authorization signature missing"
                    )
                    return
                }

                log("CHECK 5: certificateRequestSignEmpty=${certificateRequestSign.isNullOrEmpty()}")
                if (certificateRequestSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.AUTHORIZER,
                        "Certificate request signature missing"
                    )
                    return
                }

                log("CHECK 6: subClosureSignEmpty=${subClosureSign.isNullOrEmpty()}")
                if (subClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE,
                        "Subcontractor closure signature missing"
                    )
                    return
                }

                log("CHECK 7: certClosureSignEmpty=${certClosureSign.isNullOrEmpty()}")
                if (certClosureSign.isNullOrEmpty()) {
                    logResult(
                        PermitFormUserType.SUBCONTRACTOR_CLOSURE,
                        "Certificate closure signature missing"
                    )
                    return
                }

                log("No condition matched in ELSE branch")
            }
        }

        log("END: No condition returned a user type. Current value=${_userType.value}")
        log("========== END handleUserTypeForSubContractor ==========")
    }
}
