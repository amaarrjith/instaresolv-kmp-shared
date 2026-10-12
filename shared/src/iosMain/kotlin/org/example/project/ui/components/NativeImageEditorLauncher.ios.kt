package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

interface IosImageEditorBridge {
    fun launchEditor(imageBytes: ByteArray, onComplete: (ByteArray?) -> Unit)
}

object IosImageEditorProvider {
    var bridge: IosImageEditorBridge? = null
}

actual class NativeImageEditorLauncher(
    private val onLaunch: (ByteArray) -> Unit
) {
    actual fun launch(imageBytes: ByteArray) {
        onLaunch(imageBytes)
    }
}

@Composable
actual fun rememberNativeImageEditorLauncher(
    onResult: (ByteArray?) -> Unit
): NativeImageEditorLauncher {
    return remember {
        NativeImageEditorLauncher(
            onLaunch = { bytes ->
                val bridge = IosImageEditorProvider.bridge
                if (bridge != null) {
                    bridge.launchEditor(bytes) { resultBytes ->
                        onResult(resultBytes)
                    }
                } else {
                    onResult(bytes)
                }
            }
        )
    }
}
