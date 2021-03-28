package com.pj.playground.data

import com.pj.playground.domain.Document

interface OpenDocumentDataSource {
    fun setOpenDocument(document: Document)

    fun getOpenDocument(): Document
}