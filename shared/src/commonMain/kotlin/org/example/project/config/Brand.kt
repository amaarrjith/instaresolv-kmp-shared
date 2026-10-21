package org.example.project.config

import androidx.compose.ui.graphics.Color
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_app_login_logo
import instaresolv.shared.generated.resources.ic_app_logo
import instaresolv.shared.generated.resources.ic_qe_app_logo
import org.jetbrains.compose.resources.DrawableResource

/**
 * Supported brand variants for white-label application distribution.
 */
enum class Brand(
    val brandId: String,
    val displayName: String,
    val defaultPrimaryColor: Color,
    val devBaseUrl: String,
    val stagingBaseUrl: String,
    val prodBaseUrl: String,
    val appStoreId: String,
    val logoResource: DrawableResource = Res.drawable.ic_app_logo,
    val loginLogo: DrawableResource = Res.drawable.ic_app_login_logo
) {
    INSTA_RESOLV(
        brandId = "instaresolv",
        displayName = "InstaResolv",
        defaultPrimaryColor = Color(0XFFD42027),
        devBaseUrl = "https://instaresolv-dev.zoondia.org/api/",
        stagingBaseUrl = "https://instaresolv-staging.zoondia.org/api/",
        prodBaseUrl = "https://instaresolv.com/api/",
        appStoreId = "6761991644",
        logoResource = Res.drawable.ic_app_logo,
        loginLogo = Res.drawable.ic_app_login_logo
    ),
    QUALITY_EXPERTISE(
        brandId = "qualityexpertise",
        displayName = "QualityExpertise",
        defaultPrimaryColor = Color(0XFFD42027), // Currently identical to InstaResolv; update here when final color is decided
        devBaseUrl = "https://qe-dev.zoondia.org/api/",
        stagingBaseUrl = "https://qe-dev.zoondia.org/api/",
        prodBaseUrl = "https://qualityexpertise.instaresolv.com/api/",
        appStoreId = "",
        logoResource = Res.drawable.ic_qe_app_logo,
        loginLogo = Res.drawable.ic_qe_app_logo// Currently identical; update here when final asset is added
    );

    fun getBaseUrl(environment: AppEnvironment): String = when (environment) {
        AppEnvironment.DEVELOPMENT -> devBaseUrl
        AppEnvironment.STAGING -> stagingBaseUrl
        AppEnvironment.PRODUCTION -> prodBaseUrl
    }
}
