package com.example.playlistmaker.presentation.ui.media.fragments.interfaces

import com.example.playlistmaker.data.models.Track

sealed class FavoriteTrackScreenState {
    data class FavoriteContent(
        val tracks: List<Track>?,
    ): FavoriteTrackScreenState()

}