package com.example.playlistmaker.data.dto.search
data class TrackDto(
    val trackName: String,
    val trackId: Long,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val previewUrl: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val isFavorite: Boolean = false,
    val addedTime: Long? = null
)