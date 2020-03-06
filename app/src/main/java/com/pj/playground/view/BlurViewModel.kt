package com.pj.playground.view

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pj.playground.util.KEY_IMAGE_URI
import com.pj.playground.workers.BlurWorker
import com.pj.playground.workers.CleanupWorker
import com.pj.playground.workers.SaveImageToFileWorker

class BlurViewModel(application: Application) : AndroidViewModel(application) {
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    private val workManager = WorkManager.getInstance(application)

    internal fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager
            .beginWith(
                OneTimeWorkRequest
                    .from(CleanupWorker::class.java)
            )

        // Add WorkRequests to blur the image the number of times requested
        for (i in 0 until blurLevel) {
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()

            // Input the Uri if this is the first blur operation
            // After the first blur operation the input will be the output of previous
            // blur operations.
            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }
            continuation = continuation.then(blurBuilder.build())
        }

        // Add WorkRequest to save the image to the filesystem
        val saveRequest = OneTimeWorkRequestBuilder<SaveImageToFileWorker>().build()
        continuation = continuation.then(saveRequest)

        // Actually start the work
        continuation.enqueue()
    }

    /**
     * Creates the input data bundle which includes the Uri to operate on
     * @return Data which contains the Image Uri as a String
     */
    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }

        return builder.build()
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    internal fun setImageUri(uriString: String?) {
        imageUri = uriOrNull(uriString)
    }

    internal fun setOutputUri(uriString: String?) {
        outputUri = uriOrNull(uriString)
    }
}