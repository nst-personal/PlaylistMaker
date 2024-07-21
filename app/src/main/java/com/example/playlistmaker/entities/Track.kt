package com.example.playlistmaker.entities

data class Track(
    val trackName: String,
    val trackId: Long,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)