package com.pj.playground.data

import com.pj.playground.domain.Bookmark
import com.pj.playground.domain.Document

interface BookmarkDataSource {
    suspend fun add(document: Document, bookmark: Bookmark)

    suspend fun read(document: Document): List<Bookmark>

    suspend fun remove(document: Document, bookmark: Bookmark)
}