package com.example.playlistmaker.domain.managers.settings

interface SettingsManager {
    fun findDarkMode(default: Boolean): Boolean
    fun updateDarkMode(value: Boolean)
}