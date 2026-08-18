package org.example.project.utilites

import platform.Foundation.NSBundle

class IOSAppInfo : AppInfo {
    override val appVersion: String
        get() = (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String)
            ?: (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)
            ?: "1.0"
}

actual fun getAppInfo(): AppInfo = IOSAppInfo()
