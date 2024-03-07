package com.example.playlistmaker.settings.domain

import android.content.Context

interface SettingsInteractor {
    fun switchTheme(darkThemeEnabled: Boolean): Boolean
    fun switchBtnState(context: Context): Boolean
    fun getThemeSettings(): Boolean
}