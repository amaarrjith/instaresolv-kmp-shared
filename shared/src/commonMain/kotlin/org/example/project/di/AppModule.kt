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

val appModule = module {
    factory { createHttpClient(get()) }
    factory<AuthApiService> { AuthApiServiceImpl(get()) }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single { LoginValidator() }
    single { RegisterValidator() }
    single { OTPValidator() }
    factory { ProjectViewModel(get(),get()) }
    factory { NotificationsViewModel(get()) }
    factory { AppTabBarViewModel(get(), get()) }
    factory { HomeScreenViewModel(get(), get()) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { SplashViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { CreateProjectViewModel(get(), get()) }
    factory { WelcomeScreenViewModel(get()) }
    factory { ForgetPasswordViewModel(get(), get()) }
    factory { ProjectDetailViewModel(get(), get()) }
    factory { RegisterViewModel(get(), get()) }
    factory { (email: String, tempUserId: Int) -> OTPVerificationViewModel(get(), get(), get(), email, tempUserId) }
    factory { ImagePickerViewModel(get()) }
    factory { GeneralContentsViewModel(get()) }
    factory { org.example.project.settings.ChangePasswordViewModel(get()) }
    factory { org.example.project.settings.DeleteAccountViewModel(get(), get()) }
    factory { org.example.project.settings.ContactUsViewModel(get()) }
    factory { PendingActionListViewModel(get()) }
    factory<PendingActionRepository> { PendingActionRepositoryImpl(get()) }
    single { GlobalSettingsViewModel(get()) }
    single { AuthPreferences(get()) }
    single { AppPreferences(get()) }
    single<Settings> { Settings() }
}
