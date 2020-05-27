package com.pj.playground.fcm

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pj.playground.util.sendNotification

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG: String by lazy { this.javaClass.simpleName }

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        super.onMessageReceived(remoteMsg)

        remoteMsg.data.let {
            Log.d(TAG, "Message data payload: ${remoteMsg.data}")
        }

        remoteMsg.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body!!)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(msgBody: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(msgBody, applicationContext)
    }
}