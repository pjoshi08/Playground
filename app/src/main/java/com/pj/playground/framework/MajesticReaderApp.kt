package com.pj.playground.framework

import android.app.Application
import com.pj.playground.data.BookmarkRepository
import com.pj.playground.data.DocumentRepository
import com.pj.playground.interactors.*

class MajesticReaderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val bookmarkRepository = BookmarkRepository(RoomBookmarkDataSource(this))
        val documentRepository = DocumentRepository(
            RoomDocumentDataSource(this),
            InMemoryOpenDocumentDataSource()
        )

        MajesticViewModelFactory
            .inject(
                this,
                Interactors(
                    AddBookmark(bookmarkRepository),
                    RemoveBookmark(bookmarkRepository),
                    GetBookmarks(bookmarkRepository),
                    AddDocument(documentRepository),
                    RemoveDocument(documentRepository),
                    GetDocuments(documentRepository),
                    SetOpenDocument(documentRepository),
                    GetOpenDocument(documentRepository)
                )
            )
    }
}