package com.example.playlistmaker.domain.repositories.playlist

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistIds(): Flow<List<Long>>
    suspend fun addPlaylist(
        playlistName: String,
        playlistDescription: String?,
        playlistUrl: String?)
    suspend fun removePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(track: Track, playlist: Playlist) : Boolean
    suspend fun deletePlaylistTrack(track: Track, playlist: Playlist) : Boolean
    suspend fun getPlaylistById(id: Long) : Flow<Playlist>
}