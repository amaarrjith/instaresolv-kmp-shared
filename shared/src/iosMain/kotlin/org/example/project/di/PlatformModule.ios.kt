package org.example.project.di

import org.koin.dsl.module
import org.koin.core.module.Module
import org.example.project.db.DatabaseDriverFactory
import org.example.project.db.NativeDatabaseDriverFactory
import org.example.project.network.NetworkMonitor
import org.example.project.network.IOSNetworkMonitor

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { NativeDatabaseDriverFactory() }
    single<NetworkMonitor> { IOSNetworkMonitor() }
}
