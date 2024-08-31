package com.example.playlistmaker.domain.repositories

import com.example.playlistmaker.data.entities.Track

interface TrackHistoryRepository {
    fun isEmpty() : Boolean
    fun findAll(): MutableList<Track>
    fun remove()
    fun add(track: Track)
}