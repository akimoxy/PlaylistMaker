package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

const val MAX_SIZE_TRACK_HISTORY_ARRAY = 10
const val FIRST_INDEX_IN_ARRAY = 0

class TrackHistoryInteractorImpl(var history: TrackHistoryRepository) : TrackHistoryInteractor {
    override fun getItemsFromCache(): ArrayList<Track> {
        return history.getItemsFromCache()
    }

    override fun getItems(): ArrayList<Track> {
        return history.getItems()
    }

    override fun saveTrack(track: Track) {
        val trackHistoryArray = getItemsFromCache()
        trackHistoryArray.removeIf { it.trackId == track.trackId }
        trackHistoryArray.add(track)
        if (trackHistoryArray.size > MAX_SIZE_TRACK_HISTORY_ARRAY) {
            trackHistoryArray.removeAt(FIRST_INDEX_IN_ARRAY)
        }
        history.saveTrack(trackHistoryArray)
    }

    override fun clearTrackHistory() {
        history.clearTrackHistory()
    }

}