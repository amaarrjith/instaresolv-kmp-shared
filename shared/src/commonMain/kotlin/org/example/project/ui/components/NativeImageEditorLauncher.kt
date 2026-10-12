package org.example.project.ui.components

import androidx.compose.runtime.Composable

expect class NativeImageEditorLauncher {
    fun launch(imageBytes: ByteArray)
}

@Composable
expect fun rememberNativeImageEditorLauncher(
    onResult: (ByteArray?) -> Unit
): NativeImageEditorLauncher
