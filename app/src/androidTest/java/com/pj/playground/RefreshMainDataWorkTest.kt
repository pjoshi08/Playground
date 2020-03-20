package com.pj.playground

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import com.pj.playground.data.RefreshDataWork
import com.pj.playground.fakes.NetworkFake
import org.junit.Test

class RefreshMainDataWorkTest {

    @Test
    fun testRefreshDataWork() {
        val fakeNetwork = NetworkFake("OK")

        val context = ApplicationProvider.getApplicationContext<Context>()
        val worker = TestListenableWorkerBuilder<RefreshDataWork>(context)
            .setWorkerFactory(RefreshDataWork.Factory(fakeNetwork))
            .build()

        // start the work synchronously
        val result = worker.startWork().get()

        assertThat(result).isEqualTo(Result.success())
    }
}