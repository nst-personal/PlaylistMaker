package com.example.playlistmaker.domain.interactors.settings

interface SettingsInteractor {
    fun findDarkMode(default: Boolean): Boolean
    fun updateDarkMode(value: Boolean)
}