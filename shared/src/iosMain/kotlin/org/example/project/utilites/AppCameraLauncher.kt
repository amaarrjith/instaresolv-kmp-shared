package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

actual class AppCameraLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun resizeImageIfNeeded(image: UIImage, maxDimension: Double = 2048.0): UIImage {
    val (width, height) = image.size.useContents { width to height }
    if (width <= maxDimension && height <= maxDimension) {
        return image
    }
    val scale = minOf(maxDimension / width, maxDimension / height)
    val newWidth = width * scale
    val newHeight = height * scale
    val newSize = CGSizeMake(newWidth, newHeight)

    UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
    image.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resizedImage ?: image
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberAppCameraLauncher(onResult: (ByteArray?) -> Unit): AppCameraLauncher {
    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[platform.UIKit.UIImagePickerControllerOriginalImage] as? UIImage
                if (image != null) {
                    val resized = resizeImageIfNeeded(image)
                    val data = UIImageJPEGRepresentation(resized, 0.8)
                    if (data != null) {
                        val bytes = ByteArray(data.length.toInt())
                        if (bytes.isNotEmpty()) {
                            bytes.usePinned { pinned ->
                                memcpy(pinned.addressOf(0), data.bytes, data.length)
                            }
                        }
                        onResult(bytes)
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                onResult(null)
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }

    return remember {
        AppCameraLauncher(
            onLaunch = {
                if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                    val picker = UIImagePickerController()
                    picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    picker.delegate = delegate
                    val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                    rootVC?.presentViewController(picker, true, null)
                } else {
                    onResult(null)
                }
            }
        )
    }
}
