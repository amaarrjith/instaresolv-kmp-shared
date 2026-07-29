package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIDevice
import platform.UIKit.UIInterfaceOrientationLandscapeLeft
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.Foundation.NSNumber
import platform.Foundation.setValue
import platform.Foundation.NSNotificationCenter

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun LockScreenOrientation(landscape: Boolean) {
    DisposableEffect(landscape) {
        NSNotificationCenter.defaultCenter.postNotificationName(
            aName = "OrientationLockChanged",
            `object` = null,
            userInfo = mapOf("landscape" to landscape)
        )
        
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
            NSNotificationCenter.defaultCenter.postNotificationName(
                aName = "OrientationLockChanged",
                `object` = null,
                userInfo = mapOf("landscape" to false)
            )
            device.setValue(
                value = NSNumber(integer = UIInterfaceOrientationPortrait),
                forKey = "orientation"
            )
        }
    }
}
