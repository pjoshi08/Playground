package com.pj.playground.data

import com.pj.playground.util.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Network interface which will fetch a new welcome title for us
 */
interface Network {
    @GET("next_title.json")
    fun fetchNextTitle(): Call<String>
}

fun getNetworkService() = service

private val service: Network by lazy {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(Network::class.java)
}