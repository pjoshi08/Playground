package com.pj.playground

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.pj.playground.data.TitleRefreshError
import com.pj.playground.data.TitleRepository
import com.pj.playground.fakes.NetworkCompletableFake
import com.pj.playground.fakes.NetworkFake
import com.pj.playground.fakes.TitleDaoFake
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class TitleRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun whenRefreshTitleSuccess_insertRows() = runBlockingTest {
        // COMPLETED: Write this test
        val titleDao = TitleDaoFake("title")
        val subject = TitleRepository(
            NetworkFake("Ok"),
            titleDao
        )

        subject.refreshTitle()
        Truth.assertThat(titleDao.nextInsertedOrNull()).isEqualTo("Ok")
    }

    @Test(expected = TitleRefreshError::class)
    fun whenRefreshTitleTimeout_throws() = runBlockingTest {
        // COMPLETED: Write this test
        val network = NetworkCompletableFake()
        val subject = TitleRepository(
            network,
            TitleDaoFake("title")
        )

        launch {
           subject.refreshTitle()
        }

        advanceTimeBy(5_000)
    }
}