package com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen

import com.example.playlistmaker.domain.models.Playlist

sealed class PlaylistDetailsScreenState {
    data class PlaylistContent(
        val playlist: Playlist
    ): PlaylistDetailsScreenState()

}