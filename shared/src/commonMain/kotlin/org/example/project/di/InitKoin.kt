package org.example.project.di

import org.example.project.di.appModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

public fun initKoin(config: KoinApplication.() -> Unit = {}) {
    startKoin {
        config()
        modules(appModule)
    }
}
