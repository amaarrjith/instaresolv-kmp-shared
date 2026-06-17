package org.example.project.data.settings

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import org.example.project.data.model.UserDetailInfoModel
import org.example.project.data.model.UserResponse

class AuthPreferences(
    private val settings: Settings
) {
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_WELCOME_PAGE_SHOWN = "is_welcome_page_shown"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val LOGGED_USER = "logged_in_user"
        private const val LOGGED_USER_INFO = "logged_in_user_info"
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        settings.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
    }

    fun isLoggedIn(): Boolean {
        return settings.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveTokens(access: String, refresh: String, expiry: Long) {
        settings.putString(KEY_ACCESS_TOKEN, access)
        settings.putString(KEY_REFRESH_TOKEN, refresh)
        settings.putLong(KEY_TOKEN_EXPIRY, expiry)
    }

    fun getAccessToken(): String? {
        val token = settings.getString(KEY_ACCESS_TOKEN, "")
        return if (token.isEmpty()) null else token
    }

    fun getRefreshToken(): String? {
        val token = settings.getString(KEY_REFRESH_TOKEN, "")
        return if (token.isEmpty()) null else token
    }

    fun getTokenExpiry(): Long {
        return settings.getLong(KEY_TOKEN_EXPIRY, 0L)
    }

    fun logout() {
        settings.remove(KEY_IS_LOGGED_IN)
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_TOKEN_EXPIRY)
    }

    fun saveWelcomePageShownStatus(isWelcomePageShown: Boolean) {
        settings.putBoolean(KEY_IS_WELCOME_PAGE_SHOWN, isWelcomePageShown)
    }

    fun getWelcomePageShownStatus(): Boolean {
        return settings.getBoolean(KEY_IS_WELCOME_PAGE_SHOWN, false)
    }

    fun saveLoggedInUser(user: UserResponse?) {
        settings.putString(LOGGED_USER, Json.encodeToString(user))
    }

    fun saveLoggedInUserInfo(info: UserDetailInfoModel?) {
        settings.putString(LOGGED_USER_INFO, Json.encodeToString(info))
    }

    fun getLoggedInUser(): UserResponse? {
        return settings.getStringOrNull(LOGGED_USER)?.let {
            Json.decodeFromString<UserResponse>(it)
        }
    }

    fun getLoggedInUserInfo(): UserDetailInfoModel? {
        return settings.getStringOrNull(LOGGED_USER_INFO)?.let {
            Json.decodeFromString<UserDetailInfoModel>(it)
        }
    }

}