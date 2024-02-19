package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackHistoryInteractor {
    fun getItemsFromCache(): ArrayList<Track>
    fun getItems(): ArrayList<Track>
    fun saveTrack(track: Track)
    fun clearTrackHistory()
}