package com.example.playlistmaker.data.models

data class Track(
    val trackName: String,
    val trackId: Long,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val previewUrl: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)