package com.example.playlistmaker.data.network.track

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.search.TracksSearchRequest
import com.example.playlistmaker.data.network.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackNetworkClient : NetworkClient {
    private val translateBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(translateBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackClient = retrofit.create(TrackClient::class.java)

    override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            try {
                val resp = trackClient.search(dto.expression).execute()
                val response = resp.body() ?: Response()
                response.apply { isSuccessful = resp.isSuccessful }
            } catch (ex: Exception) {
                Response().apply { isSuccessful = false }
            }
        } else {
            Response().apply { isSuccessful = false }
        }
    }
}