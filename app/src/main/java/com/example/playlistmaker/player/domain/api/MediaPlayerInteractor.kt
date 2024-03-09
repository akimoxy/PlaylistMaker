package com.example.playlistmaker.player.domain.api

interface MediaPlayerInteractor {
    fun prepareMediaPlayer()
    fun startMediaPlayer()
    fun pauseMediaPlayer()
    fun mediaPlayerRelease()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListnr(setOn: () -> Unit)
    fun setOnPreparedListnr(setOn: () -> Unit)
}