package com.example.playlistmaker.domain.interactors.track

import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.favorites.FavoriteTrackRepository
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository
import kotlinx.coroutines.flow.Flow

class TrackHistoryInteractorImpl(private val trackHistoryRepository: TrackHistoryRepository,
                                 private val favoriteTrackRepository: FavoriteTrackRepository) :
    TrackHistoryInteractor {
    override fun isEmpty() : Boolean {
        return trackHistoryRepository.isEmpty()
    }
    override fun findAll(): MutableList<Track> {
        return trackHistoryRepository.findAll()
    }
    override fun remove() {
        trackHistoryRepository.remove()
    }
    override fun add(track: Track) {
        trackHistoryRepository.add(track)
    }

    override fun findLast(): Track {
        return trackHistoryRepository.findLast()
    }

    override fun updateLast(track: Track) {
        trackHistoryRepository.updateLast(track)
    }

    override fun getFavoriteIds(): Flow<List<Long>> {
        return favoriteTrackRepository.getFavoriteTrackIds()
    }
}