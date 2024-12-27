package com.example.playlistmaker.domain.repositories.track

import com.example.playlistmaker.domain.models.Track

interface TrackHistoryRepository {
    fun isEmpty() : Boolean
    fun findAll(): MutableList<Track>
    fun remove()
    fun add(track: Track)

    fun findLast() : Track

    fun updateLast(track: Track)
}