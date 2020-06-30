package com.pj.playground.utils

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pj.playground.view.PlantListViewModel
import com.pj.playground.data.AppDatabase
import com.pj.playground.data.NetworkService
import com.pj.playground.data.PlantRepository

val Injector: ViewModelProviderFactory
    get() = currentInjector

interface ViewModelProviderFactory {
    fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory
}

class PlantListViewModelFactory(
    private val repository: PlantRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = PlantListViewModel(
        repository
    ) as T
}

private object DefaultViewModelProvider : ViewModelProviderFactory {

    private fun getPlantRepository(context: Context): PlantRepository {
        return PlantRepository.getInstance(
            plantDao(context),
            plantService()
        )
    }

    private fun plantService() = NetworkService()

    private fun plantDao(context: Context) = AppDatabase.getInstance(context).plantDao()

    override fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory {
        val repository = getPlantRepository(context)
        return PlantListViewModelFactory(repository)
    }
}

private object Lock

@Volatile
private var currentInjector: ViewModelProviderFactory = DefaultViewModelProvider

@VisibleForTesting
private fun setInjectorForTesting(injector: ViewModelProviderFactory?) {
    synchronized(Lock) {
        currentInjector = injector ?: DefaultViewModelProvider
    }
}

@VisibleForTesting
private fun resetInjector() = setInjectorForTesting(null)