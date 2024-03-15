package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerInteractorImpl(val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {


    override fun prepareMediaPlayer(url: String) {
        mediaPlayerRepository.prepareMediaPlayer(url)
    }

    override fun startMediaPlayer() {
        mediaPlayerRepository.startMediaPlayer()
    }

    override fun pauseMediaPlayer() {
        mediaPlayerRepository.pauseMediaPlayer()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayerRepository.isPlaying()
    }

    override fun mediaPlayerRelease() {
        mediaPlayerRepository.mediaPlayerRelease()
    }

    override fun setOnCompletionListnr(setOn: () -> Unit) {
        mediaPlayerRepository.setOnCompletionListnr(setOn)
    }

    override fun setOnPreparedListnr(setOn: () -> Unit) {
        mediaPlayerRepository.setOnPreparedListnr(setOn)
    }
}