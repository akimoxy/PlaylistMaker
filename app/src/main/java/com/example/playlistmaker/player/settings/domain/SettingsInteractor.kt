package com.example.playlistmaker.player.settings.domain

interface SettingsInteractor {
    fun switchTheme(darkThemeEnabled: Boolean): Boolean
    fun switchBtnState(): Boolean
    fun getThemeSettings(): Boolean
}