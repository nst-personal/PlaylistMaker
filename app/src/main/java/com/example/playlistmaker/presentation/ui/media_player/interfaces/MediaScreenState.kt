package com.example.playlistmaker.presentation.ui.media_player.interfaces

import com.example.playlistmaker.domain.models.Track

sealed class MediaScreenState {
    data class Ready(
        val track: Track?,
    ): MediaScreenState()

    data class Completed(
        val track: Track?,
    ): MediaScreenState()

    data class State(
        val state: MediaState,
    ): MediaScreenState()

}