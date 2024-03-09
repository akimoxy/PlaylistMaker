package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerInteractorImpl(val mediaPl: MediaPlayerRepository, val url: String) :
    MediaPlayerInteractor {


    override fun prepareMediaPlayer() {
        mediaPl.prepareMediaPlayer(url)
    }

    override fun startMediaPlayer() {
        mediaPl.startMediaPlayer()
    }

    override fun pauseMediaPlayer() {
        mediaPl.pauseMediaPlayer()
    }

    override fun getCurrentPosition(): Int {
        return mediaPl.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return mediaPl.isPlaying()
    }

    override fun mediaPlayerRelease() {
        mediaPl.mediaPlayerRelease()
    }

    override fun setOnCompletionListnr(setOn: () -> Unit) {
        mediaPl.setOnCompletionListnr(setOn)
    }

    override fun setOnPreparedListnr(setOn: () -> Unit) {
        mediaPl.setOnPreparedListnr(setOn)
    }

}