package com.example.playlistmaker.presentation.ui.media_player.interfaces

import com.example.playlistmaker.data.models.Playlist

interface OnPlaylistItemClickListener {
    fun onItemClick(playlist: Playlist)
}