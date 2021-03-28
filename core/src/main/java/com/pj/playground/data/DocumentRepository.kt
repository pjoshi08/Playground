package com.pj.playground.data

import com.pj.playground.domain.Document

class DocumentRepository(private val dataSource: DocumentDataSource) {
    suspend fun addDocument(document: Document) = dataSource.add(document)

    suspend fun readAllDocuments() = dataSource.readAll()

    suspend fun removeDocument(document: Document) = dataSource.remove(document)
}