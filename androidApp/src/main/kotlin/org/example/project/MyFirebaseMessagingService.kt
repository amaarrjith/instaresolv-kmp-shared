package org.example.project

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.example.project.data.settings.AuthPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val authPreferences: AuthPreferences by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (BuildConfig.DEBUG) {
            Log.d("FCM", "New Token: $token")
        }
        authPreferences.saveFCMToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (BuildConfig.DEBUG) {
            Log.d("FCM", "Message Received from: ${remoteMessage.from}")
        }
        
        val data = remoteMessage.data
        if (data.isNotEmpty() && BuildConfig.DEBUG) {
            Log.d("FCM", "Message data payload: $data")
        }
        
        val notificationTitle = remoteMessage.notification?.title ?: data["title"] ?: "Notification"
        val notificationBody = remoteMessage.notification?.body ?: data["body"] ?: data["message"] ?: ""

        if (notificationBody.isNotBlank() || remoteMessage.notification != null) {
            showNotification(notificationTitle, notificationBody, data)
        }
    }

    private fun showNotification(title: String, messageBody: String, data: Map<String, String>) {
        val channelId = "high_importance_channel"
        
        val intent = android.content.Intent(this, MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
            for ((key, value) in data) {
                putExtra(key, value)
            }
        }
        
        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            0,
            intent,
            android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        // Create the NotificationChannel on API 26+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "App Notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alerts and heads-up app notifications"
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setDefaults(androidx.core.app.NotificationCompat.DEFAULT_ALL)

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
                androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("FCM", "Failed to show notification: ${e.message}")
            }
        }
    }
}

