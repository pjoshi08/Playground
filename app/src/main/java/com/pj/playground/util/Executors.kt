package com.pj.playground.util

import java.util.concurrent.Executors

/**
 * An executor service that can run [Runnable]s off the main thread.
 */
val BACKGROUND = Executors.newFixedThreadPool(2)