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
     * Clears everything stored in User Defaults / SharedPreferences via Settings.
     */
    fun logout() {
        settings.clear()
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
