package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.MediaPlayerInterf
import com.example.playlistmaker.domain.models.MediaPlayerState

class MediaPlayerInteractorImpl(val mediaPl: MediaPlayerInterf) : MediaPlayerInteractor {
    override fun initMediaPlayer(): MediaPlayer {
        return mediaPl.initMediaPlayer()

    }

    override fun prepareMediaPlayer(url: String) {
      mediaPl.prepareMediaPlayer(url)
    }

    override fun startMediaPlayer() {
        mediaPl.startMediaPlayer()
    }

    override fun pauseMediaPlayer() {
        mediaPl.pauseMediaPlayer()
    }

    override fun getMediaPlayerState(): MediaPlayerState {
        return mediaPl.getMediaPlayerState()
    }

    override fun initPlayerState(state: MediaPlayerState) {
        mediaPl.initPlayerState(state)

    }

    override fun getCurrentPosition(): Int {
       return mediaPl.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
      return  mediaPl.isPlaying()
    }

    override fun mediaPlayerRelease() {
        mediaPl.mediaPlayerRelease()
    }


}