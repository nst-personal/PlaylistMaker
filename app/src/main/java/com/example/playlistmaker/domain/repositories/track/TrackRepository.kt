package com.example.playlistmaker.domain.repositories.track

import com.example.playlistmaker.data.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun search(search: String): Flow<List<Track>?>
}