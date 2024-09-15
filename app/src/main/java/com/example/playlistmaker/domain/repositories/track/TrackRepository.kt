package com.example.playlistmaker.domain.repositories.track

import com.example.playlistmaker.data.models.Track

interface TrackRepository {
    fun search(search: String): List<Track>?
}