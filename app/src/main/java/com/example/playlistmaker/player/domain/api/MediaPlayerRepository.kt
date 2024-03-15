package com.example.playlistmaker.player.domain.api

interface MediaPlayerRepository {
    fun prepareMediaPlayer(url: String)
    fun startMediaPlayer()
    fun pauseMediaPlayer()
    fun mediaPlayerRelease()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListnr(setOn: () -> Unit)
    fun setOnPreparedListnr(setOn: () -> Unit)
     fun setUrl(url:String):String
}