package com.pj.playground.interactors

import com.pj.playground.data.DocumentRepository
import com.pj.playground.domain.Document

class AddDocument(private val repository: DocumentRepository) {
    suspend operator fun invoke(document: Document) = repository.addDocument(document)
}