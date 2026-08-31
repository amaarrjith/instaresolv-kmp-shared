package org.example.project.manager

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.project.data.model.NotificationListModel

class AppManager(
    private val settings: Settings
) {
    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _notificationTapEvent = MutableSharedFlow<NotificationListModel>(extraBufferCapacity = 1)
    val notificationTapEvent = _notificationTapEvent.asSharedFlow()

    var pendingNotificationTap: NotificationListModel? = null

    fun handleNotificationTap(type: Int, contentId: Int, groupCode: String?) {
        val model = NotificationListModel(
            id = 0,
            type = type,
            contentId = contentId,
            title = null,
            time = null,
            date = null,
            description = null,
            groupCode = groupCode,
            isRead = true
        )
        pendingNotificationTap = model
        _notificationTapEvent.tryEmit(model)
    }

    fun handleNotificationTap(model: NotificationListModel) {
        pendingNotificationTap = model
        _notificationTapEvent.tryEmit(model)
    }

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
