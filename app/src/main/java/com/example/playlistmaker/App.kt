package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
    }

    fun switchBtn(): Boolean {
        val sharedPrefs = getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(EDIT_SWITCH_SETTINGS_KEY, darkTheme).also { darkTheme = it }
    }

    private fun saveSwitchTheme(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(EDIT_SWITCH_SETTINGS_KEY, darkTheme)
            .apply()
    }

    fun switchTheme(context: Context, darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveSwitchTheme(context)
        switchBtn()
    }


    companion object {
        const val PLAYLIST_PREFERENCES = "practicum_preferences"
        const val EDIT_SWITCH_SETTINGS_KEY = "key_for_dark_theme"

    }


}