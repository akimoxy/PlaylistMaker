package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private var darkTheme: Boolean = false
    override fun onCreate() {
        super.onCreate()
    }

    private fun returnBooleanShPrefNightTheme(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(EDIT_SWITCH_SETTINGS_KEY, darkTheme)
    }

    private fun saveSwitchTheme(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(EDIT_SWITCH_SETTINGS_KEY, darkTheme)
            .apply()
        applyTheme(context)
    }

    private fun applyTheme(context: Context): Boolean {
        val nightModeEnabled = returnBooleanShPrefNightTheme(context)
        if (nightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        return nightModeEnabled
    }

    fun switchBtnState(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPrefs.contains(EDIT_SWITCH_SETTINGS_KEY)) {
            darkTheme = applyTheme(context)
        } else if (isDarkThemeOn()) {
            darkTheme = isDarkThemeOn()
        } else {
            darkTheme = false
        }
        return darkTheme
    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun switchTheme(context: Context, darkThemeEnabled: Boolean): Boolean {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        returnBooleanShPrefNightTheme(context)
        saveSwitchTheme(context)
        return darkThemeEnabled
    }

    companion object {
        const val PLAYLIST_PREFERENCES = "practicum_preferences"
        const val EDIT_SWITCH_SETTINGS_KEY = "key_for_dark_theme"

    }
}