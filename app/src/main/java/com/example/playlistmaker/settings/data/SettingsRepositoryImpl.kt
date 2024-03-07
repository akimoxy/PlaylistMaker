package com.example.playlistmaker.settings.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(var sharedPrefs: SharedPreferences) : SettingsRepository {
    private var darkTheme = ThemeSettings(false)
    override fun getThemeSettings(): Boolean {
        return sharedPrefs.getBoolean(EDIT_SWITCH_SETTINGS_KEY, darkTheme.themeSettings)
    }

    private fun saveSwitchTheme() {
        sharedPrefs.edit()
            .putBoolean(EDIT_SWITCH_SETTINGS_KEY, darkTheme.themeSettings)
            .apply()
    }

    override fun switchBtnState(context: Context): Boolean {
        if (sharedPrefs.contains(EDIT_SWITCH_SETTINGS_KEY)) {
            darkTheme.themeSettings = getThemeSettings()
            //         applyTheme()
        } else if (context.isDarkThemeOn()) {
            darkTheme.themeSettings = context.isDarkThemeOn()
        } else {
            darkTheme.themeSettings = false
        }
        saveSwitchTheme()
        switchTheme(darkTheme.themeSettings)
        return darkTheme.themeSettings
    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun switchTheme(darkThemeEnabled: Boolean): Boolean {
        darkTheme.themeSettings = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme.themeSettings) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveSwitchTheme()
        return darkTheme.themeSettings
    }

    companion object {
        const val PLAYLIST_PREFERENCES = "practicum_preferences"
        const val EDIT_SWITCH_SETTINGS_KEY = "key_for_dark_theme"
    }
}