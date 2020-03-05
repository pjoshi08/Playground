@file:JvmName("WorkerUtils")

package com.pj.playground.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pj.playground.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Create a Notification that is shown as a heads-up notification if possible.
 *
 * This is used to show a notification so that you know when different steps
 * of the background work chain are starting
 *
 * @param message Message shown on the notification
 * @param context Context needed to create Toast
 */
fun makeStatusNotification(message: String, context: Context) {
    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = NOTIFICATION_CHANNEL_NAME
        val description = NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        // Add Channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    // Create Notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Display Notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

fun sleep() {
    try {
        Thread.sleep(DELAY_TIME_MILLIS, 0)
    } catch (e: InterruptedException) {
        Log.e("WorkerUtils", e.message ?: "Error Occurred")
    }
}

/**
 * Blurs the given Bitmap image
 * @param bitmap Image to blur
 * @param applicationContext Application context
 * @return Blurred bitmap image
 */
@WorkerThread
fun blurBitmap(bitmap: Bitmap, applicationContext: Context): Bitmap {
    lateinit var rsContext: RenderScript
    try {
        // Create output bitmap
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Blur the image
        rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG)
        val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
        val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
        val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
        theIntrinsic.apply {
            setRadius(10f)
            theIntrinsic.setInput(inAlloc)
            theIntrinsic.forEach(outAlloc)
        }
        outAlloc.copyTo(output)

        return output
    } finally {
        rsContext.finish()
    }
}

/**
 * Writes bitmap to a temporary file and returns the Uri for the file
 * @param applicationContext Application context
 * @param bitmap Bitmap to write to temp file
 * @return Uri for temp file with bitmap
 * @throws FileNotFoundException Throws if bitmap file cannot be found
 */
@Throws(FileNotFoundException::class)
fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name = "blur-filter-output-${UUID.randomUUID()}.png"
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdir()
    }

    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null

    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNG */, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (ignore: IOException) {
            }
        }
    }

    return Uri.fromFile(outputFile)
}