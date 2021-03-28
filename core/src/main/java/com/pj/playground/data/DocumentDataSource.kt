package com.pj.playground.data

import com.pj.playground.domain.Document

interface DocumentDataSource {
    suspend fun add(document: Document)

    suspend fun readAll(): List<Document>

    suspend fun remove(document: Document)
}