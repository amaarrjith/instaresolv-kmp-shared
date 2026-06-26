package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.ViolationData
import org.example.project.data.model.ViolationDetailRequest
import org.example.project.domain.repository.ViolationRepository
import org.example.project.network.NetworkResult

data class ViolationDetailState(
    val isLoading: Boolean = false,
    val detail: ViolationData? = null,
    val error: String? = null
)

class ViolationDetailViewModel(
    private val repository: ViolationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViolationDetailState())
    val uiState: StateFlow<ViolationDetailState> = _uiState.asStateFlow()

    fun loadViolationDetail(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val request = ViolationDetailRequest(id = id)
            when (val result = repository.getViolationDetail(request)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    if (data != null) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                detail = data
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = "Failed to load violation details"
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load violation details"
                        )
                    }
                }
            }
        }
    }
}
