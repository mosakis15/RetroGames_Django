package com.example.retrogames.data.model

import com.google.gson.annotations.SerializedName

data class RetroGame(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("release_date") val release_date: String,
    @SerializedName("cover_image") val cover_image: String?, // Matches 'image' in createGame
    @SerializedName("youtube_video") val youtube_video: String?
)