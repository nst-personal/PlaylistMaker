package com.example.playlistmaker.presentation.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creators.settings.SettingsCreator

class SettingsViewModel(
    context: Context,
): ViewModel() {
    private val settingsManager = SettingsCreator.provideSettingsManager(context)

    fun updateTheme(checked: Boolean) {
        settingsManager.updateDarkMode(checked)
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    context
                )
            }
        }
    }

}