package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time

actual class TextSharer {
    @OptIn(ExperimentalForeignApi::class)
    actual fun shareText(text: String, title: String?) {
        presentTextSheet(text)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun presentTextSheet(text: String, retryCount: Int = 0) {
        dispatch_async(dispatch_get_main_queue()) {
            val topVC = getTopmostViewController()
            if (topVC == null || topVC.isBeingDismissed()) {
                if (retryCount < 5) {
                    val delayTime = dispatch_time(DISPATCH_TIME_NOW, 150_000_000L)
                    dispatch_after(delayTime, dispatch_get_main_queue()) {
                        presentTextSheet(text, retryCount + 1)
                    }
                    return@dispatch_async
                }
            }

            val targetVC = topVC ?: return@dispatch_async
            val activityItems = listOf(text)
            val activityVC = UIActivityViewController(
                activityItems = activityItems,
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
actual fun rememberTextSharer(): TextSharer {
    return remember { TextSharer() }
}
