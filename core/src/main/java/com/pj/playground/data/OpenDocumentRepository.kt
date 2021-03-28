package com.pj.playground.data

import com.pj.playground.domain.Document

class OpenDocumentRepository(private val dataSource: OpenDocumentDataSource) {
    suspend fun getOpenDocument() = dataSource.getOpenDocument()

    suspend fun setOpenDocument(document: Document) = dataSource.setOpenDocument(document)
}