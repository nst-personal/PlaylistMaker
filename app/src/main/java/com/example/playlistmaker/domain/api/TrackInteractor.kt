package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.entities.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}