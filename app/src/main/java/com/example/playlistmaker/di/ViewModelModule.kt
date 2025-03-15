package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.ui.media.MediaListViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.FavoriteViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistCreateViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistDetailsViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistEditViewModel
import com.example.playlistmaker.presentation.ui.media.fragments.PlaylistListViewModel
import com.example.playlistmaker.presentation.ui.media_player.MediaViewModel
import com.example.playlistmaker.presentation.ui.search.SearchViewModel
import com.example.playlistmaker.presentation.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MediaViewModel(get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        MediaListViewModel()
    }

    viewModel {
        FavoriteViewModel(get(), get())
    }

    viewModel {
        PlaylistCreateViewModel(get())
    }

    viewModel {
        PlaylistListViewModel(get())
    }

    viewModel {
        PlaylistDetailsViewModel(get(), get())
    }

    viewModel {
        PlaylistEditViewModel(get())
    }

}