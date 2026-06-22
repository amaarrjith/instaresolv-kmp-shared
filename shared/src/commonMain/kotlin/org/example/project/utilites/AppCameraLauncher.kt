package org.example.project.utilites

import androidx.compose.runtime.Composable

expect class AppCameraLauncher {
    fun launch()
}

@Composable
expect fun rememberAppCameraLauncher(onResult: (ByteArray?) -> Unit): AppCameraLauncher
