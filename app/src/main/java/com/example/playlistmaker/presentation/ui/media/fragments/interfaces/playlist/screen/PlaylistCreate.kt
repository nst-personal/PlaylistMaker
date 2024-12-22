package com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.screen

import android.net.Uri

data class PlaylistCreate(
    var playlistName: String? = null,
    var playlistDescription: String? = null,
    var playlistImageUrl: Uri? = null,
)