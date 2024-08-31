package com.example.playlistmaker.domain.creators.track

import android.content.Context
import com.example.playlistmaker.data.repositories.track.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.managers.track.TrackHistoryManager
import com.example.playlistmaker.domain.managers.track.TrackHistoryManagerImpl
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository

class TrackHistoryCreator {
    private fun getTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(context)
    }

    fun provideTrackHistoryManager(context: Context): TrackHistoryManager {
        return TrackHistoryManagerImpl(getTrackHistoryRepository(context))
    }
}