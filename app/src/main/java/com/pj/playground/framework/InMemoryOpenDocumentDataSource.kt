package com.pj.playground.framework

import com.pj.playground.data.OpenDocumentDataSource
import com.pj.playground.domain.Document

/**
 * This class stores the currently open document in memory
 */
class InMemoryOpenDocumentDataSource : OpenDocumentDataSource {

    private var openDocument = Document.EMPTY

    override fun setOpenDocument(document: Document) {
        openDocument = document
    }

    override fun getOpenDocument() = openDocument
}