package com.pj.playground.network

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData

abstract class NetworkBoundResource <ResultType, RequestType> {

    // In case the api call request failed
    protected open fun onFetchFailed() {}

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall() : LiveData<ResultType>

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun asLiveData(): LiveData<ResultType> = TODO()
}