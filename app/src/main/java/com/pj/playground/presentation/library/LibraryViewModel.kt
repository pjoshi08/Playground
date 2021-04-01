package com.pj.playground.presentation.library

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pj.playground.domain.Document
import com.pj.playground.framework.Interactors
import com.pj.playground.framework.MajesticViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(
    application: Application,
    interactors: Interactors
) : MajesticViewModel(application, interactors) {

    val documents: MutableLiveData<List<Document>> = MutableLiveData()

    init { loadDocuments() }

    fun loadDocuments() {
        viewModelScope.launch {
            documents.postValue(interactors.getDocuments())
        }
    }

    fun addDocument(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                interactors.addDocument(Document(uri.toString(), "", 0, ""))
            }

            loadDocuments()
        }
    }

    fun setOpenDocument(document: Document) {
        interactors.setOpenDocument(document)
    }
}