package com.pj.playground.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pj.playground.data.Network
import com.pj.playground.data.Title
import com.pj.playground.data.TitleDao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fake [TitleDao] for use in tests.
 */
class TitleDaoFake(initialTitle: String) : TitleDao {
    /**
     * A channel is a Coroutines based implementation of a blocking queue.
     *
     * We're using it here as a buffer of inserted elements.
     *
     * This uses a channel instead of a list to allow multiple threads to call insertTitle and
     * synchronize the results with the test thread.
     */
    private val insertedForNext = Channel<Title>(capacity = Channel.BUFFERED)

    override fun insertTitle(title: Title) {
        insertedForNext.offer(title)
        _titleLiveData.value = title
    }

    private val _titleLiveData = MutableLiveData(Title(initialTitle))

    override val titleLiveData: LiveData<Title?>
        get() = _titleLiveData

    /**
     * Assertion that the next element inserted has a title of expected
     *
     * If the element was previously inserted and is currently the most recent element
     * this assertion will also match. This allows tests to avoid synchronizing calls to insert
     * with calls to assertNextInsert.
     *
     * If multiple items were inserted, this will always match the first item that was not
     * previously matched.
     *
     * @param expected the value to match
     * @param timeout duration to wait (this is provided for instrumentation tests that may run on
     *                multiple threads)
     * @param unit timeunit
     * @return the next value that was inserted into this dao, or null if none found
     */
    fun nextInsertedOrNull(timeOut: Long = 2_000): String? {
        var result: String? = null
        runBlocking {
            // wait for the next insertion to complete
            try {
                withTimeout(timeOut) {
                    result = insertedForNext.receive().title
                }
            } catch (e: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

/**
 * Testing Fake implementation of MainNetwork
 */
class NetworkFake(var result: String) : Network {
    override fun fetchNextTitle(): Call<String> =
        MakeCompilerHappyForStarterCode() // TODO: replace with `result`
}

/**
 * Testing Fake for MainNetwork that lets you complete or error all current requests
 */
class NetworkCompletableFake() : Network {
    private var completable = CompletableDeferred<String>()

    override fun fetchNextTitle(): Call<String> =
        MakeCompilerHappyForStarterCode() // TODO: replace with `completable.await()`

    fun sendCompletionToAllCurrentRequests(result: String) {
        completable.complete(result)
        completable = CompletableDeferred()
    }

    fun sendErrorToCurrentRequests(throwable: Throwable) {
        completable.completeExceptionally(throwable)
        completable = CompletableDeferred()
    }
}

typealias MakeCompilerHappyForStarterCode = FakeCallForRetrofit<String>

/**
 * This class only exists to make the starter code compile. Remove after refactoring retrofit to use
 * suspend functions.
 */
class FakeCallForRetrofit<T> : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        // nothing
    }

    override fun isExecuted(): Boolean = false

    override fun clone(): Call<T> {
        return this
    }

    override fun isCanceled(): Boolean = true

    override fun cancel() {
        // nothing
    }

    override fun execute(): Response<T> {
        TODO("Not implemented")
    }

    override fun request(): Request {
        TODO("Not implemented")
    }
}