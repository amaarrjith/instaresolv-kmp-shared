package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.project.ui.AppTabBar
import org.example.project.ui.ForgetPasswordScreen
import org.example.project.ui.LoginScreen
import org.example.project.ui.OTPVerificationScreen
import org.example.project.ui.RegisterScreen
import org.example.project.ui.SplashScreen
import org.example.project.ui.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.Splash.route) {
        composable(Screens.Splash.route) {
            SplashScreen(
                onNavigateToWelcomeScreen = {
                    navController.navigate(Screens.WelcomeScreen.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screens.TabBar.route) {
                        popUpTo(Screens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.TabBar.route) {
                        popUpTo(Screens.Login.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToRegister = {
                    navController.navigate(Screens.RegisterScreen.route)
                },
                navigateToForgetPassword = {
                    navController.navigate(Screens.ForgetPasswordScreen.route)
                }
            )
        }
        composable(Screens.WelcomeScreen.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screens.RegisterScreen.route) {
            RegisterScreen(
                isLoginClicked = {
                    navController.popBackStack()
                },
                isRegisterCompleted = { tempUserId, email ->
                    navController.navigate(
                        "${Screens.OTPScreen.route}/$tempUserId/$email"
                    )
                }
            )
        }
        composable(
            Screens.OTPScreenRoute.route,
            arguments = listOf(
                navArgument("tempUserId") {type = NavType.IntType},
                navArgument("email") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val tempUserId =
                backStackEntry.savedStateHandle.get<Int>("tempUserId") ?: -1
            val email =
                backStackEntry.savedStateHandle.get<String>("email").orEmpty()
            OTPVerificationScreen(
                {
                    navController.navigate(Screens.TabBar.route) {
                        popUpTo(Screens.OTPScreenRoute.route) {
                            inclusive = true
                        }
                    }
                },
                backButtonPressed = { navController.popBackStack() },
                tempUserId = tempUserId,
                email = email
            )
        }
        composable(Screens.ForgetPasswordScreen.route) {
            ForgetPasswordScreen {
                navController.popBackStack()
            }
        }
        composable(Screens.TabBar.route) {
            AppTabBar(
                onProfileClick = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.TabBar.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
