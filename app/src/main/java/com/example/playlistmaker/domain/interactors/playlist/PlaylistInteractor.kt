package com.example.playlistmaker.domain.interactors.playlist

import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.data.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistIds(): Flow<List<Long>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun removePlaylist(track: Playlist)
    suspend fun updatePlaylist(track: Track, playlist: Playlist) : Boolean
}