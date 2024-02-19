package com.example.playlistmaker.domain.api

import android.media.MediaPlayer
import com.example.playlistmaker.domain.models.MediaPlayerState

interface MediaPlayerInterf {
    fun initMediaPlayer(): MediaPlayer
    fun prepareMediaPlayer(url: String)
    fun startMediaPlayer()
    fun pauseMediaPlayer()
    fun getMediaPlayerState(): MediaPlayerState
    fun mediaPlayerRelease()
    fun initPlayerState(state: MediaPlayerState)
   fun getCurrentPosition():Int
   fun isPlaying():Boolean
}