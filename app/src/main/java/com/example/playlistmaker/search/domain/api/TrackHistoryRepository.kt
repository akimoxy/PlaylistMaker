package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryRepository {
    fun getItemsFromCache(): MutableList<Track>
    fun getItems(): MutableList<Track>
    fun saveTrack(trackList: MutableList<Track>)
    fun clearTrackHistory()
}