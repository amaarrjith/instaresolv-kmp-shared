package org.example.project.navigation

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Login: Screens("login")
    object WelcomeScreen: Screens("welcome_screen")
    object RegisterScreen: Screens("register_screen")
    object OTPScreen: Screens("otp_screen")
    object OTPScreenRoute: Screens("otp_screen/{tempUserId}/{email}")
    object ForgetPasswordScreen: Screens("forget_password_screen")
    object NotificationListScreen: Screens("notification_list_screen")
    object TabBar: Screens("tab_bar")
    // Observation
    object ObservationListScreen : Screens("observation_list_screen")
    object ObservationDetailsScreen : Screens("observation_details_screen")

    // Action Overview Modules
    object AuditInspectionListScreen : Screens("audit_inspection_list_screen")
    object PermitToWorkListScreen : Screens("permit_to_work_list_screen")
    object IncidentListScreen : Screens("incident_list_screen")
    object ViolationListScreen : Screens("violation_list_screen")
    object TrainingListScreen : Screens("training_list_screen")
    object ProfileScreen : Screens("profile_screen")
    object CreateProjectScreen : Screens("create_project_screen")
}
