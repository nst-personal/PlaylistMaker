package com.example.playlistmaker.domain.interactors.favorites

import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.favorites.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val historyRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return historyRepository.getFavoriteTracks()
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return historyRepository.getFavoriteTrackIds()
    }

    override suspend fun addTrack(track: Track) {
        historyRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        historyRepository.removeTrack(track)
    }
}