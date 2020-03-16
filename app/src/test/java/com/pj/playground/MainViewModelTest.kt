package com.pj.playground


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.pj.playground.data.TitleRepository
import com.pj.playground.fakes.NetworkFake
import com.pj.playground.fakes.TitleDaoFake
import com.pj.playground.utils.MainCoroutineScopeRule
import com.pj.playground.view.MainViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var subject: MainViewModel

    @Before
    fun setup() {
        subject = MainViewModel(
            TitleRepository(
                NetworkFake("OK"),
                TitleDaoFake("initial")
            )
        )
    }

    @Test
    fun whenMainClicked_updateTaps() {
        subject.onMainViewClicked()
        Truth.assertThat(subject.taps.value).isEqualTo("0 taps")
        coroutineScope.advanceTimeBy(1000)
        Truth.assertThat(subject.taps.value).isEqualTo("1 taps")
    }
}