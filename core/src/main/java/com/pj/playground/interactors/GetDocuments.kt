package com.pj.playground.interactors

import com.pj.playground.data.DocumentRepository

class GetDocuments(private val documentRepository: DocumentRepository) {
    suspend operator fun invoke() = documentRepository.readAllDocuments()
}