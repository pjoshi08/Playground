package com.pj.playground.view

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pj.playground.util.KEY_IMAGE_URI
import com.pj.playground.workers.BlurWorker

class BlurViewModel(application: Application) : AndroidViewModel(application) {
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    private val workManager = WorkManager.getInstance(application)

    internal fun applyBlur(blurLevel: Int) {
        val request = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(createInputDataForUri())
            .build()

        workManager.enqueue(request)
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