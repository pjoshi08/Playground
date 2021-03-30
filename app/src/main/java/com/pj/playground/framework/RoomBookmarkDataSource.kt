package com.pj.playground.framework

import android.content.Context
import com.pj.playground.data.BookmarkDataSource
import com.pj.playground.domain.Bookmark
import com.pj.playground.domain.Document
import com.pj.playground.framework.db.BookmarkEntity
import com.pj.playground.framework.db.MajesticReaderDatabase

class RoomBookmarkDataSource(context: Context) : BookmarkDataSource {

    private val bookmarkDao = MajesticReaderDatabase.getInstance(context).bookmarkDao()

    override suspend fun add(document: Document, bookmark: Bookmark) = bookmarkDao
        .addBookmark(
            BookmarkEntity(
                documentUri = document.url,
                page = bookmark.page
            )
        )

    override suspend fun read(document: Document): List<Bookmark> = bookmarkDao
        .getBookmarks(document.url).map {
            Bookmark(it.id, it.page)
        }

    override suspend fun remove(document: Document, bookmark: Bookmark) = bookmarkDao
        .deleteBookark(
            BookmarkEntity(
                documentUri = document.url,
                page = bookmark.page
            )
        )
}