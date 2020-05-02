package com.pj.playground.util

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.pj.playground.R

// Notification ID
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

// COMPLETED: Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, appContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    // TODO: Step 1.11 create intent

    // TODO: Step 1.12 create PendingIntent

    // TODO: Step 2.0 add style

    // TODO: Step 2.2 add snooze action

    // COMPLETED: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        appContext,
        appContext.getString(R.string.egg_notification_channel_id)
    )

    // TODO: Step 1.8 use the new 'breakfast' notification channel

        // COMPLETED: Step 1.3 set title, text and icon to builder
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(messageBody)

    // TODO: Step 1.13 set content intent

    // TODO: Step 2.1 add style to builder

    // TODO: Step 2.3 add snooze action

    // TODO: Step 2.5 set priority

    // COMPLETED: Step 1.4 call notify
    notify(NOTIFICATION_ID, builder.build())
}

// TODO: Step 1.14 Cancel all notifications