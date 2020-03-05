package com.pj.playground.view

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.pj.playground.workers.BlurWorker

class BlurViewModel(application: Application) : AndroidViewModel(application) {
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    private val workManager = WorkManager.getInstance(application)

    internal fun applyBlur(blurLevel: Int) {
        workManager.enqueue(OneTimeWorkRequest.from(BlurWorker::class.java))
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