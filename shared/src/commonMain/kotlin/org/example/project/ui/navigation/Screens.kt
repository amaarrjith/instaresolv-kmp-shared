package org.example.project.navigation

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Login: Screens("login")
    object WelcomeScreen: Screens("welcome_screen")
    object RegisterScreen: Screens("register_screen")
    object OTPScreen: Screens("otp_screen")
    object OTPScreenRoute: Screens("otp_screen/{tempUserId}/{email}")
    object ForgetPasswordScreen: Screens("forget_password_screen")
    object TabBar: Screens("tab_bar")
}
