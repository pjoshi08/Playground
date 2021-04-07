package com.pj.playground.interactors

import com.pj.playground.data.DocumentRepository

class GetOpenDocument(private val repository: DocumentRepository) {
    operator fun invoke() = repository.getOpenDocument()
}