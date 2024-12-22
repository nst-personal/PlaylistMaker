package com.example.playlistmaker.domain.repositories.playlist

import com.example.playlistmaker.data.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistIds(): Flow<List<Long>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun removePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
}