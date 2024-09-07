package com.example.playlistmaker.creators.settings

import android.content.Context
import com.example.playlistmaker.data.repositories.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.interactors.settings.SettingsInteractor
import com.example.playlistmaker.domain.interactors.settings.SettingsInteractorImpl
import com.example.playlistmaker.domain.repositories.settings.SettingsRepository

object SettingsCreator {
    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsManager(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}