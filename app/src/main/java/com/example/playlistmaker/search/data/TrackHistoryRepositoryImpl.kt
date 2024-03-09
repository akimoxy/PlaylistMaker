package com.example.playlistmaker.search.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.TrackHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

const val TRACK_HISTORY_KEY = "track_history_key"
const val TRACK_HISTORY = "track_history_preferences"

class TrackHistoryRepositoryImpl(context: Context) : TrackHistoryRepository {
    private val gson: Gson = Gson()
    private val shP = getSharedPreferences(context)
    override fun getItemsFromCache(): ArrayList<Track> {
        val json: String = shP.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()
        return ArrayList(gson.fromJson(json, Array<Track>::class.java).toList())
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
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