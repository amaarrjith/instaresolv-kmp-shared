package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIDevice
import platform.UIKit.UIInterfaceOrientationLandscapeLeft
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.Foundation.NSNumber
import platform.Foundation.setValue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun LockScreenOrientation(landscape: Boolean) {
    DisposableEffect(landscape) {
        val device = UIDevice.currentDevice
        val orientationValue = if (landscape) {
            UIInterfaceOrientationLandscapeLeft
        } else {
            UIInterfaceOrientationPortrait
        }
        
        device.setValue(
            value = NSNumber(integer = orientationValue),
            forKey = "orientation"
        )
        
        onDispose {
            device.setValue(
                value = NSNumber(integer = UIInterfaceOrientationPortrait),
                forKey = "orientation"
            )
        }
    }
}
