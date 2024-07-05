package com.example.playlistmaker.services

import com.example.playlistmaker.models.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {
    @GET("/search")
    fun search(@Query("term") text: String): Call<TrackResponse>
}