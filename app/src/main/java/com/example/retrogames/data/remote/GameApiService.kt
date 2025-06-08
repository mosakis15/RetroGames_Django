package com.example.retrogames.data.remote

import com.example.retrogames.data.model.RetroGame
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GameApiService {

    @GET("api/games/")
    suspend fun getGames(
        @Query("title") title: String?,
        @Query("date_from") dateFrom: String?,
        @Query("date_to") dateTo: String?
    ): List<RetroGame>

    @GET("api/games/{id}/")
    suspend fun getGameDetails(@Path("id") id: Int): RetroGame

    @Multipart
    @POST("api/games/")
    suspend fun createGame(
        @Part("title") title: RequestBody,
        @Part("platform") platform: RequestBody,
        @Part("release_date") releaseDate: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>
}