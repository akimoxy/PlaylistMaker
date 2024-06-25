package com.example.playlistmaker.di

import com.example.playlistmaker.mediateka.ui.favTracks.FavoriteTracksViewModel
import com.example.playlistmaker.mediateka.ui.newPlaylists.NewPlaylistViewModel
import com.example.playlistmaker.mediateka.ui.playlist.PlaylistViewModel
import com.example.playlistmaker.mediateka.ui.playlists.PlayListsViewModel
import com.example.playlistmaker.mediateka.ui.updatePlaylist.UpdatePlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel<SettingsViewModel> {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }
    viewModel<PlayerViewModel> {
        PlayerViewModel(playerInteractor = get(), favTracksInteractor = get(), playlistsInteractor = get())
    }
    viewModel<SearchViewModel> {
        SearchViewModel(trackInteractor = get(), trackHistoryInteractor = get())
    }
    viewModel<FavoriteTracksViewModel>{
        FavoriteTracksViewModel(favTracksInteractor = get())
    }
    viewModel<PlayListsViewModel> {
        PlayListsViewModel(playlists = get())
    }
    viewModel<NewPlaylistViewModel>{
        NewPlaylistViewModel(interactor = get())
    }
    viewModel<PlaylistViewModel> { PlaylistViewModel(playlists = get()) }
    viewModel <UpdatePlaylistViewModel>{ UpdatePlaylistViewModel(interactor = get()) }
    }
