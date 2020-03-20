package com.pj.playground.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.pj.playground.utils.BACKGROUND
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * TitleRepository provides an interface to fetch a title or request a new one be generated.
 *
 * Repository modules handle data operations. They provide a clean API so that the rest of the app
 * can retrieve this data easily. They know where to get the data from and what API calls to make
 * when data is updated. You can consider repositories to be mediators between different data
 * sources, in our case it mediates between a network API and an offline database cache.
 */
class TitleRepository(val network: Network, val titleDao: TitleDao) {

    /**
     * [LiveData] to load title.
     *
     * This is the main interface for loading a title. The title will be loaded from the offline
     * cache.
     *
     * Observing this will not cause the title to be refreshed, use [TitleRepository.refreshTitleWithCallbacks]
     * to refresh the title.
     */
    val title: LiveData<String?> = titleDao.titleLiveData.map { it?.title }

    // COMPLETED: Add coroutines-based `fun refreshTitle` here
    suspend fun refreshTitle() {
        // COMPLETED: Refresh from network and write to database
        // interact with *blocking* network and IO calls from a coroutine
        try {
            // Make network request using a blocking call
            val result = withTimeout(5_000) {
                network.fetchNextTitle()
            }
            // Save it to database
            titleDao.insertTitle(Title((result)))
        } catch (cause: Throwable) {
            // If the network throws an exception, inform the caller
            throw TitleRefreshError("Unable to refresh title", cause)
        }

        // Advanced Tip: We can use Coroutine Cancellation here
    }
}

/**
 * Thrown when there was a error fetching a new title
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class TitleRefreshError(message: String, cause: Throwable?) : Throwable(message, cause)