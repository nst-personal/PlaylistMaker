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
    single<MediaInteractor> {
        MediaInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }
}