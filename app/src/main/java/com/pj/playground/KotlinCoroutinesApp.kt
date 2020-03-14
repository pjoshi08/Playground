package com.pj.playground

import android.app.Application
import androidx.work.*
import com.pj.playground.data.RefreshDataWork
import java.util.concurrent.TimeUnit

class KotlinCoroutinesApp : Application() {
    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        setupWorkManagerJob()
    }

    /**
     * Setup WorkManager background job to 'fetch' new network data daily.
     */
    private fun setupWorkManagerJob() {
        // initialize workmanager with a factory
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(RefreshDataWork.Factory())
            .build()
        WorkManager.initialize(this, workManagerConfig)

        // Use constraints to require the work only run when the device is charging and the
        // network is unmetered
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        // Specify that the work should attempt to run every day
        val work = PeriodicWorkRequestBuilder<RefreshDataWork>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // Enqueue it work WorkManager, keeping any previously scheduled jobs for the same
        // work.
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                RefreshDataWork::class.java.name,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
    }
}