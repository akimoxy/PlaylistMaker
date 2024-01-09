package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_HISTORY = "track_history_preferences"
const val TRACK_HISTORY_KEY = "track_history_key"

class SearchHistory(
    private val shP: SharedPreferences,
    listener: RecyclerViewEvent,
) {
    //private var sharedPreferences = shP.getSharedPreferences("TRACK_HISTORY", Context.MODE_PRIVATE)
    var trackHistoryArray: ArrayList<Track> = arrayListOf()
    private val gson: Gson = Gson()

    fun getItemsFromCache(): ArrayList<Track>? {
        val json: String = shP.getString(TRACK_HISTORY_KEY, null) ?: return arrayListOf()
        return gson.fromJson<ArrayList<Track>>(json, object : TypeToken<ArrayList<Track>>() {}.type)
    }

    fun saveTrack(
        track: Track
    ) {
        trackHistoryArray = getItemsFromCache()!!
        for (i in trackHistoryArray) {
            if (i.trackId == track.trackId)
                trackHistoryArray.remove(i)
        }
        trackHistoryArray.add(track)
        if (trackHistoryArray.size > 9) {
            trackHistoryArray.removeAt(0)
        }

        val json = gson.toJson(trackHistoryArray)
        shP.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
        adapterForHistoryTracks.addTracks(trackHistoryArray)
    }

    val adapterForHistoryTracks: TrackAdapter = TrackAdapter(trackHistoryArray)
}

interface RecyclerViewEvent {
    fun onItemClick(track: Track)
}