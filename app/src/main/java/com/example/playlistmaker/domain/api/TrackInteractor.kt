package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(expression: String): Flow<List<Track>?>
}