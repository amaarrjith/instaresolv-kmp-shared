package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AppWebView(url: String, modifier: Modifier) {
    UIKitView(
        factory = {
            val webView = WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = WKWebViewConfiguration())
            webView
        },
        update = { webView ->
            val request = NSURLRequest(NSURL(string = url))
            webView.loadRequest(request)
        },
        modifier = modifier
    )
}
