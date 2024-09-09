package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryInteractor {
    fun getItemsFromCache(): MutableList<Track>
    fun getItems():MutableList<Track>
    fun saveTrack(track: Track)
    fun clearTrackHistory()
}