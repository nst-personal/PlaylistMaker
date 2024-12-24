package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackInteractorImpl
import com.example.playlistmaker.domain.interactors.favorites.FavoriteTrackInteractor
import com.example.playlistmaker.domain.interactors.favorites.FavoriteTrackInteractorImpl
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.domain.interactors.media.MediaInteractorImpl
import com.example.playlistmaker.domain.interactors.media.MediaListInteractor
import com.example.playlistmaker.domain.interactors.media.MediaListInteractorImpl
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.playlist.PlaylistInteractorImpl
import com.example.playlistmaker.domain.interactors.settings.SettingsInteractor
import com.example.playlistmaker.domain.interactors.settings.SettingsInteractorImpl
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractor
import com.example.playlistmaker.domain.interactors.track.TrackHistoryInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<MediaInteractor> {
        MediaInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(get(), get())
    }

    factory<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    factory<MediaListInteractor> {
        MediaListInteractorImpl(get())
    }

    factory<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}