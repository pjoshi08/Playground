package com.pj.playground.view

import android.net.Uri
import androidx.lifecycle.ViewModel

class BlurViewModel : ViewModel() {
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

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