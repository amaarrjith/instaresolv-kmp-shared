package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.ProjectDetail
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
import org.example.project.ui.screens.ProjectDetailScreen
import org.example.project.ui.screens.ChangePasswordScreen
import org.example.project.ui.screens.ContactUsScreen
import org.example.project.ui.screens.AboutUsScreen
import org.example.project.ui.screens.TermsOfUseScreen
import org.example.project.ui.screens.PrivacyPolicyScreen
import org.example.project.ui.screens.DeleteAccountScreen
import org.example.project.ui.screens.PendingActionListScreen

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
                },
                onProjectClicked = { project ->
                    navController.navigate("${Screens.ProjectDetailScreen.route}/${project.groupId}/${project.groupCode}")
                },
                onChangePasswordClick = { navController.navigate(Screens.ChangePasswordScreen.route) },
                onContactUsClick = { navController.navigate(Screens.ContactUsScreen.route) },
                onAboutUsClick = { navController.navigate(Screens.AboutUsScreen.route) },
                onTermsOfUseClick = { navController.navigate(Screens.TermsOfUseScreen.route) },
                onPrivacyPolicyClick = { navController.navigate(Screens.PrivacyPolicyScreen.route) },
                onDeleteAccountClick = { navController.navigate(Screens.DeleteAccountScreen.route) },
                onPendingActionViewAllClick = { navController.navigate(Screens.PendingActionListScreen.route) }
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
        composable(Screens.PendingActionListScreen.route) {
            PendingActionListScreen{
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
        composable(Screens.ProjectDetailScreenWithArgs.route) { backStackEntry ->
            val groupId = backStackEntry.savedStateHandle.get<String>("groupId")?.toIntOrNull() ?: -1
            val groupCode = backStackEntry.savedStateHandle.get<String>("groupCode") ?: "-1"
            ProjectDetailScreen(
                groupId = groupId,
                groupCode = groupCode,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = { projectDetail ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("project", Json.encodeToString(projectDetail))
                    navController.navigate(Screens.EditProjectScreen.route)
                }
            )
        }
        composable(Screens.EditProjectScreen.route) {
            val projectJson = navController.previousBackStackEntry?.savedStateHandle?.get<String>("project")
            val project = projectJson?.let {
                Json.decodeFromString<ProjectDetail>(it)
            }
            CreateProjectScreen(
                project = project,
                onBack = {
                    navController.popBackStack()
                }
            )

        }
        composable(Screens.ChangePasswordScreen.route) {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(Screens.ContactUsScreen.route) {
            ContactUsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screens.AboutUsScreen.route) {
            AboutUsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screens.TermsOfUseScreen.route) {
            TermsOfUseScreen(onBack = { navController.popBackStack() })
        }
        composable(Screens.PrivacyPolicyScreen.route) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screens.DeleteAccountScreen.route) {
            DeleteAccountScreen(onBack = { navController.popBackStack() })
        }
    }
}
