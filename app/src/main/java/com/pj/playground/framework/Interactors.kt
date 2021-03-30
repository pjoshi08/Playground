package com.pj.playground.framework

import com.pj.playground.interactors.*

data class Interactors(
    val addBookmark: AddBookmark,
    val removeBookmark: RemoveBookmark,
    val getBookmarks: GetBookmarks,
    val addDocument: AddDocument,
    val removeDocument: RemoveDocument,
    val getDocuments: GetDocuments,
    val setOpenDocument: SetOpenDocument,
    val getOpenDocument: GetOpenDocument
)