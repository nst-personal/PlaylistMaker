package com.example.playlistmaker.domain.interactors.favorites

import com.example.playlistmaker.data.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {
    fun favoriteTracks(): Flow<List<Track>>
    fun favoriteTrackIds(): Flow<List<Long>>
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
}