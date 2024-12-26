package com.example.playlistmaker.domain.interactors.playlist

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistIds(): Flow<List<Long>>
    suspend fun addPlaylist(playlistName: String,
                            playlistDescription: String?,
                            playlistUrl: String?)
    suspend fun removePlaylist(track: Playlist)
    suspend fun updatePlaylist(track: Track, playlist: Playlist) : Boolean
}