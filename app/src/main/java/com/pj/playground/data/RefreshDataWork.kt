package com.pj.playground.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

/**
 * Worker job to refresh titles from the network while the app is in the background.
 *
 * WorkManager is a library used to enqueue work that is guaranteed to execute after its constraints
 * are met. It can run work even when the app is in the background, or not running.
 */
class RefreshDataWork(context: Context, params: WorkerParameters, private val network: Network) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return Result.success() // TODO: Use Coroutines from WorkManager
    }

    class Factory(private val network: Network = getNetworkService()) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = RefreshDataWork(appContext, workerParameters, network)
    }
}