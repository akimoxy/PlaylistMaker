package com.example.playlistmaker.di

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeSettings
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single {
        androidContext().getSharedPreferences(
            SettingsRepositoryImpl.PLAYLIST_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }
    factory<ExternalNavigator> {
        ExternalNavigatorImpl(context = get(), intent = get())
    }
    factory { Intent() }

    single { ThemeSettings(false) }

    factory<MediaPlayerImpl> { MediaPlayerImpl(medPl = get()) }
    factory { MediaPlayer() }
    factory<TrackRepositoryImpl> {
        TrackRepositoryImpl(networkClient = get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(iTunesService = get())
    }
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }
    single { TrackHistoryRepositoryImpl(context = get(), gson = get(), sharedPreferences = get()) }

    single { Gson() }
}
