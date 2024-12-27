package com.example.playlistmaker.presentation.ui.search.interfaces

import com.example.playlistmaker.domain.models.Track

interface OnTrackItemClickListener {
    fun onItemClick(track: Track)
}