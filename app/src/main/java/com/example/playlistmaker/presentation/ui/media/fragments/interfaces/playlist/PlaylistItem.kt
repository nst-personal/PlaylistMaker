package com.example.playlistmaker.presentation.ui.media.fragments.interfaces.playlist

import java.io.File

data class PlaylistItem(val id: Long, val title: String, val trackCount: Long, val file: File?)