package com.example.playlistmaker.domain.creators

import android.content.Context
import com.example.playlistmaker.data.repositories.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.managers.TrackHistoryManager
import com.example.playlistmaker.domain.managers.TrackHistoryManagerImpl
import com.example.playlistmaker.domain.repositories.TrackHistoryRepository

class TrackHistoryCreator {
    private fun getTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(context)
    }

    fun provideTrackHistoryManager(context: Context): TrackHistoryManager {
        return TrackHistoryManagerImpl(getTrackHistoryRepository(context))
    }
}