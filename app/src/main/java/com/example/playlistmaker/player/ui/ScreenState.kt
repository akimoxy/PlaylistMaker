package com.example.playlistmaker.player.ui

sealed interface ScreenState {
    object Default : ScreenState
    data class Pause(val time: String) : ScreenState
    object Prepare : ScreenState
    data class PlayingTime(val time: String) : ScreenState
}