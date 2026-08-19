package org.example.project.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun ScormWebViewContainer(
    url: String,
    modifier: Modifier
) {
    val context = LocalContext.current

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

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, pageUrl: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, pageUrl, favicon)
                    view?.evaluateJavascript(scormJs, null)
                }
            }
            loadUrl(url)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}
