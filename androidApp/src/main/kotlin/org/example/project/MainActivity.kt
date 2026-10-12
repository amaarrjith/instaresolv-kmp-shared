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
        
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }

        setContent {
            App()
        }
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
