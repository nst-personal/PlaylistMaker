package com.example.playlistmaker.domain.repositories.favorites

import com.example.playlistmaker.data.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {
    fun favoriteTracks(): Flow<List<Track>>
    fun favoriteTrackIds(): Flow<List<Long>>
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
}