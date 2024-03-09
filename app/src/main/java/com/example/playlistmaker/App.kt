package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {
    fun provideSettingsInteractor(): SettingsInteractor {
        return Creator.getSettingsInteractor()
    }

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
    }
}

