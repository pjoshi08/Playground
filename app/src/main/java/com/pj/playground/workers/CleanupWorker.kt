package com.pj.playground.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pj.playground.util.OUTPUT_PATH
import java.io.File
import java.lang.Exception

/**
 * Cleans up temporary files generated during blurring process
 */
class CleanupWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    private val TAG by lazy { CleanupWorker::class.java.simpleName }

    override fun doWork(): Result {
        // Makes a notification when the work starts and slows down the work so that
        // it's easier to see each WorkRequest start, even on emulated devices
        makeStatusNotification("Cleaning up old temporary files", applicationContext)
        sleep()

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)

            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        val deleted = entry.delete()
                        Log.i(TAG, "deleted $name - $deleted")
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message)
            Result.failure()
        }
    }
}