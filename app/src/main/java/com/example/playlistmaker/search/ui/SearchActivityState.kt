package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchActivityState {
    data class SearchHistory(val history: ArrayList<Track>) : SearchActivityState
    data class SearchTracks(val history: List<Track>) : SearchActivityState
    object ServerError : SearchActivityState
    object NoResult : SearchActivityState
    object Loading : SearchActivityState

}