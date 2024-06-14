package com.example.playlistmaker.mediateka.ui.playlists

import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel

sealed interface PlayListsMediatekaState {
    data class Playlists(val playlists: ArrayList<PlaylistsModel>) : PlayListsMediatekaState
    object Empty : PlayListsMediatekaState
}