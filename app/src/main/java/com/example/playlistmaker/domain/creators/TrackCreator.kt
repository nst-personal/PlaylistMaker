package com.example.playlistmaker.domain.creators

import com.example.playlistmaker.data.repositories.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackInteractorImpl
import com.example.playlistmaker.domain.network.track.TrackNetworkClient
import com.example.playlistmaker.domain.repositories.TrackRepository

class TrackCreator {
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(TrackNetworkClient())
    }

    fun provideTracksInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }
}