package com.example.retrogames.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.retrogames.data.remote.GameApiService

object RetrofitClient {
    private const val BASE_URL = "http://your-django-api.com/" // άλλαξε το

    val apiService: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}