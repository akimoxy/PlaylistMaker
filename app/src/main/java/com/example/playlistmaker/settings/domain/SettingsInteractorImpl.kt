package com.example.playlistmaker.settings.domain

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