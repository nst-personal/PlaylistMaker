package com.example.playlistmaker.domain.managers.track

import com.example.playlistmaker.data.models.Track

interface TrackHistoryManager {
    fun isEmpty() : Boolean
    fun findAll(): MutableList<Track>
    fun remove()
    fun add(track: Track)
    fun findLast() : Track
    fun updateLast(track: Track)
}