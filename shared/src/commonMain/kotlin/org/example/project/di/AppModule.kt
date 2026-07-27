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
import org.example.project.ui.components.FilterBottomSheetViewModel

val appModule = module {
    factory { createHttpClient(get(), get()) }
    factory<AuthApiService> { AuthApiServiceImpl(get()) }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory<org.example.project.data.remote.api.AudioApiService> { org.example.project.data.remote.api.AudioApiServiceImpl(get()) }
    factory<org.example.project.domain.repository.AudioRepository> { org.example.project.data.repository.AudioRepositoryImpl(get()) }
    factory<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single { LoginValidator() }
    single { RegisterValidator() }
    single { OTPValidator() }
    factory { ProjectViewModel(get(),get()) }
    factory { NotificationsViewModel(get()) }
    factory { AppTabBarViewModel(get(), get()) }
    factory { HomeScreenViewModel(get(), get(), get(), get(), get()) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { SplashViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { CreateProjectViewModel(get(), get()) }
    factory { WelcomeScreenViewModel(get()) }
    factory { ForgetPasswordViewModel(get(), get()) }
    factory { ProjectDetailViewModel(get(), get()) }
    factory { RegisterViewModel(get(), get()) }
    factory { ObservationDetailViewModel(get(), get()) }
    factory { CreateObservationViewModel(get(), get(), get()) }
    factory { CreateIncidentViewModel(get(), get(), get()) }
    factory { AppProjectDropdownViewModel(get()) }
    factory { (email: String, tempUserId: Int) -> OTPVerificationViewModel(get(), get(), get(), email, tempUserId) }
    factory { ImagePickerViewModel(get()) }
    factory { GeneralContentsViewModel(get()) }
    factory { ChangePasswordViewModel(get()) }
    factory { DeleteAccountViewModel(get(), get()) }
    factory { ContactUsViewModel(get()) }
    factory<ToolBoxTalkRepository> { ToolBoxTalkRepositoryImpl(get()) }
    factory { ToolBoxTalkListViewModel(get()) }
    factory { CreateToolBoxTalkViewModel(get(), get(), get()) }
    factory { ToolBoxTalkDetailViewModel(get()) }
    factory { PendingActionListViewModel(get(), get(), get(), get()) }
    factory<PendingActionRepository> { PendingActionRepositoryImpl(get()) }
    factory { ObservationListViewModel(get()) }
    factory<ObservationRepository> { ObservationRepositoryImpl(get()) }
    factory<InspectionRepository> { InspectionRepositoryImpl(get()) }
    factory { AuditInspectionListViewModel(get()) }
    factory { CreateInspectionViewModel(get(), get()) }
    factory { InspectionDetailViewModel(get()) }
    factory { IncidentListViewModel(get()) }
    factory<IncidentRepository> { IncidentRepositoryImpl(get()) }
    factory { IncidentDetailViewModel(get()) }
    
    factory { ViolationListViewModel(get(), get()) }
    factory<ViolationRepository> { ViolationRepositoryImpl(get()) }
    factory { ViolationDetailViewModel(get()) }
    factory { FilterBottomSheetViewModel(get()) }
    factory { CreateViolationViewModel(get(), get(), get()) }
    
    factory<PreTaskRepository> { PreTaskRepositoryImpl(get()) }
    factory { PreTaskListViewModel(get()) }
    factory { PreTaskDetailViewModel(get()) }
    factory { CreatePreTaskViewModel(get(), get(), get()) }
    
    factory<LessonLearnedRepository> { LessonLearnedRepositoryImpl(get()) }
    factory { LessonsLearnedListViewModel(get()) }
    factory { LessonsLearnedDetailViewModel(get()) }
    factory {
        CreateLessonLearnedViewModel(
            get(),
            authPreferences = get()
        )
    }

    factory<TrainingRepository> { TrainingRepositoryImpl(get()) }
    factory { TrainingListViewModel(get()) }
    factory { (trainingId: Int) -> TrainingDetailViewModel(get(), trainingId) }
    factory { (trainingId: Int) -> TrainingVideoViewModel(get(), trainingId) }
    factory { (trainingId: Int) -> QuizViewModel(get(), trainingId) }
    
    factory<PermitRepository> { PermitRepositoryImpl(get()) }
    factory { PermitToWorkListViewModel(get(), get()) }
    factory { CreatePermitViewModel(get(), get()) }
    factory { PermitDetailViewModel(get(), get(), get()) }
    
    single { GlobalSettingsViewModel(get()) }
    single { AuthPreferences(get()) }
    single { AppPreferences(get()) }
    single<Settings> { Settings() }
}
