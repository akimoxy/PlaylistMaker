package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerInterf
import com.example.playlistmaker.domain.models.MediaPlayerState

class MediaPlayerImpl() : MediaPlayerInterf {
    private var mediaPlState = MediaPlayerState.STATE_DEFAULT
    val medPl = MediaPlayer()
    override fun initMediaPlayer(): MediaPlayer {
        return medPl
    }

    override fun prepareMediaPlayer(url: String) {
        medPl.reset()
        medPl.setDataSource(url)
        medPl.prepareAsync()
    }

    override fun startMediaPlayer() {
        medPl.start()
    }

    override fun pauseMediaPlayer() {
        medPl.pause()
    }

    override fun getMediaPlayerState(): MediaPlayerState {
        return mediaPlState
    }

    override fun mediaPlayerRelease() {
        initMediaPlayer().release()
    }

    override fun initPlayerState(state: MediaPlayerState) {
        mediaPlState = state
    }

    override fun getCurrentPosition(): Int {
        return medPl.currentPosition
    }

    override fun isPlaying(): Boolean {
        return medPl.isPlaying

    }
}