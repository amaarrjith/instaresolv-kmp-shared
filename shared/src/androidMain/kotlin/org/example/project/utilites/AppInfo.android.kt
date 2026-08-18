package org.example.project.utilites

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidAppInfo : AppInfo, KoinComponent {
    private val context: Context? by lazy {
        try {
            getKoin().get()
        } catch (e: Exception) {
            null
        }
    }

    override val appVersion: String
        get() = try {
            val ctx = context
            if (ctx != null) {
                val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
                pInfo.versionName ?: "1.0"
            } else {
                "1.0"
            }
        } catch (e: Exception) {
            "1.0"
        }
}

actual fun getAppInfo(): AppInfo = AndroidAppInfo()
