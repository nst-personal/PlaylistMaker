package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.ui.media.MediaListViewModel
import com.example.playlistmaker.presentation.ui.media_player.MediaViewModel
import com.example.playlistmaker.presentation.ui.search.SearchViewModel
import com.example.playlistmaker.presentation.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MediaViewModel(get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        MediaListViewModel(get())
    }
}