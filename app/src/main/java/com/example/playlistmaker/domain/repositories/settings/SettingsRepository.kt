package com.example.playlistmaker.domain.repositories.settings

interface SettingsRepository {
    fun findDarkMode(default: Boolean) : Boolean
    fun updateDarkMode(value: Boolean)
}