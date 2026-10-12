package org.example.project

import android.app.Application
import org.example.project.config.AppConfig
import org.example.project.config.AppEnvironment
import org.example.project.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class InstaResolvApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        AppConfig.environment = if (BuildConfig.DEBUG) {
            AppEnvironment.DEVELOPMENT
        } else {
            AppEnvironment.PRODUCTION
        }

        initKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.ERROR)
            }
            androidContext(this@InstaResolvApp)
        }
    }
}
