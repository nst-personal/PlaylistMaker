package com.example.playlistmaker.domain.interactors.settings

import com.example.playlistmaker.domain.repositories.settings.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository): SettingsInteractor {
    override fun findDarkMode(default: Boolean): Boolean {
        return settingsRepository.findDarkMode(default);
    }
    override fun updateDarkMode(value: Boolean) {
        settingsRepository.updateDarkMode(value)
    }
}