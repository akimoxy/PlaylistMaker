package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.data.MediaPlayerImpl
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
     private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())}

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository() )
    }

  fun getTrackHistory(shP:SharedPreferences): TrackHistoryInteractor{ return TrackHistoryInteractorImpl(TrackHistoryRepositoryImpl(shP) )
  }
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
      val mediaPl=MediaPlayerImpl()
       return MediaPlayerInteractorImpl(mediaPl)
    }
}