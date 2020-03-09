package com.pj.playground.view

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.pj.playground.util.IMAGE_MANIPULATION_WORK_NAME
import com.pj.playground.util.KEY_IMAGE_URI
import com.pj.playground.util.TAG_OUTPUT
import com.pj.playground.workers.BlurWorker
import com.pj.playground.workers.CleanupWorker
import com.pj.playground.workers.SaveImageToFileWorker

class BlurViewModel(application: Application) : AndroidViewModel(application) {
    // New instance variable for the WorkInfo
    internal val outputWorkInfos: LiveData<List<WorkInfo>>
    private val workManager = WorkManager.getInstance(application)

    init {
        // This transformation makes sure that whenever the current work Id changes the WorkInfo
        // the UI is listening to changes
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    internal fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        // Unique chain of work
        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
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

        // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        // Add WorkRequest to save the image to the filesystem
        val saveRequest = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag(TAG_OUTPUT)
            .setConstraints(constraints)
            .build()
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

    internal fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }
}