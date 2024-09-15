package com.example.playlistmaker.creators.track

import android.content.Context
import com.example.playlistmaker.data.repositories.track.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractorImpl
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository

object TrackHistoryCreator {
    private fun getTrackHistoryRepository(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(context)
    }

    fun provideTrackHistoryManager(context: Context): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getTrackHistoryRepository(context))
    }
}