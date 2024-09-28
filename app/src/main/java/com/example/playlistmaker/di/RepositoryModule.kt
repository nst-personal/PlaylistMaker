package com.example.playlistmaker.di

import com.example.playlistmaker.data.repositories.media.MediaRepositoryImpl
import com.example.playlistmaker.data.repositories.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.repositories.track.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.repositories.track.TrackRepositoryImpl
import com.example.playlistmaker.domain.repositories.media.MediaRepository
import com.example.playlistmaker.domain.repositories.settings.SettingsRepository
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository
import com.example.playlistmaker.domain.repositories.track.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MediaRepository> {
        MediaRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(get(), get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }
}