package com.example.playlistmaker.presentation.ui.search.interfaces

import com.example.playlistmaker.data.models.Track

sealed class TrackScreenState {
    data class SearchContent(
        val tracks: List<Track>?,
        val search: String
    ): TrackScreenState()
}