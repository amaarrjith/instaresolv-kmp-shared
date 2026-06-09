package org.example.project.di

import org.example.project.data.remote.api.AuthApiService
import org.example.project.data.remote.api.AuthApiServiceImpl
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.AuthRepositoryImpl
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.domain.validation.LoginValidator
import org.example.project.login.LoginViewModel
import org.example.project.network.createHttpClient
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single<AuthApiService> { AuthApiServiceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { LoginValidator() }
    single { LoginUseCase(repository = get(), validator = get()) }
    factory { LoginViewModel(get()) }
}


