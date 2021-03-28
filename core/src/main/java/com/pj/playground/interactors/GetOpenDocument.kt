package com.pj.playground.interactors

import com.pj.playground.data.OpenDocumentRepository

class GetOpenDocument(private val repository: OpenDocumentRepository) {
    suspend operator fun invoke() = repository.getOpenDocument()
}