package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryInteractor {
    fun getItemsFromCache(): ArrayList<Track>
    fun getItems(): ArrayList<Track>
    fun saveTrack(track: Track)
    fun clearTrackHistory()
}