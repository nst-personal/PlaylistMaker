package com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen

import com.example.playlistmaker.domain.models.Playlist

sealed class PlaylistEditScreenState: PlaylistCreateScreenState() {
    data class PlaylistContent(
        val playlist: Playlist
    ): PlaylistEditScreenState()

    data class PlaylistUpdateCompletedContent(
        val playlist: Playlist
    ): PlaylistEditScreenState()

}