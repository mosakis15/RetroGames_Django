package com.example.retrogames.util

import com.example.retrogames.data.remote.GameApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ΣΗΜΕΙΩΣΗ:
    // - For Emulator: "http://10.0.2.2:8000/api/retro-games/"
    // - For Physical Device: e.g., "http://192.168.1.5:8000/api/retro-games/"
    // - Ensure the server is running and the path is correct per API documentation

    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val apiService: GameApiService by lazy {
        // Set up logging interceptor for debugging
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY) // Log request/response body
        }

        // Configure OkHttpClient with logging and timeouts
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Adjust as needed
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Build Retrofit instance
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}