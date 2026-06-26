package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.IncidentDetailRequest
import org.example.project.data.model.IncidentDetailResponse
import org.example.project.domain.repository.IncidentRepository
import org.example.project.network.NetworkResult

sealed class IncidentDetailUiState {
    object Idle : IncidentDetailUiState()
    object Loading : IncidentDetailUiState()
    data class Success(val detail: IncidentDetailResponse) : IncidentDetailUiState()
    data class Error(val message: String) : IncidentDetailUiState()
}

class IncidentDetailViewModel(
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncidentDetailUiState>(IncidentDetailUiState.Idle)
    val uiState: StateFlow<IncidentDetailUiState> = _uiState

    fun loadIncidentDetail(incidentId: Int) {
        viewModelScope.launch {
            _uiState.value = IncidentDetailUiState.Loading
            val request = IncidentDetailRequest(id = incidentId)
            when (val result = incidentRepository.getIncidentDetail(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response != null) {
                        _uiState.value = IncidentDetailUiState.Success(response)
                    } else {
                        _uiState.value = IncidentDetailUiState.Error("Invalid response")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = IncidentDetailUiState.Error(result.message ?: "Failed to load incident details")
                }
            }
        }
    }
}
