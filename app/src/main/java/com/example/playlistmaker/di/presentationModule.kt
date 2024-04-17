package com.example.playlistmaker.di

import com.example.playlistmaker.mediateka.ui.FavoriteTracksViewModel
import com.example.playlistmaker.mediateka.ui.PlayListsViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel<SettingsViewModel> {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }
    viewModel<PlayerViewModel> {
        PlayerViewModel(playerInteractor = get())
    }
    viewModel<SearchViewModel> {
        SearchViewModel(trackInteractor = get(), trackHistoryInteractor = get())
    }
    viewModel<FavoriteTracksViewModel>{
        FavoriteTracksViewModel(text = get())
    }
    viewModel<PlayListsViewModel> {
        PlayListsViewModel(text = get())
    }
}
