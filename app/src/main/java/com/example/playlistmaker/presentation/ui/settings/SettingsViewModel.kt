package com.example.playlistmaker.presentation.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactors.settings.SettingsInteractor

class SettingsViewModel(
    private val settingsManager: SettingsInteractor
): ViewModel() {

    fun updateTheme(checked: Boolean) {
        settingsManager.updateDarkMode(checked)
        AppCompatDelegate.setDefaultNightMode(
            if (checked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}