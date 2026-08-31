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
        Log.d("FCM", "New Token: $token")
        authPreferences.saveFCMToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message Received from: ${remoteMessage.from}")
        
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: $data")
        }
        
        // Handle notification payload
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            showNotification(it.title ?: "Notification", it.body ?: "", data)
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

        val notificationBuilder = android.app.Notification.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(android.app.Notification.PRIORITY_HIGH)
            .setDefaults(android.app.Notification.DEFAULT_ALL)

        val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
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
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
