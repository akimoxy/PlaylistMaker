package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchActivityState {
    object SearchHistory : SearchActivityState
    data class SearchTracks(val searchTracks: List<Track>) : SearchActivityState
    object ServerError : SearchActivityState
    object NoResult : SearchActivityState
    object Loading : SearchActivityState
    object NoTextOrFocusState : SearchActivityState

}