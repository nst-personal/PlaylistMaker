package com.example.playlistmaker.presentation.ui.search.interfaces

import com.example.playlistmaker.data.entities.Track

interface OnTrackItemClickListener {
    fun onItemClick(track: Track)
}