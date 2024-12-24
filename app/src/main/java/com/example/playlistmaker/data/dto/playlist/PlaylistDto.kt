package com.example.playlistmaker.data.dto.playlist

data class PlaylistDto(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistImageUrl: String?,
    var playlistTracks: String,
    var playlistTracksCount: Long,
    val addedTime: Long? = null
)