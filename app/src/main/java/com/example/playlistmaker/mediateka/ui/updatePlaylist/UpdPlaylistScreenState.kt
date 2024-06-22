package com.example.playlistmaker.mediateka.ui.updatePlaylist

import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel

sealed interface UpdPlaylistScreenState {
    data class Playlist(var playlistsModel: PlaylistsModel) : UpdPlaylistScreenState
}