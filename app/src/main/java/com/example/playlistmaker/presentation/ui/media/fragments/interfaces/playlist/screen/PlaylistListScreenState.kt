package com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

sealed class PlaylistListScreenState {
    data class PlaylistListContent(
        val playlists: List<Playlist>
    ): PlaylistListScreenState()

    data class PlaylistListUpdatedContent(
        val track: Track,
        val playlist: Playlist
    ): PlaylistListScreenState()

    data class PlaylistListNotUpdatedContent(
        val track: Track,
        val playlist: Playlist,
    ): PlaylistListScreenState()
}