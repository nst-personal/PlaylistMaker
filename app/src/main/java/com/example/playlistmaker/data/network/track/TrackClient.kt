package com.example.playlistmaker.data.network.track

import com.example.playlistmaker.data.dto.history.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackClient {
    @GET("/search")
    fun search(@Query("term") text: String): Call<TrackResponse>
}