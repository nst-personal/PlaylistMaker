package com.example.playlistmaker.data.repositories.settings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.configuration.ShareablePreferencesConfig
import com.example.playlistmaker.domain.repositories.settings.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override fun findDarkMode(default: Boolean) : Boolean {
        val sharedPreferences = context.getSharedPreferences(ShareablePreferencesConfig.DARK_THEME,
            Application.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(ShareablePreferencesConfig.DARK_THEME, default);
    }

    override fun updateDarkMode(value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(ShareablePreferencesConfig.DARK_THEME,
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean(ShareablePreferencesConfig.DARK_THEME, value).apply()
    }
}