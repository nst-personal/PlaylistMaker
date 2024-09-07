package com.example.playlistmaker.domain.interactors.settings

interface SettingsManager {
    fun findDarkMode(default: Boolean): Boolean
    fun updateDarkMode(value: Boolean)
}