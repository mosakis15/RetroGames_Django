package com.example.retrogames.data.remote

import com.example.retrogames.data.model.RetroGame
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GameApiService {
    // GET games with filters (matches Django's retro-games/)
    @GET("retro-games/")
    suspend fun getGames(
        @Query("title") title: String?,
        @Query("release_date__gte") dateFrom: String?,
        @Query("release_date__lte") dateTo: String?
    ): List<RetroGame>

    // GET game details by ID (matches Django's retro-games/<int:pk>/)
    @GET("retro-games/{id}/")
    suspend fun getGameDetails(@Path("id") id: Int): RetroGame

    // POST to create a game with image (matches Django's retro-games/)
    @Multipart
    @POST("retro-games/")
    suspend fun createGame(
        @Part("title") title: RequestBody,
        @Part("platform") platform: RequestBody,
        @Part("release_date") releaseDate: RequestBody,
        @Part cover_image: MultipartBody.Part
    ): Response<Unit>
}