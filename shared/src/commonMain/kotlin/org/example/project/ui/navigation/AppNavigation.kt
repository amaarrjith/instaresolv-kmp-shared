package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.json.Json
import org.example.project.data.model.ProjectDetail
import org.example.project.data.model.ProjectMember
import org.example.project.manager.AppManager
import org.example.project.navigation.Screens
import org.koin.compose.koinInject
import org.example.project.ui.ActionOverview
import org.example.project.ui.AppTabBar
import org.example.project.ui.ForgetPasswordScreen
import org.example.project.ui.LoginScreen
import org.example.project.ui.OTPVerificationScreen
import org.example.project.ui.RegisterScreen
import org.example.project.ui.SplashScreen
import org.example.project.ui.WelcomeScreen
import org.example.project.ui.screens.AuditInspectionListScreen
import org.example.project.ui.screens.IncidentListScreen
import org.example.project.ui.screens.IncidentDraftListScreen
import org.example.project.ui.screens.LessonsLearnedListScreen
import org.example.project.ui.screens.CreateLessonsLearnedScreen
import org.example.project.ui.screens.LessonsLearnedDraftListScreen
import org.example.project.ui.screens.ToolBoxTalkListScreen
import org.example.project.ui.screens.ToolBoxTalkDraftListScreen
import org.example.project.ui.screens.CreateToolBoxTalkScreen
import org.example.project.ui.screens.ToolBoxTalkDetailScreen
import org.example.project.ui.components.NotificationListScreen
import org.example.project.ui.components.AppNotificationClickListener
import org.example.project.ui.screens.ObservationListScreen
import org.example.project.ui.screens.CreateObservationScreen
import org.example.project.ui.screens.CreateIncidentScreen
import org.example.project.ui.screens.PermitToWorkListScreen
import org.example.project.ui.screens.CreatePermitScreen
import org.example.project.ui.screens.PermitDraftListScreen
import org.example.project.ui.screens.PermitDetailScreen
import org.example.project.ui.screens.TrainingListScreen
import org.example.project.ui.screens.TrainingDetailScreen
import org.example.project.ui.screens.TrainingVideoScreen
import org.example.project.ui.screens.TrainingScormScreen
import org.example.project.ui.screens.QuizScreen
import org.example.project.ui.screens.ViolationListScreen
import org.example.project.ui.screens.CreateViolationScreen
import org.example.project.ui.screens.CreateProjectScreen
import org.example.project.ui.screens.ProfileScreen
import org.example.project.ui.screens.ProjectDetailScreen
import org.example.project.ui.screens.ChangePasswordScreen
import org.example.project.ui.screens.ContactUsScreen
import org.example.project.ui.screens.AboutUsScreen
import org.example.project.ui.screens.AssignedTrainingsView
import org.example.project.ui.screens.CreateInspectionScreen
import org.example.project.ui.screens.CreatePreTaskScreen
import org.example.project.ui.screens.TermsOfUseScreen
import org.example.project.ui.screens.PrivacyPolicyScreen
import org.example.project.ui.screens.DeleteAccountScreen
import org.example.project.ui.screens.InspectionDetailScreen
import org.example.project.ui.screens.ObservationDetailScreen
import org.example.project.ui.screens.ObservationDraftListScreen
import org.example.project.ui.screens.ViolationDraftListScreen
import org.example.project.ui.screens.PendingActionListScreen
import org.example.project.ui.screens.PreTaskListScreen
import org.example.project.ui.screens.PreTaskDraftListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val appManager: AppManager = koinInject()

    LaunchedEffect(Unit) {
        appManager.logoutEvent.collect {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

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
                },
                isTermsClicked = {
                    navController.navigate(Screens.TermsOfUseScreen.route)
                },
                isPrivacyClicked = {
                    navController.navigate(Screens.PrivacyPolicyScreen.route)
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
            NotificationListScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onNotificationClick = { notification ->
                    AppNotificationClickListener(
                        notification = notification,
                        navController = navController
                    )
                }
            )
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
                        
                        ActionOverview.LESSONS_LEARNED -> {
                            navController.navigate(Screens.LessonsLearnedListScreen.route)
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
                onPendingActionViewAllClick = { navController.navigate(Screens.PendingActionListScreen.route) },
                onPreTaskClicked = { navController.navigate(Screens.PreTaskListScreen.route) },
                onLessonLearnedClicked = { navController.navigate(Screens.LessonsLearnedListScreen.route) },
                onToolboxTalksClicked = { navController.navigate(Screens.ToolBoxTalkListScreen.route) },
                onPermitDetailClick = { permitId -> navController.navigate("${Screens.PermitDetailScreen.route}/$permitId") },
                onNavigateToProject = { groupId, groupCode -> navController.navigate("${Screens.ProjectDetailScreen.route}/${groupId}/${groupCode}") },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screens.PreTaskListScreen.route) {
            PreTaskListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { navController.navigate(Screens.CreatePreTaskScreen.route) },
                onDraftClicked = { navController.navigate(Screens.PreTaskDraftListScreen.route) }
            )
        }
        composable(Screens.CreatePreTaskScreen.route) {
            CreatePreTaskScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(
            route = Screens.CreatePreTaskScreenWithArgs.route,
            arguments = listOf(
                navArgument("isFromDraft") {
                    type = NavType.StringType
                    defaultValue = "false"
                },
                navArgument("draftId") {
                    type = NavType.StringType
                    defaultValue = "-1"
                }
            )
        ) { backStackEntry ->
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreatePreTaskScreen(
                onBackClicked = { navController.popBackStack() },
                isFromDraft = isFromDraft,
                draftId = draftId
            )
        }
        composable(Screens.PreTaskDraftListScreen.route) {
            PreTaskDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { draftId ->
                    navController.navigate("create_pre_task_screen?isFromDraft=true&draftId=$draftId")
                }
            )
        }
        composable(Screens.AuditInspectionListScreen.route) {
            AuditInspectionListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { typeId, typeName ->
                    navController.navigate("${Screens.CreateInspectionScreen.route}/$typeId/$typeName")
                },
                onItemClicked = { inspectionId ->
                    navController.navigate("${Screens.InspectionDetailScreen.route}/$inspectionId")
                }
            )
        }
        composable(Screens.CreateInspectionScreenWithArgs.route) { backStackEntry ->
            val inspectionTypeId = backStackEntry.savedStateHandle.get<String>("inspectionTypeId")?.toIntOrNull() ?: -1
            val inspectionTypeName = backStackEntry.savedStateHandle.get<String>("inspectionTypeName") ?: ""
            CreateInspectionScreen(
                inspectionTypeId = inspectionTypeId,
                inspectionTypeName = inspectionTypeName,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.InspectionDetailScreenWithArgs.route) { backStackEntry ->
            val inspectionId = backStackEntry.savedStateHandle.get<String>("inspectionId")?.toIntOrNull() ?: -1
            InspectionDetailScreen(
                inspectionId = inspectionId,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.PermitToWorkListScreen.route) {
            PermitToWorkListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { typeId, typeName ->
                    navController.navigate("${Screens.CreatePermitScreen.route}/$typeId/$typeName")
                },
                onItemClicked = { permitId ->
                    navController.navigate("${Screens.PermitDetailScreen.route}/$permitId")
                },
                onDraftClicked = {
                    navController.navigate(Screens.PermitDraftListScreen.route)
                }
            )
        }
        composable(Screens.PermitDetailScreenWithArgs.route) { backStackEntry ->
            val permitId = backStackEntry.savedStateHandle.get<String>("permitId")?.toIntOrNull() ?: -1
            PermitDetailScreen(
                id = permitId,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.PermitDraftListScreen.route) {
            PermitDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { draftId ->
                    navController.navigate("create_permit_screen/-1/Draft?isFromDraft=true&draftId=$draftId")
                }
            )
        }
        composable(Screens.CreatePermitScreenWithArgs.route) { backStackEntry ->
            val permitTypeId = backStackEntry.savedStateHandle.get<String>("permitTypeId")?.toIntOrNull() ?: -1
            val permitTypeName = backStackEntry.savedStateHandle.get<String>("permitTypeName") ?: ""
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreatePermitScreen(
                permitTypeId = permitTypeId,
                permitTypeName = permitTypeName,
                isFromDraft = isFromDraft,
                draftId = draftId,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.ObservationListScreen.route) {
            ObservationListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { navController.navigate(Screens.CreateObservationScreen.route) },
                onDraftClicked = { navController.navigate(Screens.ObservationDraftListScreen.route) }
            )
        }
        composable(Screens.ObservationDraftListScreen.route) {
            ObservationDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { draftId ->
                    navController.navigate("${Screens.CreateObservationScreen.route}?isFromDraft=true&draftId=$draftId")
                }
            )
        }
        composable(
            route = "${Screens.CreateObservationScreen.route}?isFromDraft={isFromDraft}&draftId={draftId}"
        ) { backStackEntry ->
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreateObservationScreen(
                isFromDraft = isFromDraft,
                draftId = draftId,
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screens.CreateIncidentScreen.route) {
            CreateIncidentScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(
            route = Screens.CreateIncidentScreenWithArgs.route,
            arguments = listOf(
                navArgument("isFromDraft") {
                    type = NavType.StringType
                    defaultValue = "false"
                },
                navArgument("draftId") {
                    type = NavType.StringType
                    defaultValue = "-1"
                }
            )
        ) { backStackEntry ->
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreateIncidentScreen(
                onBackClicked = { navController.popBackStack() },
                isFromDraft = isFromDraft,
                draftId = draftId
            )
        }
        composable(
            route = Screens.ObservationDetailsScreenWithArgs.route
        ) { backStackEntry ->
            val observationId = backStackEntry.savedStateHandle.get<String>("observationId")?.toIntOrNull() ?: -1
            ObservationDetailScreen(
                observationId = observationId,
                onBackClicked = { navController.popBackStack() },
                onRefreshList = {}
            )
        }
        composable(Screens.IncidentListScreen.route) {
            IncidentListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { navController.navigate(Screens.CreateIncidentScreen.route) },
                onDraftClicked = { navController.navigate(Screens.IncidentDraftListScreen.route) }
            )
        }
        composable(Screens.IncidentDraftListScreen.route) {
            IncidentDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { draftId ->
                    navController.navigate("create_incident_screen?isFromDraft=true&draftId=$draftId")
                }
            )
        }
        composable(Screens.ViolationListScreen.route) {
            ViolationListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { navController.navigate(Screens.CreateViolationScreen.route) },
                onDraftClicked = { navController.navigate(Screens.ViolationDraftListScreen.route) }
            )
        }
        composable(Screens.ViolationDraftListScreen.route) {
            ViolationDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { draftId ->
                    navController.navigate("${Screens.CreateViolationScreen.route}?isFromDraft=true&draftId=$draftId")
                }
            )
        }
        composable(
            route = "${Screens.CreateViolationScreen.route}?isFromDraft={isFromDraft}&draftId={draftId}"
        ) { backStackEntry ->
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreateViolationScreen(
                isFromDraft = isFromDraft,
                draftId = draftId,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.TrainingListScreen.route) {
            TrainingListScreen(
                onBackClicked = { navController.popBackStack() },
                onItemClicked = { trainingId ->
                    navController.navigate("${Screens.TrainingDetailScreen.route}/$trainingId")
                }
            )
        }
        composable(Screens.TrainingDetailScreenWithArgs.route) { backStackEntry ->
            val trainingId = backStackEntry.savedStateHandle.get<String>("trainingId")?.toIntOrNull() ?: -1
            TrainingDetailScreen(
                trainingId = trainingId,
                onBackClicked = { navController.popBackStack() },
                onPlayVideoClicked = { id ->
                    navController.navigate("${Screens.TrainingVideoScreen.route}/$id")
                },
                onScormVideoClicked = { url ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("scormUrl", url)
                    navController.navigate(Screens.TrainingScormScreen.route)
                },
                onStartQuizClicked = { id ->
                    navController.navigate("${Screens.QuizScreen.route}/$id")
                }
            )
        }
        composable(Screens.TrainingVideoScreenWithArgs.route) { backStackEntry ->
            val trainingId = backStackEntry.savedStateHandle.get<String>("trainingId")?.toIntOrNull() ?: -1
            TrainingVideoScreen(
                trainingId = trainingId,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.QuizScreenWithArgs.route) { backStackEntry ->
            val trainingId = backStackEntry.savedStateHandle.get<String>("trainingId")?.toIntOrNull() ?: -1
            QuizScreen(
                trainingId = trainingId,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.TrainingScormScreen.route) {
            val scormUrl = navController.previousBackStackEntry?.savedStateHandle?.get<String>("scormUrl") ?: ""
            TrainingScormScreen(
                scormUrl = scormUrl,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screens.PendingActionListScreen.route) {
            PendingActionListScreen(
                onBackClicked = { navController.popBackStack() },
                onPermitClick = { permitId -> navController.navigate("${Screens.PermitDetailScreen.route}/$permitId") },
                onNavigateToProject = { groupId, groupCode -> navController.navigate("${Screens.ProjectDetailScreen.route}/${groupId}/${groupCode}") }
            )
        }
        composable(Screens.ProfileScreen.route) {
            ProfileScreen(
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
                },
                onTrainingClick = { member ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("member", Json.encodeToString(member))
                    navController.navigate(Screens.AssignedTrainingsScreen.route)
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
        composable(Screens.AssignedTrainingsScreen.route) {
            val memberJson = navController.previousBackStackEntry?.savedStateHandle?.get<String>("member")
            val member = memberJson?.let {
                Json.decodeFromString<ProjectMember>(it)
            }
            if (member != null) {
                AssignedTrainingsView(
                    member = member,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
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
            TermsOfUseScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screens.LessonsLearnedListScreen.route) {
            LessonsLearnedListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { navController.navigate(Screens.CreateLessonsLearnedScreen.route) },
                onDraftClicked = { navController.navigate(Screens.LessonsLearnedDraftListScreen.route) }
            )
        }

        composable(Screens.CreateLessonsLearnedScreen.route) {
            CreateLessonsLearnedScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(
            route = Screens.CreateLessonsLearnedScreenWithArgs.route,
            arguments = listOf(
                navArgument("isFromDraft") {
                    type = NavType.StringType
                    defaultValue = "false"
                },
                navArgument("draftId") {
                    type = NavType.StringType
                    defaultValue = "-1"
                }
            )
        ) { backStackEntry ->
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreateLessonsLearnedScreen(
                onBackClicked = { navController.popBackStack() },
                isFromDraft = isFromDraft,
                draftId = draftId
            )
        }

        composable(Screens.LessonsLearnedDraftListScreen.route) {
            LessonsLearnedDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { id ->
                    navController.navigate("create_lessons_learned_screen?isFromDraft=true&draftId=$id")
                }
            )
        }
        composable(Screens.ToolBoxTalkListScreen.route) {
            ToolBoxTalkListScreen(
                onBackClicked = { navController.popBackStack() },
                onCreateClicked = { navController.navigate(Screens.CreateToolBoxTalkScreen.route) },
                onDraftClicked = { navController.navigate(Screens.ToolBoxTalkDraftListScreen.route) }
            )
        }
        composable(Screens.CreateToolBoxTalkScreen.route) {
            CreateToolBoxTalkScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(
            route = Screens.CreateToolBoxTalkScreenWithArgs.route,
            arguments = listOf(
                navArgument("isFromDraft") {
                    type = NavType.StringType
                    defaultValue = "false"
                },
                navArgument("draftId") {
                    type = NavType.StringType
                    defaultValue = "-1"
                }
            )
        ) { backStackEntry ->
            val isFromDraft = backStackEntry.savedStateHandle.get<String>("isFromDraft")?.toBooleanStrictOrNull() ?: false
            val draftId = backStackEntry.savedStateHandle.get<String>("draftId")?.toLongOrNull() ?: -1L
            CreateToolBoxTalkScreen(
                onBackClicked = { navController.popBackStack() },
                isFromDraft = isFromDraft,
                draftId = draftId
            )
        }
        composable(Screens.ToolBoxTalkDraftListScreen.route) {
            ToolBoxTalkDraftListScreen(
                onBackClicked = { navController.popBackStack() },
                onDraftClicked = { draftId ->
                    navController.navigate("create_toolbox_talk_screen?isFromDraft=true&draftId=$draftId")
                }
            )
        }
        composable(Screens.ToolBoxTalkDetailScreenWithArgs.route) { backStackEntry ->
            val toolboxTalkId = backStackEntry.savedStateHandle.get<String>("toolboxTalkId")?.toIntOrNull() ?: -1
            ToolBoxTalkDetailScreen(
                id = toolboxTalkId,
                onClose = { navController.popBackStack() }
            )
        }
        composable(Screens.PrivacyPolicyScreen.route) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screens.DeleteAccountScreen.route) {
            DeleteAccountScreen(onBack = { navController.popBackStack() })
        }
    }
}
