package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.data.SettingsRepository

class SettingsInteractorImpl(var settings: SettingsRepository) : SettingsInteractor {

    override fun getThemeSettings(): Boolean {
        return settings.getThemeSettings()
    }
    override fun switchTheme(darkThemeEnabled: Boolean): Boolean {
        return settings.switchTheme(darkThemeEnabled)
    }
    override fun switchBtnState(): Boolean {
        return settings.switchBtnState()
    }
}