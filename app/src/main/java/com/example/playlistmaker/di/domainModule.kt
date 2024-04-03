package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.player.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.player.settings.domain.SettingsInteractor
import com.example.playlistmaker.player.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.player.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.practicum.playlist.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get(), context = get(), darkTheme = get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(settings = get())
    }
    factory<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get(), context = get())
    }
    factory<MediaPlayerRepository> {
        MediaPlayerImpl(medPl = get())
    }
    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(mediaPlayerRepository = get())
    }
    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get())
    }
    factory<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }
    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(context = get(), gson = get(), sharedPreferences = get())
    }
    factory<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(history = get())
    }
    factory<TrackHistoryInteractorImpl> { TrackHistoryInteractorImpl(history = get()) }

}