package com.example.playlistmaker.domain.managers.settings

import com.example.playlistmaker.domain.repositories.settings.SettingsRepository

class SettingsManagerImpl(private val settingsRepository: SettingsRepository): SettingsManager {
    override fun findDarkMode(default: Boolean): Boolean {
        return settingsRepository.findDarkMode(default);
    }
    override fun updateDarkMode(value: Boolean) {
        settingsRepository.updateDarkMode(value)
    }
}