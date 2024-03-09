package com.example.playlistmaker.settings.data

interface SettingsRepository {
   fun switchTheme( darkThemeEnabled: Boolean): Boolean
    fun switchBtnState(): Boolean
    fun getThemeSettings(): Boolean
}