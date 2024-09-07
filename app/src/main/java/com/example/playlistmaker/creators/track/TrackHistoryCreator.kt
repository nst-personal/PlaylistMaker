package com.example.playlistmaker.creators.track

import android.content.Context
import com.example.playlistmaker.data.repositories.track.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.interactors.track.TrackHistoryManager
import com.example.playlistmaker.domain.interactors.track.TrackHistoryManagerImpl
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository

object TrackHistoryCreator {
    private fun getTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(context)
    }

    fun provideTrackHistoryManager(context: Context): TrackHistoryManager {
        return TrackHistoryManagerImpl(getTrackHistoryRepository(context))
    }
}