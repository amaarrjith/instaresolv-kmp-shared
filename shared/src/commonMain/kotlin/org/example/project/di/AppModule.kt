package com.example.instaresolv.di

import com.example.instaresolv.data.remote.api.AuthApiService
import com.example.instaresolv.data.remote.api.AuthApiServiceImpl
import com.example.instaresolv.domain.repository.AuthRepository
import com.example.instaresolv.domain.repository.AuthRepositoryImpl
import com.example.instaresolv.domain.usecase.LoginUseCase
import com.example.instaresolv.domain.validation.LoginValidator
import com.example.instaresolv.login.LoginViewModel
import com.example.instaresolv.network.createHttpClient
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
    single<AuthApiService> { AuthApiServiceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { LoginValidator() }
    single { LoginUseCase(repository = get(), validator = get()) }
    factory { LoginViewModel(get()) }
}
