package com.example.playlistmaker.presentation.ui.media_player.interfaces

import com.example.playlistmaker.data.models.Track

open class TrackState {
    data class Favorite(
        val track: Track?,
        val isFavorite: Boolean
    ): TrackState()
}