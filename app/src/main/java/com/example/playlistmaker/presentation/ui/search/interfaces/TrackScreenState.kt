package com.example.playlistmaker.presentation.ui.search.interfaces

import com.example.playlistmaker.domain.models.Track

sealed class TrackScreenState {
    data class SearchContent(
        val tracks: List<Track>?,
        val search: String
    ): TrackScreenState()

    data class HistoryContent(
        val tracks: List<Track>,
    ): TrackScreenState()
}