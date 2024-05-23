package com.example.playlistmaker.mediateka.ui.favTracks

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavTracksState {
    data class Content(val tracks: List<Track>) : FavTracksState
    object Empty : FavTracksState
}