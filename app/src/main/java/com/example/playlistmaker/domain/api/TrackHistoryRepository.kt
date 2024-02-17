package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackHistoryRepository {
    fun getItemsFromCache(): ArrayList<Track>
    fun getItems(): ArrayList<Track>
    fun saveTrack(trackArray: ArrayList<Track>)
    fun clearTrackHistory()
}