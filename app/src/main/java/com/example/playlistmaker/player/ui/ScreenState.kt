package com.example.playlistmaker.player.ui

sealed interface ScreenState {
    object Default : ScreenState
    object Prepare : ScreenState
    data class Pause(val time: String) : ScreenState
    data class PlayingTime(val time: String) : ScreenState
}