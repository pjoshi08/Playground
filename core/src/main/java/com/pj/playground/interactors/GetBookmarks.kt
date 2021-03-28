package com.pj.playground.interactors

import com.pj.playground.data.BookmarkRepository
import com.pj.playground.domain.Document

class GetBookmarks(private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(document: Document) = bookmarkRepository.getBookmarks(document)
}