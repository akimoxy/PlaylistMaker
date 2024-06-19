package com.example.playlistmaker.di

import com.example.playlistmaker.db.data.favTracks.FavTracksRepositoryImpl
import com.example.playlistmaker.db.data.playlist.PlaylistsRepositoryimpl
import com.example.playlistmaker.db.domain.favTracks.FavTracksInteractor
import com.example.playlistmaker.db.domain.favTracks.FavTracksInteractorImpl
import com.example.playlistmaker.db.domain.favTracks.FavTracksRepository
import com.example.playlistmaker.db.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.db.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.db.domain.playlists.Playlistsinteractorimpl
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
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
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


    single<FavTracksRepository> {
        FavTracksRepositoryImpl(get(), get())
    }


    factory<FavTracksInteractor> {
        FavTracksInteractorImpl(get())
    }

    factory<PlaylistsInteractor> {
        Playlistsinteractorimpl(plRepository = get())
    }

    factory<Playlistsinteractorimpl> {Playlistsinteractorimpl(plRepository = get())  }
    factory<PlaylistsRepository> { PlaylistsRepositoryimpl(appDataBase = get(), conv = get()) }

}