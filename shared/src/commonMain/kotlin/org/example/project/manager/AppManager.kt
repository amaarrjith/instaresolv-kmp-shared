package org.example.project.manager

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppManager(
    private val settings: Settings
) {
    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    /**
     * Clears user session credentials and details from Settings.
     */
    fun logout() {
        settings.remove("is_logged_in")
        settings.remove("access_token")
        settings.remove("refresh_token")
        settings.remove("token_expiry")
        settings.remove("logged_in_user")
        settings.remove("logged_in_user_info")
        _logoutEvent.tryEmit(Unit)
    }

    companion object {
        private var instance: AppManager? = null

        fun init(appManager: AppManager) {
            instance = appManager
        }

        fun getInstance(): AppManager {
            return instance ?: error("AppManager is not initialized")
        }

        fun logout() {
            instance?.logout()
        }
    }
}
