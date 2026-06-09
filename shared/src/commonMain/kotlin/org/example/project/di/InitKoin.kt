package com.example.instaresolv.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(config: KoinApplication.() -> Unit = {}) {
    startKoin {
        config()
        modules(appModule)
    }
}
