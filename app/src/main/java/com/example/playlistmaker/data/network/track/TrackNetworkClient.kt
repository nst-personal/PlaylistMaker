package com.example.playlistmaker.data.network.track

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.search.TracksSearchRequest
import com.example.playlistmaker.data.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackNetworkClient(private val trackClient: TrackClient) : NetworkClient {

    suspend override fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            return withContext(Dispatchers.IO) {
                try {
                    val response = trackClient.search(dto.expression)
                    response.apply { isSuccessful = true }
                } catch (e: Throwable) {
                    Response().apply { isSuccessful = false }
                }
            }
        } else {
            Response().apply { isSuccessful = false }
        }
    }
}