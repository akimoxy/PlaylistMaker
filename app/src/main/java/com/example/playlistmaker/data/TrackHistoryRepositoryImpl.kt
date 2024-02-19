package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

const val TRACK_HISTORY_KEY = "track_history_key"

class TrackHistoryRepositoryImpl(private val shP: SharedPreferences) : TrackHistoryRepository {
    private val gson: Gson = Gson()
    override fun getItemsFromCache(): ArrayList<Track> {
        val json: String = shP.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()
        return ArrayList(gson.fromJson(json, Array<Track>::class.java).toList())
    }

    override fun getItems(): ArrayList<Track> {
        val itemsFromCache = getItemsFromCache()
        itemsFromCache.reverse()
        return itemsFromCache
    }

    override fun saveTrack(trackArray: ArrayList<Track>) {
        val json = gson.toJson(trackArray)
        shP.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    override fun clearTrackHistory() {
        shP.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }
}