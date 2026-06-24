package org.example.project.localization

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    val appName: String
    val login: String
    val notAMember: String
    val registerNow: String
    val fullName: String
    val emailId: String
    val password: String
    val forgotPassword: String
    val emailPlaceholder: String
    val passwordPlaceholder: String
    val register: String
    val alreadyHaveAccount: String
    val loginNow: String
    val uploadProfile: String
    val confirmPassword: String
    val designation: String
    val company: String
    val privacyPolicyMessage: String
    val termsAndConditions: String
    val andThe: String
    val privacyPolicy: String
    val welcomeToInstaresolv: String
    val loremIpsum: String
    val dimentumAliquam: String
    val donecPosuerunc: String
    val welcomeTo: String
    val verifyOtp: String
    val verifyOtpMessage: String
    val resendCode: String
    val continueText: String
    val forgotPasswordDescription: String
    val resetPassword: String
    val home: String
    val project: String
    val briefs: String
    val settings: String
    val assignedToMe: String
    val viewAll: String
    val actionOverview: String
    
    // Additional commonly used hardcoded strings found in the app
    val profile: String
    val notifications: String
    val logout: String
    val logoutConfirmation: String
    val yes: String
    val no: String
    val cancel: String
    val save: String
    val submit: String
    val success: String
    val error: String
    val ok: String
    val deleteAccount: String
    val changePassword: String
    val contactUs: String
    val selectLanguage: String
    val appLanguage: String
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnStrings }
