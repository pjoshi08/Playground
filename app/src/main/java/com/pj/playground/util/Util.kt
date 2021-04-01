package com.pj.playground.util

import android.content.Intent
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow

fun readableFileSize(size: Int): String {
    if (size <= 0) return "0"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = ((ln(size.toDouble())) / ln(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

fun createOpenIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "application/pdf"
}

