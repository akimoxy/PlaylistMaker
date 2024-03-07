package com.example.playlistmaker.settings.data

import android.content.Context

interface SettingsRepository {
   fun switchTheme( darkThemeEnabled: Boolean): Boolean
    fun switchBtnState(context: Context): Boolean
    fun getThemeSettings(): Boolean
}