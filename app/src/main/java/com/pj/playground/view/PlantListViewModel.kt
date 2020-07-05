package com.pj.playground.view

import androidx.lifecycle.*
import com.example.sunflower.GrowZone
import com.example.sunflower.NoGrowZone
import com.example.sunflower.Plant
import com.pj.playground.data.PlantRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * The [ViewModel] for fetching a list of [Plant]s.
 */
class PlantListViewModel internal constructor(
    private val repository: PlantRepository
) : ViewModel() {

    /**
     * Request a snackbar to display a string.
     *
     * This variable is private because we don't want to expose [MutableLiveData].
     *
     * MutableLiveData allows anyone to set a value, and [PlantListViewModel] is the only
     * class that should be setting values.
     */
    private val _snackBar = MutableLiveData<String?>()

    /**
     * Request a snackbar to display a string.
     */
    val snackBar: LiveData<String?>
        get() = _snackBar

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    /**
     * The current growZone selection.
     */
    private val growZone = MutableLiveData<GrowZone>(NoGrowZone)

    /**
     * A list of plants that updates based on the current filter.
     * SwitchMap: A switchmap applies a given function to the input livedata(growZone)
     * and returns the transformed result as a livedata
     */
    val plants: LiveData<List<Plant>> = growZone.switchMap { growZone ->
        if (growZone == NoGrowZone) {
            repository.plants
        } else {
            repository.getPlantsWithGrowZone(growZone)
        }
    }

    /**
     * This defines a new [ConflatedBroadcastChannel]. This is a special kind of coroutine-based
     * value holder that holds only the last value it was given. It's a thread-safe concurrency
     * primitive, so you can write to it from multiple threads at the same time (and whichever
     * is considered "last" will win).
     *
     * You can also subscribe to get updates to the current value. Overall, it has the similar
     * behavior to a [LiveData]–it just holds the last value and lets you observe changes to it.
     * However, unlike [LiveData], you have to use [Coroutines] to read values on multiple threads.
     *
     * A [ConflatedBroadcastChannel] is often a good way to insert [events] into a flow. It provides
     * a [concurrency] [primitive] (or low-level tool) for passing values between several coroutines.
     *
     * By conflating the events, we keep track of only the most recent event. This is often the
     * correct thing to do, since UI events may come in faster than processing, and we usually
     * don't care about intermediate values.
     *
     * If you do need to pass all events between coroutines and don't want conflation, consider
     * using a [Channel] which offers the semantics of a [BlockingQueue] using suspend functions.
     * The channelFlow builder can be used to make channel backed flows.
     */
    private val growZoneChannel = ConflatedBroadcastChannel<GrowZone>()

    /**
     * This pattern shows how to integrate events (grow zone changing) into a flow.
     * It does exactly the same thing as the LiveData.switchMap version–switching
     * between two data sources based on an event.
     *
     * One of the easiest ways to subscribe to changes in a [ConflatedBroadcastChannel]
     * is to convert it to a [Flow]. This creates a flow that, when being collected,
     * will subscribe to changes to the ConflatedBroadcastChannel and send them on the
     * flow. It does [not] add any additional [buffers], so if the flow's collector is slower
     * than writes to the growZoneChannel it'll skip over any results and only emit the
     * most recent.
     *
     * This is also nice because cancellation of the channel subscription will happen on
     * flow cancellation.
     */
    val plantsUsingFlow: LiveData<List<Plant>> = growZoneChannel.asFlow()
        // This is exactly the same as `switchMap` from LiveData. Whenever the growZoneChannel
        // changes its value, this lambda will be applied and it must return a Flow.
        // Then, the returned Flow will be used as the Flow for all downstream operators.
        // Basically, this lets us switch between different flows based on the value of growZone.
        .flatMapLatest { growZone ->
            if (growZone == NoGrowZone) {
                repository.plantsFlow
            } else {
                repository.getPlantsWithGrowZoneFlow(growZone)
            }
        }.asLiveData()

    val plantsUsingFlowDelay: LiveData<List<Plant>> = repository.plantsFlowWithDelay.asLiveData()

    init {
        // When creating a new ViewModel, clear the grow zone and perform any related udpates
        clearGrowZoneNumber()

        // This code will launch a new coroutine to observe the values sent to growZoneChannel.
        // This code drives network requests from the growZoneChannel.
        // This helps us create a single source of truth and avoid code duplication–there's
        // no way any code can change the filter without refreshing the cache.
        growZoneChannel.asFlow()
            // `mapLatest` will apply this map function for each value.
            // However, unlike regular `map`, it'll launch a new coroutine for each call
            // to the map transform. Then, if a new value is emitted by the growZoneChannel
            // before the previous coroutine completes, it'll cancel it before starting a new one.
            // We can use mapLatest to control concurrency for us. Instead of building
            // cancel/restart logic ourselves, the flow transform can take care of it.
            // This code saves a lot of code and complexity compared to writing the same
            // cancellation logic by hand.
            // If you've used RxJava, you can use mapLatest exactly like you'd use `switchMap`.
            // The key difference is that it provides a suspending lambda for you in a new
            // coroutine, so you can call regular suspend functions directly from mapLatest.
            .mapLatest { growZone ->
                _spinner.value = true

                if (growZone == NoGrowZone) {
                    repository.tryUpdateRecentPlantsCache()
                } else {
                    repository.tryUpdateRecentPlantsForGrowZoneCache(growZone)
                }
            }
            // onCompletion will be called every time the flow above it completes.
            // It's the same thing as a `finally` block – it's a good place to put any code you
            // need to execute during cleanup. Here we're resetting the spinner.
            .onCompletion { _spinner.value = false }
            // The catch operator will capture any exceptions thrown above it in the flow.
            // It can emit a new value to the flow like an error state, rethrow the exception
            // back into the flow, or perform work like we're doing here.
            .catch { throwable -> _snackBar.value = throwable.message }
            // The operator launchIn creates a new coroutine and collects every value
            // from the flow. It'll launch in the CoroutineScope provided–in this case,
            // the `viewModelScope`. This is great because it means when this ViewModel
            // gets cleared, the Flow will be cancelled.
            .launchIn(viewModelScope)

        // Using Flow, it's natural to collect data in the ViewModel, Repository, or
        // other data layers when needed.
        // Since Flow is not tied to the UI, you don't need a UI observer to collect a flow.
        // This is a big difference from LiveData which always requires a UI-observer to run.
        // It is not a good idea to try to observe a LiveData in your ViewModel because it
        // doesn't have an appropriate observation lifecycle.
    }

    /**
     * Filter the list to this grow zone.
     *
     * To let the channel know about the filter change, we can call [offer]. This is a
     * regular (non-suspending) function, and it's an easy way to communicate an event
     * into a coroutine like we're doing here.
     */
    fun setGrowZoneNumber(num: Int) {
        growZone.value = GrowZone(num)
        growZoneChannel.offer(GrowZone(num))

        //launchDataLoad { repository.tryUpdateRecentPlantsForGrowZoneCache(GrowZone(num)) }
    }

    /**
     * Clear the current filter of this plants list.
     *
     * To let the channel know about the filter change, we can call [offer]. This is a
     * regular (non-suspending) function, and it's an easy way to communicate an event
     * into a coroutine like we're doing here.
     */
    fun clearGrowZoneNumber() {
        growZone.value = NoGrowZone
        growZoneChannel.offer(NoGrowZone)

        //launchDataLoad { repository.tryUpdateRecentPlantsCache() }
    }

    /**
     * Return true iff the current list is filtered.
     */
    fun isFiltered() = growZone.value != NoGrowZone

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackBarShown() {
        _snackBar.value = null
    }

    /**
     * Helper function to call a data load function with a loading spinner; errors will trigger a
     * snackbar.
     *
     * By marking [block] as [suspend] this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling
     *              the lambda, the loading spinner will display. After completion or error, the
     *              loading spinner will stop.
     */
    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                _snackBar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }
}