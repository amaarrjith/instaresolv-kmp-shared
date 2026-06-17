package org.example.project.di

import com.russhwolf.settings.Settings
import org.example.project.data.remote.api.AuthApiService
import org.example.project.data.remote.api.AuthApiServiceImpl
import org.example.project.data.settings.AuthPreferences
import org.example.project.data.repository.AuthRepositoryImpl
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.validation.LoginValidator
import org.example.project.domain.validation.OTPValidator
import org.example.project.domain.validation.RegisterValidator
import org.example.project.forgetpassword.ForgetPasswordViewModel
import org.example.project.homescreen.HomeScreenViewModel
import org.example.project.login.LoginViewModel
import org.example.project.network.createHttpClient
import org.example.project.otp.OTPVerificationViewModel
import org.example.project.profile.ProfileViewModel
import org.example.project.register.RegisterViewModel
import org.example.project.splash.SplashViewModel
import org.koin.dsl.module
import org.example.project.welcomescreen.WelcomeScreenViewModel

val appModule = module {
    single { createHttpClient(get()) }
    single<AuthApiService> { AuthApiServiceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { LoginValidator() }
    single { RegisterValidator() }
    single { OTPValidator() }
    factory { HomeScreenViewModel(get(), get()) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { SplashViewModel(get(), get()) }
    factory { ProfileViewModel(get()) }
    factory { WelcomeScreenViewModel(get()) }
    factory { ForgetPasswordViewModel(get(), get()) }
    factory { RegisterViewModel(get(), get()) }
    factory { (email: String, tempUserId: Int) -> OTPVerificationViewModel(get(), get(), get(), email, tempUserId) }
    single { AuthPreferences(get()) }
    single<Settings> { Settings() }
}


