package com.example.playlistmaker.di

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
        PlayerViewModel(playerInteractor = get())
    }
    viewModel<SearchViewModel> {
        SearchViewModel(trackInteractor = get(), trackHistoryInteractor = get())
    }
}
