package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.track.TrackRepository
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>?> {
        return repository.search(expression)
    }
} 