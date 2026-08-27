package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.example.project.data.model.PermitItem
import org.example.project.data.model.PermitListRequest
import org.example.project.data.model.PermitStatus
import org.example.project.domain.repository.PermitRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.repository.PermitDraftRepository
import org.example.project.data.settings.AuthPreferences

data class PermitToWorkListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val permits: List<PermitItem> = emptyList(),
    val drafts: List<org.example.project.shared.db.PermitDraft> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val appliedFilterState: org.example.project.data.model.AppFilterState = org.example.project.data.model.AppFilterState(),
    val isTypesLoading: Boolean = false,
    val permitTypesList: List<org.example.project.data.model.PermitTypeItem> = emptyList(),
    val typesError: String? = null,
    val isExporting: Boolean = false,
    val exportSuccessMessage: String? = null,
    val exportDownloadUrl: String? = null,
    val exportError: String? = null,
    val errorMessage: String? = null
)

class PermitToWorkListViewModel(
    private val repository: PermitRepository,
    private val authPreferences: AuthPreferences,
    private val permitDraftRepository: PermitDraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermitToWorkListState())
    val uiState: StateFlow<PermitToWorkListState> = _uiState.asStateFlow()
    val logginedUser = authPreferences.getLoggedInUser()
    val draftToastMessage = MutableStateFlow<String?>(null)
    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        fetchPermits(isRefresh = true)
    }

    fun updateSearchKey(query: String) {
        _uiState.update { it.copy(searchKey = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            fetchPermits(isRefresh = true)
        }
    }

    fun applyFilters(state: org.example.project.data.model.AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = state) }
        fetchPermits(isRefresh = true)
    }

    fun fetchPermits(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
        }

        if (_uiState.value.isLoading || _uiState.value.isPaginating) return
        if (!isRefresh && _uiState.value.endReached) return

        if (currentPage == 1) {
            _uiState.update { it.copy(isLoading = true, error = null, endReached = false) }
        } else {
            _uiState.update { it.copy(isPaginating = true, error = null) }
        }

        viewModelScope.launch {
            val filter = _uiState.value.appliedFilterState

            // Map selected statuses from titles to enum values
            val selectedStatuses = filter.selectedStatuses.mapNotNull { statusName ->
                PermitStatus.entries.find { it.title.equals(statusName, ignoreCase = true) }?.value
            }.takeIf { it.isNotEmpty() }

            val projectIds = filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val authorizerIds = filter.selectedAuthorizers.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val requestorIds = filter.selectedRequestors.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val hseAssignedIds = filter.selectedHseAssigned.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val permitTypeIds = filter.selectedPermitTypes.map { it.permitTypeId }.takeIf { it.isNotEmpty() }
            val openDateStr = formatMillis(filter.dateOpenMillis).takeIf { it.isNotBlank() }
            val closeDateStr = formatMillis(filter.dateCloseMillis).takeIf { it.isNotBlank() }

            val request = PermitListRequest(
                pageNumber = currentPage,
                limit = 10,
                sortType = 1, // Descending
                searchKey = _uiState.value.searchKey,
                projectIds = projectIds,
                authorizer = authorizerIds,
                requestor = requestorIds,
                hseAssigned = hseAssignedIds,
                status = selectedStatuses,
                permitTypes = permitTypeIds,
                openDate = openDateStr,
                closeDate = closeDateStr,
                validity = filter.selectedValidity
            )

            when (val result = repository.getPermitList(request)) {
                is NetworkResult.Success -> {
                    val items = result.data?.results ?: emptyList()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPaginating = false,
                            permits = if (currentPage == 1) items else it.permits + items,
                            endReached = items.isEmpty()
                        )
                    }
                    if (items.isNotEmpty()) {
                        currentPage++
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPaginating = false,
                            error = result.message ?: "Failed to fetch permits"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun formatMillis(millis: Long?): String {
        if (millis == null) return ""
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val day = localDate.dayOfMonth.toString().padStart(2, '0')
        val month = localDate.monthNumber.toString().padStart(2, '0')
        val year = localDate.year
        return "$year-$month-$day" // API expects yyyy-MM-dd
    }

    fun fetchPermitTypes() {
        _uiState.update { it.copy(isTypesLoading = true, typesError = null) }
        viewModelScope.launch {
            when (val result = repository.getPermitTypes()) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isTypesLoading = false,
                            permitTypesList = result.data?.contents ?: emptyList()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isTypesLoading = false,
                            typesError = result.message ?: "Failed to load permit types"
                        )
                    }
                }
            }
        }
    }
    fun generateExcel() {
        if (_uiState.value.isExporting) return
        _uiState.update { it.copy(isExporting = true, exportError = null, exportSuccessMessage = null, exportDownloadUrl = null) }
        viewModelScope.launch {
            val currentState = _uiState.value
            val filter = currentState.appliedFilterState

            val selectedStatuses = filter.selectedStatuses.mapNotNull { statusName ->
                PermitStatus.entries.find { it.title.equals(statusName, ignoreCase = true) }?.value
            }.takeIf { it.isNotEmpty() }

            val projectIds = filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val authorizerIds = filter.selectedAuthorizers.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val requestorIds = filter.selectedRequestors.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val hseAssignedIds = filter.selectedHseAssigned.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            val permitTypeIds = filter.selectedPermitTypes.map { it.permitTypeId }.takeIf { it.isNotEmpty() }
            val openDateStr = formatMillis(filter.dateOpenMillis).takeIf { it.isNotBlank() }
            val closeDateStr = formatMillis(filter.dateCloseMillis).takeIf { it.isNotBlank() }

            val request = org.example.project.data.model.PermitExcelRequest(
                searchKey = currentState.searchKey,
                sortBy = 1,
                projectIds = projectIds,
                authorizer = authorizerIds,
                requestor = requestorIds,
                hseAssigned = hseAssignedIds,
                status = selectedStatuses,
                permitTypes = permitTypeIds,
                openDate = openDateStr,
                closeDate = closeDateStr,
                validity = filter.selectedValidity
            )
            
            try {
                when (val result = repository.generatePermitExcel(request)) {
                    is NetworkResult.Success -> {
                        val url = result.data.excelUrl?.takeIf { it.isNotBlank() }
                        _uiState.update { it.copy(isExporting = false, exportSuccessMessage = "Downloading Permit Report", exportDownloadUrl = url) }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isExporting = false, exportError = result.message ?: "Failed to generate Excel") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isExporting = false, exportError = e.message ?: "An unexpected error occurred") }
            }
        }
    }
    
    fun clearExportSuccess() {
        _uiState.update { it.copy(exportSuccessMessage = null) }
    }
    
    fun clearExportDownloadUrl() {
        _uiState.update { it.copy(exportDownloadUrl = null) }
    }
    
    fun clearExportError() {
        _uiState.update { it.copy(exportError = null) }
    }

    fun clearDraftToast() {
        draftToastMessage.value = null
    }

    fun loadDrafts() {
        viewModelScope.launch {
            val user = authPreferences.getLoggedInUser()
            if (user?.userId != null) {
                val list = permitDraftRepository.getAllDrafts(user.userId!!)
                _uiState.update { it.copy(drafts = list) }
            }
        }
    }

    fun deleteDrafts(ids: List<Long>, successMessage: String) {
        viewModelScope.launch {
            ids.forEach { id ->
                permitDraftRepository.deleteDraft(id)
            }
            draftToastMessage.value = successMessage
            loadDrafts()
        }
    }
}
