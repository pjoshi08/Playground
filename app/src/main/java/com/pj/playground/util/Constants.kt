@file:JvmName("Constants")

package com.pj.playground.util

// Notification Constants
@JvmField val NOTIFICATION_CHANNEL_NAME: CharSequence = "WorkManager Notifications"
const val NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
const val CHANNEL_ID = "Work_Notification"
const val NOTIFICATION_ID = 1
@JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"

// Image Manipulation work name
const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

// Other keys
const val OUTPUT_PATH = "blur_filter_outputs"
const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val TAG_OUTPUT = "OUTPUT"

const val DELAY_TIME_MILLIS: Long = 3000

// SelectImageActivity
const val REQUEST_CODE_IMAGE = 100
const val REQUEST_CODE_PERMISSION = 101

const val KEY_PERMISSION_REQUEST_COUNT = "KEY_PERMISSION_REQUEST_COUNT"
const val MAX_NUMBER_REQUEST_PERMISSION = 2
