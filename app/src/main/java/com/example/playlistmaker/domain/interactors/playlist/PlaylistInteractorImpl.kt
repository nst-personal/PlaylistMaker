package com.example.playlistmaker.domain.interactors.playlist

import com.example.playlistmaker.data.models.Playlist
import com.example.playlistmaker.data.models.Track
import com.example.playlistmaker.domain.repositories.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistIds(): Flow<List<Long>> {
        return playlistRepository.getPlaylistIds()
    }



    override suspend fun addPlaylist(playlistName: String,
                                     playlistDescription: String?,
                                     playlistUrl: String?) {
        playlistRepository.addPlaylist(playlistName,
            playlistDescription,
            playlistUrl)
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistRepository.removePlaylist(playlist)
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistRepository.updatePlaylist(track, playlist)
    }
}