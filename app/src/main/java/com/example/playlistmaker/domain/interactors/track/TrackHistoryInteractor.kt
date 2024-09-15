package com.example.playlistmaker.domain.interactors.track

import com.example.playlistmaker.data.models.Track

interface TrackHistoryInteractor {
    fun isEmpty() : Boolean
    fun findAll(): MutableList<Track>
    fun remove()
    fun add(track: Track)
    fun findLast() : Track
    fun updateLast(track: Track)
}