package com.example.playlistmaker.domain.interactors.track

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackHistoryInteractor {
    fun isEmpty() : Boolean
    fun findAll(): MutableList<Track>
    fun remove()
    fun add(track: Track)
    fun findLast() : Track
    fun updateLast(track: Track)
    fun getFavoriteIds() : Flow<List<Long>>
}