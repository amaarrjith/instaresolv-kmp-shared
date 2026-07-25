package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class FileDownloader {
    @OptIn(ExperimentalForeignApi::class)
    actual fun downloadFile(url: String, fileName: String) {
        val nsUrl = NSURL(string = url)
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
                    dispatch_async(dispatch_get_main_queue()) {
                        val activityVC = UIActivityViewController(
                            activityItems = listOf(destinationUrl),
                            applicationActivities = null
                        )
                        
                        var rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                        if (rootVC == null) {
                            val windows = UIApplication.sharedApplication.windows
                            for (window in windows) {
                                if ((window as? UIWindow)?.isKeyWindow() == true) {
                                    rootVC = window.rootViewController
                                    break
                                }
                            }
                        }
                        
                        while (rootVC?.presentedViewController != null) {
                            rootVC = rootVC.presentedViewController
                        }
                        
                        rootVC?.presentViewController(activityVC, animated = true, completion = null)
                    }
                }
            } else if (error != null) {
                println("FileDownloader: Error downloading file - ${error.localizedDescription}")
            }
        }
        task.resume()
    }
}

@Composable
actual fun rememberFileDownloader(): FileDownloader {
    return remember { FileDownloader() }
}
