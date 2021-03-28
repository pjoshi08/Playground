package com.pj.playground.interactors

import com.pj.playground.data.BookmarkRepository
import com.pj.playground.domain.Bookmark
import com.pj.playground.domain.Document

class AddBookmark(private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(document: Document, bookmark: Bookmark) =
        bookmarkRepository.addBookmark(document, bookmark)
}