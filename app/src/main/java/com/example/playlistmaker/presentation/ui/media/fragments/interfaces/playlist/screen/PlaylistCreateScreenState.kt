package com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen

import android.net.Uri

sealed class PlaylistCreateScreenState {
    data class PlaylistCreateCreateNameContent(
        val name: String
    ): PlaylistCreateScreenState()

    data class PlaylistCreateCreateDescriptionContent(
        val description: String
    ): PlaylistCreateScreenState()

    data class PlaylistCreateCreatePhotoUrlContent(
        val photoUrl: Uri
    ): PlaylistCreateScreenState()
}