package org.example.project.config

/**
 * Supported runtime environments for InstaResolv.
 */
enum class AppEnvironment(
    val baseUrl: String,
    val isLoggingEnabled: Boolean,
    val appStoreId: String
) {
    DEVELOPMENT(
        baseUrl = "https://instaresolv-dev.zoondia.org/api/",
        isLoggingEnabled = true,
        appStoreId = "6478951234"
    ),
    STAGING(
        baseUrl = "https://instaresolv-staging.zoondia.org/api/",
        isLoggingEnabled = true,
        appStoreId = "6478951234"
    ),
    PRODUCTION(
        baseUrl = "https://instaresolv.zoondia.org/api/",
        isLoggingEnabled = false,
        appStoreId = "6478951234" // Replace with real production App Store ID when assigned by Apple
    );
}

/**
 * Global configuration management for endpoints, logging, and environment settings.
 */
object AppConfig {
    /**
     * Active environment. Default to DEVELOPMENT for dev builds,
     * switchable at application start or release config.
     */
    var environment: AppEnvironment = AppEnvironment.DEVELOPMENT

    val baseUrl: String
        get() = environment.baseUrl

    val isLoggingEnabled: Boolean
        get() = environment.isLoggingEnabled

    val appStoreId: String
        get() = environment.appStoreId

    val isProduction: Boolean
        get() = environment == AppEnvironment.PRODUCTION
}
