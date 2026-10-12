package org.example.project.di

import com.russhwolf.settings.Settings
import org.example.project.data.remote.api.AuthApiService
import org.example.project.data.remote.api.AuthApiServiceImpl
import org.example.project.data.settings.AppPreferences
import org.example.project.data.settings.AuthPreferences
import org.example.project.ui.viewmodel.GlobalSettingsViewModel
import org.example.project.data.repository.AuthRepositoryImpl
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.ProjectRepository
import org.example.project.domain.repository.ProjectRepositoryImpl
import org.example.project.domain.repository.PendingActionRepository
import org.example.project.domain.repository.PendingActionRepositoryImpl
import org.example.project.domain.repository.ObservationRepository
import org.example.project.domain.repository.ObservationRepositoryImpl
import org.example.project.domain.repository.InspectionRepository
import org.example.project.domain.repository.InspectionRepositoryImpl
import org.example.project.domain.validation.LoginValidator
import org.example.project.domain.validation.OTPValidator
import org.example.project.domain.validation.RegisterValidator
import org.example.project.forgetpassword.ForgetPasswordViewModel
import org.example.project.homescreen.HomeScreenViewModel
import org.example.project.login.LoginViewModel
import org.example.project.network.createHttpClient
import org.example.project.notifications.NotificationsViewModel
import org.example.project.otp.OTPVerificationViewModel
import org.example.project.profile.ProfileViewModel
import org.example.project.project.CreateProjectViewModel
import org.example.project.project.ProjectDetailViewModel
import org.example.project.project.ProjectViewModel
import org.example.project.register.RegisterViewModel
import org.example.project.splash.SplashViewModel
import org.example.project.network.NetworkMonitor
import org.example.project.tabbar.AppTabBarViewModel
import org.koin.dsl.module
import org.example.project.welcomescreen.WelcomeScreenViewModel
import org.example.project.ui.components.imagepicker.ImagePickerViewModel
import org.example.project.settings.GeneralContentsViewModel
import org.example.project.ui.screens.PendingActionListViewModel
import org.example.project.ui.screens.ObservationListViewModel
import org.example.project.ui.screens.CreateObservationViewModel
import org.example.project.ui.screens.CreateIncidentViewModel
import org.example.project.domain.repository.IncidentRepository
import org.example.project.domain.repository.IncidentRepositoryImpl
import org.example.project.domain.repository.LessonLearnedRepository
import org.example.project.domain.repository.LessonLearnedRepositoryImpl
import org.example.project.domain.repository.PermitRepository
import org.example.project.domain.repository.PermitRepositoryImpl
import org.example.project.domain.repository.PreTaskRepository
import org.example.project.domain.repository.PreTaskRepositoryImpl
import org.example.project.domain.repository.TrainingRepository
import org.example.project.domain.repository.TrainingRepositoryImpl
import org.example.project.ui.screens.IncidentListViewModel
import org.example.project.ui.screens.ViolationListViewModel
import org.example.project.domain.repository.ViolationRepository
import org.example.project.domain.repository.ViolationRepositoryImpl
import org.example.project.settings.ChangePasswordViewModel
import org.example.project.settings.ContactUsViewModel
import org.example.project.settings.DeleteAccountViewModel
import org.example.project.ui.components.AppProjectDropdownViewModel
import org.example.project.ui.screens.AuditInspectionListViewModel
import org.example.project.ui.screens.CreateInspectionViewModel
import org.example.project.ui.screens.CreateLessonLearnedViewModel
import org.example.project.ui.screens.CreatePreTaskViewModel
import org.example.project.ui.screens.CreateViolationViewModel
import org.example.project.ui.screens.IncidentDetailViewModel
import org.example.project.ui.screens.InspectionDetailViewModel
import org.example.project.ui.screens.LessonsLearnedDetailViewModel
import org.example.project.ui.screens.LessonsLearnedListViewModel
import org.example.project.ui.screens.ObservationDetailViewModel
import org.example.project.ui.screens.PermitToWorkListViewModel
import org.example.project.ui.screens.PreTaskDetailViewModel
import org.example.project.ui.screens.PreTaskListViewModel
import org.example.project.ui.screens.QuizViewModel
import org.example.project.ui.screens.TrainingDetailViewModel
import org.example.project.ui.screens.TrainingListViewModel
import org.example.project.ui.screens.TrainingVideoViewModel
import org.example.project.ui.screens.ViolationDetailViewModel
import org.example.project.ui.screens.CreatePermitViewModel
import org.example.project.ui.screens.PermitDetailViewModel
import org.example.project.ui.screens.ToolBoxTalkListViewModel
import org.example.project.ui.screens.CreateToolBoxTalkViewModel
import org.example.project.ui.screens.ToolBoxTalkDetailViewModel
import org.example.project.domain.repository.ToolBoxTalkRepository
import org.example.project.domain.repository.ToolBoxTalkRepositoryImpl
import org.example.project.data.repository.PermitDraftRepository
import org.example.project.ui.components.FilterBottomSheetViewModel
import org.example.project.db.DatabaseDriverFactory
import org.example.project.db.listOfStringsAdapter
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.ObservationDraft
import org.example.project.data.repository.ObservationDraftRepository
import org.example.project.shared.db.ViolationDraft
import org.example.project.data.repository.ViolationDraftRepository
import org.example.project.data.repository.LessonLearnedDraftRepository
import org.example.project.data.repository.IncidentDraftRepository
import org.example.project.data.repository.ToolBoxTalkDraftRepository
import org.example.project.data.repository.PreTaskDraftRepository
import org.example.project.data.repository.InspectionDraftRepository
import org.koin.core.module.dsl.viewModel

val appModule = module {
    single { createHttpClient(get(), get()) }
    factory<AuthApiService> { AuthApiServiceImpl(get()) }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory<org.example.project.data.remote.api.AudioApiService> { org.example.project.data.remote.api.AudioApiServiceImpl(get()) }
    factory<org.example.project.domain.repository.AudioRepository> { org.example.project.data.repository.AudioRepositoryImpl(get()) }
    factory<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single { LoginValidator() }
    single { RegisterValidator() }
    single { OTPValidator() }
    viewModel { ProjectViewModel(get(),get()) }
    viewModel { NotificationsViewModel(get()) }
    viewModel { AppTabBarViewModel(get(), get()) }
    viewModel { HomeScreenViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { CreateProjectViewModel(get(), get()) }
    viewModel { WelcomeScreenViewModel(get()) }
    viewModel { ForgetPasswordViewModel(get(), get()) }
    viewModel { ProjectDetailViewModel(get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { ObservationDetailViewModel(get(), get()) }
    viewModel { CreateObservationViewModel(get(), get(), get(), get()) }
    viewModel { CreateIncidentViewModel(get(), get(), get(), get()) }
    viewModel { AppProjectDropdownViewModel(get()) }
    viewModel { (email: String, tempUserId: Int) -> OTPVerificationViewModel(get(), get(), get(), email, tempUserId) }
    viewModel { ImagePickerViewModel(get()) }
    viewModel { GeneralContentsViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { DeleteAccountViewModel(get(), get()) }
    viewModel { ContactUsViewModel(get()) }
    factory<ToolBoxTalkRepository> { ToolBoxTalkRepositoryImpl(get()) }
    viewModel { ToolBoxTalkListViewModel(get(), get(), get()) }
    viewModel { CreateToolBoxTalkViewModel(get(), get(), get(), get()) }
    viewModel { ToolBoxTalkDetailViewModel(get()) }
    viewModel { org.example.project.ui.screens.AssignedTrainingsViewModel(get(), get()) }
    viewModel { PendingActionListViewModel(get(), get(), get(), get(), get()) }
    factory<PendingActionRepository> { PendingActionRepositoryImpl(get()) }
    viewModel { ObservationListViewModel(get(), get(), get()) }
    factory<ObservationRepository> { ObservationRepositoryImpl(get()) }
    factory<InspectionRepository> { InspectionRepositoryImpl(get()) }
    viewModel { AuditInspectionListViewModel(get(), get(), get()) }
    viewModel { CreateInspectionViewModel(get(), get(), get(), get()) }
    viewModel { InspectionDetailViewModel(get()) }
    viewModel { IncidentListViewModel(get(), get(), get()) }
    factory<IncidentRepository> { IncidentRepositoryImpl(get()) }
    viewModel { IncidentDetailViewModel(get()) }
    
    viewModel { ViolationListViewModel(get(), get(), get(), get()) }
    factory<ViolationRepository> { ViolationRepositoryImpl(get()) }
    viewModel { ViolationDetailViewModel(get()) }
    viewModel { org.example.project.ui.components.AddEmployeeViewModel(get()) }
    viewModel { FilterBottomSheetViewModel(get()) }
    viewModel { CreateViolationViewModel(get(), get(), get(), get()) }
    
    factory<PreTaskRepository> { PreTaskRepositoryImpl(get()) }
    viewModel { PreTaskListViewModel(get(), get(), get()) }
    viewModel { PreTaskDetailViewModel(get()) }
    viewModel { CreatePreTaskViewModel(get(), get(), get(), get(), get()) }
    
    factory<LessonLearnedRepository> { LessonLearnedRepositoryImpl(get()) }
    viewModel { LessonsLearnedListViewModel(get(), get(), get()) }
    viewModel { LessonsLearnedDetailViewModel(get()) }
    viewModel {
        CreateLessonLearnedViewModel(
            get(),
            authPreferences = get(),
            lessonLearnedDraftRepository = get()
        )
    }
    factory { LessonLearnedDraftRepository(get()) }

    factory<TrainingRepository> { TrainingRepositoryImpl(get()) }
    viewModel { TrainingListViewModel(get()) }
    viewModel { (trainingId: Int) -> TrainingDetailViewModel(get(), trainingId) }
    viewModel { (trainingId: Int) -> TrainingVideoViewModel(get(), trainingId) }
    viewModel { (trainingId: Int) -> QuizViewModel(get(), trainingId) }
    
    factory<PermitRepository> { PermitRepositoryImpl(get()) }
    factory { PermitDraftRepository(get()) }
    viewModel { PermitToWorkListViewModel(get(), get(), get()) }
    viewModel { CreatePermitViewModel(get(), get(), get()) }
    viewModel { PermitDetailViewModel(get(), get(), get()) }
    
    single { GlobalSettingsViewModel(get()) }
    single { AuthPreferences(get()) }
    single { AppPreferences(get()) }
    single<Settings> { Settings() }
    single {
        val manager = org.example.project.manager.AppManager(get(), get())
        org.example.project.manager.AppManager.init(manager)
        manager
    }
    
    single {
        val driverFactory = get<DatabaseDriverFactory>()
        AppDatabase(
            driver = driverFactory.createDriver(),
            ObservationDraftAdapter = ObservationDraft.Adapter(
                imageDescriptionsJsonAdapter = listOfStringsAdapter,
                notificationToJsonAdapter = listOfStringsAdapter
            ),
            ViolationDraftAdapter = ViolationDraft.Adapter(
                imagesJsonAdapter = listOfStringsAdapter
            )
        )
    }
    factory { ObservationDraftRepository(get()) }
    factory { ViolationDraftRepository(get()) }
    factory { IncidentDraftRepository(get()) }
    factory { ToolBoxTalkDraftRepository(get()) }
    factory { PreTaskDraftRepository(get()) }
    factory { InspectionDraftRepository(get()) }
}
