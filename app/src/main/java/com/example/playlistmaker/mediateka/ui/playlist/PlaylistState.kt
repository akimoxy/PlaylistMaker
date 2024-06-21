package com.example.playlistmaker.mediateka.ui.playlist

import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistState {
    data class Playlist(
        var playlists: PlaylistsModel?,
        val tracks: ArrayList<Track>?,
        val tracksTiming: Long?
    ) : PlaylistState



}