package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.example.project.ui.ActionOverview
import org.example.project.ui.AppTabBar
import org.example.project.ui.ForgetPasswordScreen
import org.example.project.ui.LoginScreen
import org.example.project.ui.OTPVerificationScreen
import org.example.project.ui.RegisterScreen
import org.example.project.ui.SplashScreen
import org.example.project.ui.WelcomeScreen
import org.example.project.ui.components.AuditInspectionListScreen
import org.example.project.ui.components.IncidentListScreen
import org.example.project.ui.components.NotificationListScreen
import org.example.project.ui.components.ObservationListScreen
import org.example.project.ui.components.PermitToWorkListScreen
import org.example.project.ui.components.TrainingListScreen
import org.example.project.ui.components.ViolationListScreen
import org.example.project.ui.screens.CreateProjectScreen
import org.example.project.ui.screens.ProfileScreen

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
        composable(Screens.NotificationListScreen.route) {
            NotificationListScreen {
                navController.popBackStack()
            }
        }
        composable(Screens.TabBar.route) {
            AppTabBar(
                onProfileClick = {
                    navController.navigate(Screens.ProfileScreen.route)
                },
                onNotificationClick = {
                    navController.navigate(Screens.NotificationListScreen.route)
                },
                onModuleClicked = { module ->
                    when (module) {
                        ActionOverview.AUDIT_INSPECTIONS -> {
                            navController.navigate(Screens.AuditInspectionListScreen.route)
                        }
                        ActionOverview.PERMIT_TO_WORK -> {
                            navController.navigate(Screens.PermitToWorkListScreen.route)
                        }
                        ActionOverview.OBSERVATIONS -> {
                            navController.navigate(Screens.ObservationListScreen.route)
                        }
                        ActionOverview.INCIDENTS -> {
                            navController.navigate(Screens.IncidentListScreen.route)
                        }
                        ActionOverview.VIOLATIONS -> {
                            navController.navigate(Screens.ViolationListScreen.route)
                        }
                        ActionOverview.TRAINING -> {
                            navController.navigate(Screens.TrainingListScreen.route)
                        }
                    }
                },
                onCreateProjectClicked = {
                    navController.navigate(Screens.CreateProjectScreen.route)
                }
            )
        }
        composable(Screens.AuditInspectionListScreen.route) {
            AuditInspectionListScreen {
                navController.popBackStack()
            }
        }
        composable(Screens.PermitToWorkListScreen.route) {
            PermitToWorkListScreen{
                navController.popBackStack()
            }
        }
        composable(Screens.ObservationListScreen.route) {
            ObservationListScreen{
                navController.popBackStack()
            }
        }
        composable(Screens.IncidentListScreen.route) {
            IncidentListScreen{
                navController.popBackStack()
            }
        }
        composable(Screens.ViolationListScreen.route) {
            ViolationListScreen{
                navController.popBackStack()
            }
        }
        composable(Screens.TrainingListScreen.route) {
            TrainingListScreen{
                navController.popBackStack()
            }
        }
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.TabBar.route) {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screens.CreateProjectScreen.route) {
            CreateProjectScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
