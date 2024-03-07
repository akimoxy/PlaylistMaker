package com.example.playlistmaker.settings.domain

import android.content.Context
import com.example.playlistmaker.settings.data.SettingsRepository

class SettingsInteractorImpl(var settings: SettingsRepository) : SettingsInteractor {

    override fun getThemeSettings(): Boolean {
        return settings.getThemeSettings()
    }
    override fun switchTheme(darkThemeEnabled: Boolean): Boolean {
        return settings.switchTheme(darkThemeEnabled)
    }
    override fun switchBtnState(context: Context): Boolean {
        return settings.switchBtnState(context)
    }
}