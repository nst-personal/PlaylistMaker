package com.example.playlistmaker.domain.interactors.favorites

import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.favorites.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val historyRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {

    override fun favoriteTracks(): Flow<List<Track>> {
        return historyRepository.favoriteTracks()
    }

    override fun favoriteTrackIds(): Flow<List<Long>> {
        return historyRepository.favoriteTrackIds()
    }

    override suspend fun addTrack(track: Track) {
        historyRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        historyRepository.removeTrack(track)
    }
}