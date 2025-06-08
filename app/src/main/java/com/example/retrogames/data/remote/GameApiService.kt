package com.example.retrogames.data.remote

import com.example.retrogames.data.model.RetroGame
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface GameApiService {

    // GET παιχνίδια με φίλτρα (συμβατό με Django REST)
    @GET("retro-games/")
    suspend fun getGames(
        @Query("title") title: String?,
        @Query("release_date__gte") dateFrom: String?,
        @Query("release_date__lte") dateTo: String?
    ): List<RetroGame>

    // GET λεπτομέρειες παιχνιδιού με id
    @GET("retro-games/{id}/")
    suspend fun getGameDetails(@Path("id") id: Int): RetroGame

    // POST για δημιουργία παιχνιδιού (με εικόνα)
    @Multipart
    @POST("retro-games/")
    suspend fun createGame(
        @Part("title") title: RequestBody,
        @Part("platform") platform: RequestBody,
        @Part("release_date") releaseDate: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>
}
