package com.example.playlistmaker.interfaces

import com.example.playlistmaker.entities.Track

interface OnTrackItemClickListener {
    fun onItemClick(track: Track)
}