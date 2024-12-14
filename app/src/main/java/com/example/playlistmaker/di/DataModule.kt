package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.track.TrackClient
import com.example.playlistmaker.data.network.track.TrackNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<TrackClient> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackClient::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(ShareablePreferencesConfig.DARK_THEME, Context.MODE_PRIVATE)
    }

    single {
        androidContext()
            .getSharedPreferences(ShareablePreferencesConfig.CURRENT_MEDIA, Context.MODE_PRIVATE)
    }

    single {
        androidContext()
            .getSharedPreferences(ShareablePreferencesConfig.HISTORY_LIST, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<NetworkClient> {
        TrackNetworkClient(get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}