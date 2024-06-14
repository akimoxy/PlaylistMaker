package com.example.playlistmaker.mediateka.ui.newPlaylists

import android.net.Uri

sealed interface NewPlaylistScreenState {
    object Empty : NewPlaylistScreenState
    data class NamePlaylists(var name: String) : NewPlaylistScreenState
    data class DescriptionPlaylists(var description: String) : NewPlaylistScreenState
    data class PlaylistsUri(var uri: Uri) : NewPlaylistScreenState
    data class ImageName(var name: String) : NewPlaylistScreenState
}