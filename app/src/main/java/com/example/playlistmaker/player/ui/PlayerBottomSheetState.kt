package com.example.playlistmaker.player.ui

import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel

sealed interface PlayerBottomSheetState {
    data class PlaylistsBottomSheet(val playlists: ArrayList<PlaylistsModel>) :
        PlayerBottomSheetState

    object Empty : PlayerBottomSheetState
    data class AddToPlaylist(val playlistsModel: PlaylistsModel) : PlayerBottomSheetState
}