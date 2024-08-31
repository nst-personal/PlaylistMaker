package com.example.playlistmaker.data.repositories.track

import com.example.playlistmaker.data.dto.history.TrackResponse
import com.example.playlistmaker.data.dto.search.TracksSearchRequest
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.network.NetworkClient
import com.example.playlistmaker.domain.repositories.track.TrackRepository

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun search(search: String): List<Track>? {
       val response = networkClient.doRequest(TracksSearchRequest(search))
        if (response.resultCode == 200) {
            val trackResponse = (response as TrackResponse)

            if (trackResponse.resultCount.equals("0") || trackResponse.results == null) {
                return emptyList()
            }

            return trackResponse.results.map { item ->
                Track(
                   item.trackName ?: "",
                   item.trackId ?: 0,
                   item.artistName ?: "",
                    item.trackTimeMillis ?: 0,
                   item.artworkUrl100 ?: "",
                   item.previewUrl ?: "",
                   item.collectionName ?: "",
                   item.releaseDate ?: "",
                   item.primaryGenreName ?: "",
                   item.country ?: ""
                )
            }
        } else {
            return null
        }
    }
}