package com.example.retrogames.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.retrogames.data.remote.GameApiService

object RetrofitClient {

    // 🔗 ΣΗΜΕΙΩΣΗ:
    // Για Emulator:      "http://127.0.0.1:8000/api/retro-games/"
    // Για φυσική συσκευή: π.χ. "http://192.168.1.5:8000/api/"

    private const val BASE_URL = "http://10.0.2.2:8000/api/retro-games/"

    val apiService: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}
