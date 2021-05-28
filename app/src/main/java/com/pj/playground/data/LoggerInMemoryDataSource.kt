package com.pj.playground.data

import dagger.hilt.android.scopes.ActivityScoped
import java.util.*
import javax.inject.Inject

/**
 * Instance of the [LoggerInMemoryDataSource] is in the [Activity] container and that
 * instance reused across Fragments.
 *
 * We can achieve the in-memory logging behavior by scoping [LoggerInMemoryDataSource]
 * to the Activity [container]: every Activity created will have its own container, a
 * different instance. On each container, the same instance of LoggerInMemoryDataSource
 * will be provided when the logger is needed as a dependency or for field injection.
 * Also, the same instance will be provided in containers below the [Components hierarchy]
 * [https://developer.android.com/training/dependency-injection/hilt-android#component-hierarchy]
 *
 * Following the [scoping to Components documentation]
 * [https://developer.android.com/training/dependency-injection/hilt-android#component-scopes],
 * to scope a type to the Activity
 * container, we need to annotate the type with @[ActivityScoped]
 */
@ActivityScoped
class LoggerInMemoryDataSource @Inject constructor() : LoggerDataSource {
    private val logs = LinkedList<Log>()

    override fun addLog(msg: String) {
        logs.addFirst(Log(msg, System.currentTimeMillis()))
    }

    override fun getAllLogs(callback: (List<Log>) -> Unit) {
        callback(logs)
    }

    override fun removeLogs() {
        logs.clear()
    }
}