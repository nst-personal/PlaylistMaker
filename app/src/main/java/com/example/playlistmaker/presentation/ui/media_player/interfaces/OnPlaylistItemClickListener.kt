package com.example.playlistmaker.presentation.ui.media_player.interfaces

import com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist.PlaylistItem

interface OnPlaylistItemClickListener {
    fun onItemClick(playlist: PlaylistItem)
}