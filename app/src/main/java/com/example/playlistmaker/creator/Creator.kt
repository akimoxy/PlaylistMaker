package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl.Companion.PLAYLIST_PREFERENCES
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.practicum.playlist.domain.sharing.impl.SharingInteractorImpl

object Creator {
    private lateinit var application: App
    fun init(app:App):App{
      application =app
      return application
    }
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun getTrackHistory(): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(
            TrackHistoryRepositoryImpl(application)
        )
    }

    fun provideMediaPlayerInteractor(url: String): MediaPlayerInteractor {
        val mediaPl = MediaPlayerImpl()
        return MediaPlayerInteractorImpl(mediaPl, url)
    }

    private fun getSettingsRepository(): SettingsRepository {
        val sharedPreferences =
            application.getSharedPreferences(PLAYLIST_PREFERENCES, Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPreferences, application)
    }

    fun getSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun getSharingInterator(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context), context)
    }
}