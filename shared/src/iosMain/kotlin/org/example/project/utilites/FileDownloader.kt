package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import org.example.project.config.AppConfig

@OptIn(ExperimentalForeignApi::class)
internal fun getTopmostViewController(): UIViewController? {
    var keyWindow: UIWindow? = null
    val scenes = UIApplication.sharedApplication.connectedScenes
    for (scene in scenes) {
        val windowScene = scene as? UIWindowScene ?: continue
        if (windowScene.activationState == UISceneActivationStateForegroundActive) {
            for (window in windowScene.windows) {
                val uiWindow = window as? UIWindow ?: continue
                if (uiWindow.isKeyWindow()) {
                    keyWindow = uiWindow
                    break
                }
            }
            if (keyWindow != null) break
        }
    }
    if (keyWindow == null) {
        val windows = UIApplication.sharedApplication.windows
        for (window in windows) {
            val uiWindow = window as? UIWindow ?: continue
            if (uiWindow.isKeyWindow()) {
                keyWindow = uiWindow
                break
            }
        }
    }
    if (keyWindow == null) {
        keyWindow = UIApplication.sharedApplication.keyWindow
    }

    var topVC = keyWindow?.rootViewController
    while (true) {
        val presented = topVC?.presentedViewController
        if (presented != null && !presented.isBeingDismissed()) {
            topVC = presented
        } else {
            break
        }
    }
    return topVC
}

actual class FileDownloader {
    @OptIn(ExperimentalForeignApi::class)
    actual fun downloadFile(url: String, fileName: String) {
        val fullUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("/") -> {
                val host = AppConfig.baseUrl.substringBefore("/api").trimEnd('/')
                "$host$url"
            }
            else -> {
                "${AppConfig.baseUrl.trimEnd('/')}/$url"
            }
        }
        val encodedUrl = fullUrl.replace(" ", "%20")
        val nsUrl = NSURL(string = encodedUrl) ?: run {
            println("FileDownloader: Invalid URL: $encodedUrl")
            return
        }

        val request = NSURLRequest(uRL = nsUrl)
        val session = NSURLSession.sharedSession
        
        val task = session.downloadTaskWithRequest(request) { location, _, error ->
            if (location != null && error == null) {
                val fileManager = NSFileManager.defaultManager
                val documentsPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
                val destinationUrl = NSURL.fileURLWithPath("$documentsPath/$fileName")
                
                try {
                    fileManager.removeItemAtURL(destinationUrl, null)
                } catch (e: Exception) {
                    // Ignore if file doesn't exist
                }
                
                val moveSuccess = try {
                    fileManager.moveItemAtURL(location, destinationUrl, null)
                } catch (e: Exception) {
                    println("FileDownloader: Error moving file - ${e.message}")
                    false
                }
                
                if (moveSuccess) {
                    presentActivitySheet(destinationUrl)
                }
            } else if (error != null) {
                println("FileDownloader: Error downloading file - ${error.localizedDescription}")
            }
        }
        task.resume()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun presentActivitySheet(destinationUrl: NSURL, retryCount: Int = 0) {
        dispatch_async(dispatch_get_main_queue()) {
            val topVC = getTopmostViewController()
            if (topVC == null || topVC.isBeingDismissed()) {
                if (retryCount < 5) {
                    val delayTime = dispatch_time(DISPATCH_TIME_NOW, 150_000_000L)
                    dispatch_after(delayTime, dispatch_get_main_queue()) {
                        presentActivitySheet(destinationUrl, retryCount + 1)
                    }
                    return@dispatch_async
                }
            }

            val targetVC = topVC ?: return@dispatch_async
            val activityVC = UIActivityViewController(
                activityItems = listOf(destinationUrl),
                applicationActivities = null
            )
            activityVC.popoverPresentationController?.let { popover ->
                popover.sourceView = targetVC.view
                popover.sourceRect = targetVC.view.bounds
            }
            targetVC.presentViewController(activityVC, animated = true, completion = null)
        }
    }
}

@Composable
actual fun rememberFileDownloader(): FileDownloader {
    return remember { FileDownloader() }
}
