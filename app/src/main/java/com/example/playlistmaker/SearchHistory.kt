package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val TRACK_HISTORY = "track_history_preferences"
const val TRACK_HISTORY_KEY = "track_history_key"

class SearchHistory(private val shP: SharedPreferences) {
    var trackHistoryArray: ArrayList<Track> = arrayListOf()
    private val gson: Gson = Gson()

    private fun getItemsFromCache(): ArrayList<Track> {
        val json: String = shP.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()
        return ArrayList(gson.fromJson(json, Array<Track>::class.java).toList())
    }
    fun getItems(): ArrayList<Track> {
        val itemsFromCache = getItemsFromCache()
        itemsFromCache.reverse()
        trackHistoryArray = itemsFromCache
        return trackHistoryArray
    }
    fun saveTrack(track: Track) {
        trackHistoryArray = getItemsFromCache()
        trackHistoryArray.removeIf { it.trackId == track.trackId }
        trackHistoryArray.add(track)
        if (trackHistoryArray.size > 10) {
            trackHistoryArray.removeAt(0)
        }
        val json = gson.toJson(trackHistoryArray)
        shP.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    fun clearTrackHistory() {
        trackHistoryArray.clear()
        shP.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }
}

