package com.example.playlistmaker.player.settings.domain

interface SettingsRepository {
   fun switchTheme( darkThemeEnabled: Boolean): Boolean
    fun switchBtnState(): Boolean
    fun getThemeSettings(): Boolean
}