package com.example.playlistmaker.player.domain.api

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.models.MediaPlayerState

interface MediaPlayerInteractor {
    fun initMediaPlayer(): MediaPlayer
    fun prepareMediaPlayer()
    fun startMediaPlayer()
    fun pauseMediaPlayer()
    fun mediaPlayerRelease()
    fun getMediaPlayerState(): MediaPlayerState
    fun initPlayerState(state: MediaPlayerState)
    fun getCurrentPosition():Int
    fun isPlaying():Boolean
}