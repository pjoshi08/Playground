package com.pj.playground.util

import com.pj.playground.domain.Document

interface MainActivityDelegate {

    fun openDocument(document: Document)
}