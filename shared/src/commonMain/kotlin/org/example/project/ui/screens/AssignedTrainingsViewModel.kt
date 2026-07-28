package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.model.DesignationTypeResponse
import org.example.project.data.settings.AuthPreferences

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.data.model.AllTrainingData
import org.example.project.data.model.AllTrainingRequest
import org.example.project.data.model.AssignTrainingRequest
import org.example.project.data.model.AssignedTrainingData
import org.example.project.data.model.AssignedTrainingRequest
import org.example.project.domain.repository.TrainingRepository
import org.example.project.network.NetworkResult

data class AssignedTrainingsUiState(
    val trainings: List<AssignedTrainingData> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val error: String? = null,
    val isLastPage: Boolean = false,
    val searchQuery: String = "",
    
    // Add Training Modal State
    val isAddTrainingModalVisible: Boolean = false,
    val allTrainings: List<AllTrainingData> = emptyList(),
    val isLoadingAllTrainings: Boolean = false,
    val isPaginatingAllTrainings: Boolean = false,
    val isLastPageAllTrainings: Boolean = false,
    val allTrainingsError: String? = null,
    val selectedTrainingIds: Set<Int> = emptySet(),
    val isAssigning: Boolean = false,
    val assignSuccessMessage: String? = null,
    val assignErrorMessage: String? = null
)

class AssignedTrainingsViewModel(
    private val authPreferences: AuthPreferences,
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _designations = MutableStateFlow<List<DesignationTypeResponse>>(emptyList())
    val designations: StateFlow<List<DesignationTypeResponse>> = _designations.asStateFlow()

    private val _uiState = MutableStateFlow(AssignedTrainingsUiState())
    val uiState: StateFlow<AssignedTrainingsUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var currentAllTrainingsPage = 1
    private val limit = 20
    private var currentUserId: Int = -1

    init {
        getDesignationTypes()
    }

    private fun getDesignationTypes() {
        _designations.value = authPreferences.getDesignationTypes()
    }

    fun initUserId(userId: Int) {
        if (currentUserId != userId) {
            currentUserId = userId
            loadTrainings(isRefresh = true)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadTrainings(isRefresh = true)
    }

    fun loadTrainings(isRefresh: Boolean = false) {
        if (currentUserId == -1) return
        if (_uiState.value.isLoading || _uiState.value.isPaginating) return

        if (isRefresh) {
            currentPage = 1
            _uiState.value = _uiState.value.copy(isLoading = true, isLastPage = false, error = null)
        } else {
            if (_uiState.value.isLastPage) return
            _uiState.value = _uiState.value.copy(isPaginating = true, error = null)
        }

        viewModelScope.launch {
            val request = AssignedTrainingRequest(
                pageNumber = currentPage,
                limit = limit,
                userId = currentUserId,
                searchKey = _uiState.value.searchQuery
            )

            when (val result = trainingRepository.getAssignedTrainings(request)) {
                is NetworkResult.Success -> {
                    val newTrainings = result.data.trainings
                    val allTrainings = if (isRefresh) newTrainings else _uiState.value.trainings + newTrainings
                    
                    currentPage++
                    _uiState.value = _uiState.value.copy(
                        trainings = allTrainings,
                        isLoading = false,
                        isPaginating = false,
                        isLastPage = newTrainings.size < limit,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPaginating = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun setAddTrainingModalVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(
            isAddTrainingModalVisible = visible,
            selectedTrainingIds = emptySet() // clear selection on open/close
        )
        if (visible) {
            loadAllTrainings(isRefresh = true)
        }
    }

    fun loadAllTrainings(isRefresh: Boolean = false) {
        if (currentUserId == -1) return
        if (_uiState.value.isLoadingAllTrainings || _uiState.value.isPaginatingAllTrainings) return

        if (isRefresh) {
            currentAllTrainingsPage = 1
            _uiState.value = _uiState.value.copy(isLoadingAllTrainings = true, isLastPageAllTrainings = false, allTrainingsError = null)
        } else {
            if (_uiState.value.isLastPageAllTrainings) return
            _uiState.value = _uiState.value.copy(isPaginatingAllTrainings = true, allTrainingsError = null)
        }

        viewModelScope.launch {
            val request = AllTrainingRequest(
                pageNumber = currentAllTrainingsPage,
                limit = limit,
                userId = currentUserId
            )
            when (val result = trainingRepository.getAllTrainings(request)) {
                is NetworkResult.Success -> {
                    val newTrainings = result.data.trainings
                    val allTrainings = if (isRefresh) newTrainings else _uiState.value.allTrainings + newTrainings
                    
                    currentAllTrainingsPage++
                    _uiState.value = _uiState.value.copy(
                        isLoadingAllTrainings = false,
                        isPaginatingAllTrainings = false,
                        isLastPageAllTrainings = newTrainings.size < limit,
                        allTrainings = allTrainings,
                        allTrainingsError = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingAllTrainings = false,
                        isPaginatingAllTrainings = false,
                        allTrainingsError = result.message
                    )
                }
            }
        }
    }

    fun toggleTrainingSelection(id: Int) {
        val currentSelection = _uiState.value.selectedTrainingIds.toMutableSet()
        if (currentSelection.contains(id)) {
            currentSelection.remove(id)
        } else {
            currentSelection.add(id)
        }
        _uiState.value = _uiState.value.copy(selectedTrainingIds = currentSelection)
    }

    fun assignSelectedTrainings() {
        val selectedIds = _uiState.value.selectedTrainingIds.toList()
        if (selectedIds.isEmpty() || currentUserId == -1) return

        _uiState.value = _uiState.value.copy(isAssigning = true, assignErrorMessage = null, assignSuccessMessage = null)

        viewModelScope.launch {
            val request = AssignTrainingRequest(
                userId = currentUserId,
                videoIds = selectedIds
            )
            when (val result = trainingRepository.assignTraining(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAssigning = false,
                        assignSuccessMessage = result.data.statusMessage.ifBlank { "Training Video Successfully Assigned" },
                        isAddTrainingModalVisible = false
                    )
                    loadTrainings(isRefresh = true) // Refresh assigned list
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAssigning = false,
                        assignErrorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            assignSuccessMessage = null,
            assignErrorMessage = null
        )
    }
}
