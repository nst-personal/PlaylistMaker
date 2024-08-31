package com.example.playlistmaker.domain.managers

import com.example.playlistmaker.data.entities.Track
import com.example.playlistmaker.domain.repositories.TrackHistoryRepository

class TrackHistoryManagerImpl(private val trackHistoryRepository: TrackHistoryRepository) :
    TrackHistoryManager {
    override fun isEmpty() : Boolean {
        return trackHistoryRepository.isEmpty()
    }
    override fun findAll(): MutableList<Track> {
        return trackHistoryRepository.findAll()
    }
    override fun remove() {
        trackHistoryRepository.remove()
    }
    override fun add(track: Track) {
        trackHistoryRepository.add(track)
    }
}