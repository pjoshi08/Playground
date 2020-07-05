package com.pj.playground.data

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.sunflower.GrowZone
import com.example.sunflower.Plant
import com.example.sunflower.util.CacheOnSuccess
import com.pj.playground.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations.
 *
 * This PlantRepository exposes two UI-observable database queries [plantsFlow] and
 * [getPlantsWithGrowZoneFlow].
 *
 * To update the plants cache, call [tryUpdateRecentPlantsForGrowZoneCache] or
 * [tryUpdateRecentPlantsCache].
 */
class PlantRepository private constructor(
    private val dao: PlantDao,
    private val service: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    /**
     * Fetch a list of [Plant]s from the database.
     * Returns a LiveData-wrapped List of Plants.
     */
    val plants: LiveData<List<Plant>> = liveData {
        val plantsLiveData = dao.getPlants()
        val customSortOrder = plantsListSortOrderCache.getOrAwait()
        emitSource(plantsLiveData.map { plantList ->
            plantList.applySort(customSortOrder)
        })
    }

    /**
     * The list of plants were displayed in alphabetical order, but we want to
     * change the order of this list by listing certain plants first, and then the rest in
     * alphabetical order.
     */
    private var plantsListSortOrderCache =
        CacheOnSuccess(onErrorFallback = { listOf<String>() }) {
            service.customPlantSortOrder()
        }

    /**
     * The transform onStart will happen when an observer listens before other operators,
     * and it can emit placeholder values. So here we're emitting an empty list, delaying
     * calling getOrAwait by 1500ms, then continuing the original flow. If you run the app
     * now, you'll see that the Room database query returns right away, combining with the
     * empty list (which means it'll sort alphabetically). Then around 1500ms later, it
     * applies the custom sort.
     *
     * You can use onStart to run suspending code before a flow runs. It can even emit extra
     * values into the flow, so you could use it to emit a Loading state on a network request
     * flow.
     */
    private val customSortFlowWithOnStart =
        suspend { plantsListSortOrderCache.getOrAwait() }.asFlow()
            .onStart {
                emit(listOf())
                delay(1500)
            }

    // Same as below
    private val customSortFlow = flow { emit(plantsListSortOrderCache.getOrAwait()) }

    /**
     * Create a flow that calls a single function.
     * This Flow calls getOrAwait and emits the result as its first and only value.
     * It does this by referencing the getOrAwait method using :: and calling asFlow on the
     * resulting Function object.
     * Usage: Experimental for now
     */
    private val customSortFlowExperimental = plantsListSortOrderCache::getOrAwait.asFlow()

    /**
     * The [combine] operator combines two flows together. Both flows will run in their `own
     * coroutine`, then whenever either flow produces a new value the transformation will be
     * called with the latest value from either flow.
     *
     * By using [combine], we can combine the cached network lookup with our database query.
     * Both of them will run on different coroutines [concurrently]. That means that while
     * Room starts the network request, Retrofit can start the network query. Then, as
     * soon as a result is available for both flows, it will call the combine lambda where
     * we apply the loaded sort order to the loaded plants.
     *
     * The transformation [combine] will launch one coroutine for each flow being combined.
     * This lets you combine two flows concurrently.
     * It will combine the flows in a "fair" manner, which means that they'll all get a
     * chance to produce a value (even if one of them is produced by a tight loop).
     *
     * By default, this flow uses the following [threads]:
     * [plantService.customPlantSortOrder] runs on a Retrofit thread (it calls [Call.enqueue])
     * [getPlantsFlow] will run queries on a Room [Executor]
     * [applySort] will run on the collecting dispatcher (in this case [Dispatchers.Main])
     *
     * Calling [flowOn] has below important effects on how the code executes:
     * 1. Launch a new coroutine on the defaultDispatcher (in this case, [Dispatchers.Default])
     *    to run and collect the flow before the call to flowOn.
     * 2. Introduces a [buffer] to send results from the new coroutine to later calls.
     * 3. Emit the values from that buffer into the Flow after flowOn. In this case,
     *    that's asLiveData in the ViewModel.
     *
     * This is very similar to how [withContext] works to switch dispatchers, but it does
     * introduce a [buffer] in the middle of our transforms that changes how the flow works.
     * The coroutine launched by [flowOn] is allowed to produce results faster than the caller
     * consumes them, and it will buffer a large number of them by default.
     */
    val plantsFlow: Flow<List<Plant>>
        get() = dao.getPlantsFlow()
            // When the result of customSortFlow is available,
            // this will combine it with the latest value from
            // the flow above.  Thus, as long as both `plants`
            // and `sortOrder` have an initial value (their
            // flow has emitted at least one value), any change
            // to either `plants` or `sortOrder`  will call
            // `plants.applySort(sortOrder)`.
            .combine(customSortFlowExperimental) { plants, sortOrder ->
                plants.applySort(sortOrder)
            }
            // So if all we were doing was calling suspend functions in Retrofit and and
            // using Room flows, we wouldn't need to complicate this code with main-safety
            // concerns.
            // However, as our data set grows in size, the call to applySort may become slow
            // enough to block the main thread. Flow offers a declarative API called flowOn
            // to control which thread the flow runs on.
            .flowOn(defaultDispatcher)
            // In this case, we plan on sending the results to the UI, so we would only ever
            // care about the most recent result. That's what the conflate operator does–it
            // modifies the buffer of flowOn to store only the last result. If another result
            // comes in before the previous one is read, it gets overwritten.
            .conflate()

    val plantsFlowWithDelay: Flow<List<Plant>>
        get() = dao.getPlantsFlow()
            .combine(customSortFlowWithOnStart) { plants, sortOrder ->
                plants.applySort(sortOrder)
            }

    /**
     * Fetch a list of [Plant]s from the database that matches a given [GrowZone].
     * Returns a LiveData-wrapped List of Plants.
     *
     * Change: Compared to the previous version, once the custom sort order is received
     * from the network, it can then be used with the new main-safe applyMainSafeSort.
     * This result is then emitted to the switchMap as the new value returned by
     * getPlantsWithGrowZone.
     * This is done to implement a suspending transform as each value is processed, learning
     * how to build complex async transforms in LiveData
     */
    fun getPlantsWithGrowZone(growZone: GrowZone): LiveData<List<Plant>> =
        dao.getPlantsWithGrowZoneNumber(growZone.number)
            .switchMap { plantList ->
                liveData {
                    val customSortOrder = plantsListSortOrderCache.getOrAwait()
                    emit(plantList.applyMainSafeSort(customSortOrder))
                }
            }

    /**
     * By relying on regular suspend functions to handle the async work, this map operation
     * is main-safe even though it combines two async operations.
     *
     * As each result from the database is returned, we'll get the cached sort order–and
     * if it's not ready yet, it will wait on the async network request. Then once we have
     * the sort order, it's safe to call applyMainSafeSort, which will run the sort on the
     * default dispatcher.
     *
     * This code is now entirely main-safe by deferring the main safety concerns to regular
     * suspend functions. It's quite a bit simpler than the same transformation implemented
     * in [plantsFlow].
     *
     * It is an [error] to emit a value from a different coroutine than the one that called
     * the suspending transformation.
     * If you do launch another coroutine inside a flow operation like we're doing here inside
     * [getOrAwait] and [applyMainSafeSort], make sure the value is returned to the original
     * coroutine before emitting it.
     *
     * However, it is worth noting that it will execute a bit differently. The cached value
     * will be fetched every single time the database emits a new value. This is OK because
     * we're caching it correctly in [plantsListSortOrderCache], but if that started a new
     * network request this implementation would make a lot of unnecessary network requests.
     * In addition, in the [combine] version, the network request and the database query run
     * [concurrently], while in this version they run in [sequence].
     *
     * Due to these differences, there is not a clear rule to structure this code.
     * In many cases, it's fine to use suspending transformations like we're doing here,
     * which makes all async operations sequential. However, in other cases, it's better
     * to use operators to control concurrency and provide main-safety.
     */
    fun getPlantsWithGrowZoneFlow(growZone: GrowZone): Flow<List<Plant>> {
        return dao.getPlantsWithGrowZoneNumberFlow(growZone.number)
            .map { plantsList ->
                val sortedOrderFromNetwork = plantsListSortOrderCache.getOrAwait()
                val nextValue = plantsList.applyMainSafeSort(sortedOrderFromNetwork)
                nextValue
            }
    }

    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
        withContext(defaultDispatcher) {
            this@applyMainSafeSort.applySort(customSortOrder)
        }

    private fun List<Plant>.applySort(customSortOrder: List<String>): List<Plant> {
        return sortedBy { plant ->
            val positionForItem = customSortOrder.indexOf(plant.plantId).let { order ->
                if (order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, plant.name)
        }
    }

    /**
     * Returns true if we should make a network request.
     */
    private fun shouldUpdatePlantsCache(): Boolean {
        // suspending function, so you can e.g. check the status of the database here
        return true
    }

    /**
     * Update the plants cache.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    /**
     * Update the plants cache for a specific grow zone.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZone: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZone)
    }

    /**
     * Fetch a new list of plants from the network, and append them to [plantDao]
     */
    private suspend fun fetchRecentPlants() {
        val plants = service.allPlants()
        dao.insertAll(plants)
    }

    /**
     * Fetch a list of plants for a grow zone from the network, and append them to [plantDao]
     */
    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = service.plantsByGrowZone(growZone)
        dao.insertAll(plants)
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: PlantRepository? = null

        fun getInstance(plantDao: PlantDao, plantService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao, plantService).also { instance = it }
            }
    }
}