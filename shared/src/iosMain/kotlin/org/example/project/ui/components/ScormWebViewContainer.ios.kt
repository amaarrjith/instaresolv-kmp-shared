package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIView
import platform.WebKit.WKUserContentController
import platform.WebKit.WKUserScript
import platform.WebKit.WKUserScriptInjectionTime
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ScormWebViewContainer(
    url: String,
    modifier: Modifier
) {
    val scormJs = """
        var scormData = {};
        function ok(){ return 'true'; }
        var api = {
            LMSInitialize: function(a) { return ok(); },
            LMSFinish: function(a) { return ok(); },
            LMSGetValue: function(key) { return scormData[key] || ''; },
            LMSSetValue: function(key, value) { scormData[key] = value; return ok(); },
            LMSCommit: function(a) { return ok(); },
            LMSGetLastError: function() { return '0'; },
            LMSGetErrorString: function(error) { return ''; },
            LMSGetDiagnostic: function(error) { return ''; }
        };
        api.Initialize = function(a) { return api.LMSInitialize(a); };
        api.Terminate = function(a) { return api.LMSFinish(a); };
        api.GetValue = function(a) { return api.LMSGetValue(a); };
        api.SetValue = function(a,b) { return api.LMSSetValue(a,b); };
        api.Commit = function(a) { return api.LMSCommit(a); };
        api.GetLastError = function() { return api.LMSGetLastError(); };
        api.GetErrorString = function(a) { return api.LMSGetErrorString(a); };
        api.GetDiagnostic = function(a) { return api.LMSGetDiagnostic(a); };
        window.API = api;
        window.API_1484_11 = api;
    """.trimIndent()

    val webView = remember(url) {
        val userScript = WKUserScript(
            source = scormJs,
            injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentStart,
            forMainFrameOnly = false
        )
        val userContentController = WKUserContentController()
        userContentController.addUserScript(userScript)

        val configuration = WKWebViewConfiguration()
        configuration.userContentController = userContentController

        val wkWebView = WKWebView(frame = platform.CoreGraphics.CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = configuration)
        val nsUrl = NSURL(string = url)
        val request = NSURLRequest(uRL = nsUrl)
        wkWebView.loadRequest(request)
        wkWebView
    }

    UIKitView(
        factory = { webView },
        modifier = modifier
    )
}
