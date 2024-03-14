package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerRepository


class MediaPlayerImpl(var medPl: MediaPlayer) : MediaPlayerRepository {

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

    override fun mediaPlayerRelease() {
        medPl.release()

    }

    override fun getCurrentPosition(): Int {
        return medPl.currentPosition
    }

    override fun isPlaying(): Boolean {
        return medPl.isPlaying

    }

    override fun setOnPreparedListnr(setOn: () -> Unit) {
        medPl.setOnPreparedListener { setOn() }
    }

    override fun setUrl(url: String): String {
        return url
    }
    override fun setOnCompletionListnr(setOn: () -> Unit) {

        medPl.setOnCompletionListener { setOn() }
    }
}