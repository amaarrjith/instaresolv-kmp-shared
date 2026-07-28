package org.example.project.di

import org.koin.dsl.module
import org.koin.core.module.Module
import org.example.project.db.DatabaseDriverFactory
import org.example.project.db.NativeDatabaseDriverFactory

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { NativeDatabaseDriverFactory() }
}
