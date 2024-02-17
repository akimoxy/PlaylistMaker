package com.example.playlistmaker.domain.api

import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.MediaPlayerState

interface MediaPlayerInteractor {
    fun initMediaPlayer(): MediaPlayer
    fun prepareMediaPlayer(url:String)
    fun startMediaPlayer()
    fun pauseMediaPlayer()
    fun mediaPlayerRelease()
    fun getMediaPlayerState():MediaPlayerState
    fun initPlayerState(state: MediaPlayerState)
    fun getCurrentPosition():Int
    fun isPlaying():Boolean
}