package com.example.playlistmaker.domain.creators.settings

import android.content.Context
import com.example.playlistmaker.data.repositories.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.managers.settings.SettingsManager
import com.example.playlistmaker.domain.managers.settings.SettingsManagerImpl
import com.example.playlistmaker.domain.repositories.settings.SettingsRepository

class SettingsCreator {
    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsManager(context: Context): SettingsManager {
        return SettingsManagerImpl(getSettingsRepository(context))
    }
}