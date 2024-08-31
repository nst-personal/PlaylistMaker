package com.example.playlistmaker.domain.repositories

import com.example.playlistmaker.data.entities.Track

interface TrackRepository {
    fun search(search: String): List<Track>?
}