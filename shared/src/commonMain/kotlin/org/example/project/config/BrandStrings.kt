package org.example.project.config

import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * Brand-aware string resolution helper.
 * Provides the correct localized StringResource based on AppConfig.brand.
 */
object BrandStrings {
    val appName: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.app_name_qe else Res.string.app_name

    val welcome: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.welcome_to_qe else Res.string.welcome_to_instaresolv

    val aboutApp: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.learnAboutTheApp_qe else Res.string.learnAboutTheApp

    val connectTeam: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.connectQe else Res.string.connectInsta

    val contactEmail: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.connectqecom else Res.string.connectinstaresolvcom

    val authorizerName: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.qeAuthorizerName else Res.string.instaresolvAuthorizerName

    val aiAnalyzingImage: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) {
            Res.string.qeAiIsAnalysingYourImageToGenerateADetailedAndAccurateDescriptionThisMayTakeAFewMoments
        } else {
            Res.string.instaresolvAiIsAnalysingYourImageToGenerateADetailedAndAccurateDescriptionThisMayTakeAFewMoments
        }

    val copyrightText: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.copyrightText_qe else Res.string.copyrightText

    val newVersionAvailable: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.new_version_available_qe else Res.string.new_version_available

    val pendingAuthorization: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_authorization else Res.string.pending_instaresolv_authorization

    val pendingClosure: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_closure else Res.string.pending_instaresolv_closure

    val pendingAuthExpired: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_auth_expired else Res.string.pending_instaresolv_auth_expired

    val pendingClosureExpired: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_closure_expired else Res.string.pending_instaresolv_closure_expired

    val pendingAuthShort: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_authorization_short else Res.string.pending_ir_authorization

    val pendingClosureShort: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_closure_short else Res.string.pending_ir_closure

    val pendingAuthExpiredShort: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_authorization_and_expired else Res.string.pending_ir_authorization_and_expired

    val pendingClosureExpiredShort: StringResource
        get() = if (AppConfig.brand == Brand.QUALITY_EXPERTISE) Res.string.pending_qe_closure_and_expired else Res.string.pending_ir_closure_and_expired
}
