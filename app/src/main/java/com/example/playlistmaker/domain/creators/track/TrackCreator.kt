package com.example.playlistmaker.domain.creators.track

import com.example.playlistmaker.data.repositories.track.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackInteractorImpl
import com.example.playlistmaker.domain.network.track.TrackNetworkClient
import com.example.playlistmaker.domain.repositories.track.TrackRepository

class TrackCreator {
    private fun getTracksRepository(): TrackRepository {
        return TrackRepositoryImpl(TrackNetworkClient())
    }

    fun provideTracksInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTracksRepository())
    }
}