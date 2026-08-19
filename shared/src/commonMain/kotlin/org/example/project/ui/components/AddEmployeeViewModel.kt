package org.example.project.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.EmployeeAllListRequest
import org.example.project.data.model.EmployeeData
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

data class AddEmployeeUiState(
    val employees: List<EmployeeData> = emptyList(),
    val isLoading: Boolean = false,
    val searchKey: String = "",
    val pageNumber: Int = 1,
    val hasMore: Boolean = true,
    val showDropdown: Boolean = false,
    val errorMessage: String? = null
)

class AddEmployeeViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEmployeeUiState())
    val uiState: StateFlow<AddEmployeeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchKeyChange(key: String) {
        _uiState.update { it.copy(searchKey = key, showDropdown = key.isNotEmpty(), errorMessage = null) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            if (key.isNotEmpty()) {
                _uiState.update { it.copy(employees = emptyList(), pageNumber = 1, hasMore = true, isLoading = true, errorMessage = null) }
                fetchEmployees(key, 1)
            } else {
                _uiState.update { it.copy(employees = emptyList(), pageNumber = 1, hasMore = true, showDropdown = false, errorMessage = null) }
            }
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isLoading || !currentState.hasMore) return
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            fetchEmployees(currentState.searchKey, currentState.pageNumber + 1)
        }
    }

    private suspend fun fetchEmployees(searchKey: String, page: Int) {
        val request = EmployeeAllListRequest(searchKey = searchKey, pageNumber = page)
        when (val result = repository.getEmployeeAllList(request)) {
            is NetworkResult.Success -> {
                val newEmployees = result.data
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        employees = if (page == 1) newEmployees else it.employees + newEmployees,
                        pageNumber = page,
                        hasMore = newEmployees.isNotEmpty()
                    ) 
                }
            }
            is NetworkResult.Error -> {
                _uiState.update { it.copy(isLoading = false, hasMore = false, errorMessage = result.message) }
            }
        }
    }
    
    fun hideDropdown() {
        _uiState.update { it.copy(showDropdown = false) }
    }
}
