package org.example.project

import android.app.Application
import com.example.instaresolv.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class InstaResolvApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@InstaResolvApp)
        }
    }
}
