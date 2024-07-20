package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.configuration.ShareablePreferencesConfig


class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(ShareablePreferencesConfig.DARK_THEME, MODE_PRIVATE)
        val currentMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val appMode = sharedPreferences.getBoolean(ShareablePreferencesConfig.DARK_THEME, currentMode);
        switchTheme(appMode)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}