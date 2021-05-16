package com.pj.playground.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {

    @SuppressLint("SimpleDateFormat")
    private val formatter = SimpleDateFormat("d MMM yyyy HH:mm::ss")

    fun formatDate(timeStamp: Long): String = formatter.format(Date(timeStamp))
}