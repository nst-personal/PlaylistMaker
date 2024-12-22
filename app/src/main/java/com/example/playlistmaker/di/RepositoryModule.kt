package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.db.convertors.TrackDbConvertor
import com.example.playlistmaker.data.repositories.favorites.FavoriteTrackRepositoryImpl
import com.example.playlistmaker.data.repositories.media.MediaListRepositoryImpl
import com.example.playlistmaker.data.repositories.media.MediaRepositoryImpl
import com.example.playlistmaker.data.repositories.playlist.PlaylistRepositoryImpl
import com.example.playlistmaker.data.repositories.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.repositories.track.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.repositories.track.TrackRepositoryImpl
import com.example.playlistmaker.domain.repositories.favorites.FavoriteTrackRepository
import com.example.playlistmaker.domain.repositories.media.MediaListRepository
import com.example.playlistmaker.domain.repositories.media.MediaRepository
import com.example.playlistmaker.domain.repositories.playlist.PlaylistRepository
import com.example.playlistmaker.domain.repositories.settings.SettingsRepository
import com.example.playlistmaker.domain.repositories.track.TrackHistoryRepository
import com.example.playlistmaker.domain.repositories.track.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<MediaRepository> {
        MediaRepositoryImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(get(), get())
    }

    factory<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    factory<MediaListRepository> {
        MediaListRepositoryImpl()
    }

    factory { TrackDbConvertor() }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConvertor() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
}