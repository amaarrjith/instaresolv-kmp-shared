package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.PendingActionItem
import org.example.project.domain.repository.PendingActionRepository
import org.example.project.network.NetworkResult

data class PendingActionListState(
    val isLoading: Boolean = false,
    val pendingActions: List<PendingActionItem> = emptyList(),
    val error: String? = null
)

class PendingActionListViewModel(
    private val repository: PendingActionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendingActionListState())
    val uiState: StateFlow<PendingActionListState> = _uiState.asStateFlow()

    init {
        fetchPendingActions()
    }

    fun fetchPendingActions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getPendingActions()) {
                is NetworkResult.Success -> {
                    val actions = result.data.pendingActions
                    _uiState.update { 
                        it.copy(isLoading = false, pendingActions = actions) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message ?: "Failed to fetch pending actions") 
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
