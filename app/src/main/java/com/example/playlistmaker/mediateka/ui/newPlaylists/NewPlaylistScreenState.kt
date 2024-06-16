package com.example.playlistmaker.mediateka.ui.newPlaylists

sealed interface NewPlaylistScreenState {
    object Empty : NewPlaylistScreenState
    data class NamePlaylists(var name: String) : NewPlaylistScreenState
    data class DescriptionPlaylists(var description: String) : NewPlaylistScreenState
    data class ImageName(var name: String) : NewPlaylistScreenState
    data class ImageIsNotEmpty(var boolean: Boolean) : NewPlaylistScreenState
}