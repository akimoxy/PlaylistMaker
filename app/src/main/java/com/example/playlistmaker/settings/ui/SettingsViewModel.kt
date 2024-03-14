package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,

    ) : ViewModel() {
    fun firstInitTheme(): Boolean {
        return settingsInteractor.switchBtnState()
    }

    fun onSwitchClick(isChecked: Boolean): Boolean {
        return settingsInteractor.switchTheme(isChecked)

    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun termsOfUse() {
        sharingInteractor.openTerms()
    }

    fun sendToSupport() {
        sharingInteractor.openSupport()
    }
}