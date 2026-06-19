package org.example.project.tabbar

import org.example.project.data.model.ActionsOverview
import org.example.project.data.model.AssignedToMe

sealed class AppTabBarUiState {
    data object Loading : AppTabBarUiState()
    data class Success(
        val actionsOverview: ActionsOverview? = null,
        val assignedToMe: AssignedToMe? = null
    ): AppTabBarUiState()
    data class Error(
        val errorMessage: String
    ): AppTabBarUiState()
}