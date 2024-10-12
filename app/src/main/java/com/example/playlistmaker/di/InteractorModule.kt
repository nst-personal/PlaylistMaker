package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackInteractorImpl
import com.example.playlistmaker.domain.interactors.media.MediaInteractor
import com.example.playlistmaker.domain.interactors.media.MediaInteractorImpl
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
        TrackHistoryInteractorImpl(get())
    }

    factory<TrackInteractor> {
        TrackInteractorImpl(get())
    }
}