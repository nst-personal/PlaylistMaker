package com.example.playlistmaker.presentation.ui.media_player.interfaces

import com.example.playlistmaker.domain.models.Track

interface OnPlaylistTrackItemClickListener {
    fun onItemClick(playlistTrack: Track)
    fun onItemLongClick(playlistTrack: Track)
}