package com.example.playlistmaker.domain.models

data class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistImageUrl: String?,
    var playlistTracks: String,
    var playlistTracksCount: Long,
    val addedTime: Long? = null,
    var tracks: List<Track>? = null,
    var tracksDuration: Long? = null,
    var tracksCount: Long? = null,
)