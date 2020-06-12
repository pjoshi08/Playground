package com.pj.playground.network

sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()

    sealed class Error(val errorMsg: String) : Result<Nothing>() {
        class RecoverableError(msg: String) : Error(msg)
        class SessionExpiredError(msg: String) : Error(msg)
        class NoData(msg: String) : Error(msg)
    }

    object Loading : Result<Nothing>()
}