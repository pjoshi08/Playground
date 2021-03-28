package com.pj.playground.interactors

import com.pj.playground.data.BookmarkRepository
import com.pj.playground.domain.Bookmark
import com.pj.playground.domain.Document

class RemoveBookmark(private val repository: BookmarkRepository) {
    suspend operator fun invoke(document: Document, bookmark: Bookmark) =
        repository.removeBookmark(document, bookmark)
}