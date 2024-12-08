package com.example.playlistmaker.data.repositories.track

import com.example.playlistmaker.data.dto.history.TrackResponse
import com.example.playlistmaker.data.dto.search.TracksSearchRequest
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.repositories.favorites.FavoriteTrackRepository
import com.example.playlistmaker.domain.repositories.track.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favoriteTrackRepository: FavoriteTrackRepository
) : TrackRepository {

    override fun search(search: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(search))
        if (response.isSuccessful) {
            val trackResponse = (response as TrackResponse)
            if (trackResponse.results == null || trackResponse.resultCount == 0) {
                emit(emptyList())
            } else {
                favoriteTrackRepository.favoriteTrackIds().collect { trackIds ->
                    emit(trackResponse.results?.map { item ->
                        Track(
                            item.trackId ?: 0,
                            item.trackName ?: "",
                            item.artistName ?: "",
                            item.trackTimeMillis ?: 0,
                            item.artworkUrl100 ?: "",
                            item.previewUrl ?: "",
                            item.collectionName ?: "",
                            item.releaseDate ?: "",
                            item.primaryGenreName ?: "",
                            item.country ?: "",
                            trackIds.contains(item.trackId)
                        )
                    }?.sortedByDescending { item -> item.isFavorite })
                }
            }
        } else {
            emit(null)
        }
    }
}