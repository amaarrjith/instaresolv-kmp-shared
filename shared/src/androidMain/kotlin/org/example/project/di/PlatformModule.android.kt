package org.example.project.di

import org.koin.dsl.module
import org.koin.core.module.Module
import org.koin.android.ext.koin.androidContext
import org.example.project.db.DatabaseDriverFactory
import org.example.project.db.AndroidDatabaseDriverFactory

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
}
