package com.example.playlistmaker.domain.managers

import com.example.playlistmaker.data.entities.Track

interface TrackHistoryManager {
    fun isEmpty() : Boolean
    fun findAll(): MutableList<Track>
    fun remove()
    fun add(track: Track)
}