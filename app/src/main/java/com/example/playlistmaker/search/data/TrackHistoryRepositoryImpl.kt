package com.example.playlistmaker.search.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.TrackHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

const val TRACK_HISTORY_KEY = "track_history_key"


class TrackHistoryRepositoryImpl(
     context: Context,
    val gson: Gson,
    val sharedPreferences: SharedPreferences
) : TrackHistoryRepository {

    override fun getItemsFromCache(): ArrayList<Track> {
        val json: String =
            sharedPreferences.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()
        return ArrayList(gson.fromJson(json, Array<Track>::class.java).toList())
    }

    override fun getItems(): ArrayList<Track> {
        val itemsFromCache = getItemsFromCache()
        itemsFromCache.reverse()
        return itemsFromCache
    }

    override fun saveTrack(trackArray: ArrayList<Track>) {
        val json = gson.toJson(trackArray)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    override fun clearTrackHistory() {
        sharedPreferences.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }
}