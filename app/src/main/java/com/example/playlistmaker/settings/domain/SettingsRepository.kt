package com.example.playlistmaker.settings.domain

interface SettingsRepository {
   fun switchTheme( darkThemeEnabled: Boolean): Boolean
    fun switchBtnState(): Boolean
    fun getThemeSettings(): Boolean
}