package org.example.project.config

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

/**
 * Supported runtime environments.
 */
enum class AppEnvironment(
    val baseUrl: String,
    val isLoggingEnabled: Boolean,
    val appStoreId: String
) {
    DEVELOPMENT(
        baseUrl = "https://instaresolv-dev.zoondia.org/api/",
        isLoggingEnabled = true,
        appStoreId = ""
    ),
    STAGING(
        baseUrl = "https://instaresolv-staging.zoondia.org/api/",
        isLoggingEnabled = true,
        appStoreId = ""
    ),
    PRODUCTION(
        baseUrl = "https://instaresolv.com/api/",
        isLoggingEnabled = false,
        appStoreId = "6761991644"
    );
}

/**
 * Global configuration management for brand, endpoints, logging, and environment settings.
 */
object AppConfig {
    /**
     * Active brand flavor. Defaults to INSTA_RESOLV.
     */
    var brand: Brand = Brand.INSTA_RESOLV

    /**
     * Active environment. Default to DEVELOPMENT for dev builds,
     * switchable at application start or release config.
     */
    var environment: AppEnvironment = AppEnvironment.DEVELOPMENT

    /**
     * Optional dynamic color override for primary brand theme.
     */
    var customPrimaryColor: Color? = null

    val primaryColor: Color
        get() = customPrimaryColor ?: brand.defaultPrimaryColor

    val baseUrl: String
        get() = brand.getBaseUrl(environment)

    val isLoggingEnabled: Boolean
        get() = environment.isLoggingEnabled

    val appStoreId: String
        get() = brand.appStoreId.ifEmpty { environment.appStoreId }

    val isProduction: Boolean
        get() = environment == AppEnvironment.PRODUCTION

    val appName: String
        get() = brand.displayName

    val appLogo: DrawableResource
        get() = brand.logoResource

    val loginLogo: DrawableResource
        get() = brand.loginLogo
}

/**
 * Utility logger that automatically suppresses output in Production.
 */
fun appLog(message: Any?) {
    if (AppConfig.isLoggingEnabled) {
        println(message)
    }
}
