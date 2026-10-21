package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedLocale()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        
        val intent = intent
        handleNotificationIntent(intent)
        
        // Fetch current FCM token on launch
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("FCM", "Current Token: $token")
                    }
                    val authPreferences = org.koin.mp.KoinPlatform.getKoin().get<org.example.project.data.settings.AuthPreferences>()
                    authPreferences.saveFCMToken(token)
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("FCM", "Failed to retrieve Firebase token on launch: ${e.message}")
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionsLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        applySavedLocale()
    }

    private fun applySavedLocale() {
        try {
            val appPreferences = org.koin.mp.KoinPlatform.getKoin().get<org.example.project.data.settings.AppPreferences>()
            val langCode = appPreferences.getLanguage()
            org.example.project.utilites.setAppLocale(langCode)
        } catch (_: Exception) {}
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: android.content.Intent?) {
        intent?.let {
            val typeStr = it.getStringExtra("type")
            val contentIdStr = it.getStringExtra("contentId")
            val groupCode = it.getStringExtra("groupCode")
            
            if (!typeStr.isNullOrEmpty() && !contentIdStr.isNullOrEmpty()) {
                val type = typeStr.toIntOrNull() ?: 0
                val contentId = contentIdStr.toIntOrNull() ?: 0
                
                try {
                    val appManager = org.koin.mp.KoinPlatform.getKoin().get<org.example.project.manager.AppManager>()
                    appManager.handleNotificationTap(type, contentId, groupCode)
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Failed to resolve AppManager or emit notification tap: ${e.message}")
                }
            }
        }
    }
}
