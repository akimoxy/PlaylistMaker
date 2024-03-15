package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackResponseDomain

const val SERVER_CODE_200 = 200
const val SERVER_CODE_400 = 400
const val SEARCH_DEBOUNCE_DELAY = 2000L
private val SEARCH_REQUEST_TOKEN = Any()

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor,
) : ViewModel() {
    val handler = Handler(Looper.getMainLooper())
    private val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
    private val searchLiveData = MutableLiveData<SearchActivityState>(SearchActivityState.Loading)
    fun activityStateLiveData(): LiveData<SearchActivityState> = searchLiveData
    private fun updateState(state: SearchActivityState) {
        searchLiveData.postValue(state)
    }

    private fun consume() = object : TrackInteractor.Consumer<TrackResponseDomain> {
        @SuppressLint("NotifyDataSetChanged")
        override fun consume(foundTracks: TrackResponseDomain) {
            handler.post {
                if (foundTracks.result.isNotEmpty() && foundTracks.resulCode == 200) {
                    updateState(SearchActivityState.SearchTracks(foundTracks.result))
                } else if (foundTracks.result.isEmpty() && foundTracks.resulCode == SERVER_CODE_200) {
                    updateState(SearchActivityState.NoResult)
                } else if (foundTracks.resulCode == SERVER_CODE_400) {
                    updateState(SearchActivityState.ServerError)
                }
            }
        }
    }

    private fun search(text: String) {
        trackInteractor.searchTrack(text, consume())
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun setHistory() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        updateState(SearchActivityState.SearchHistory(getHistoryItems()))
    }

    init {
        setHistory()
    }

    fun searchDebounce(text: String) {
        val searchRunnable = Runnable { search(text) }
        updateState(SearchActivityState.Loading)
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }


    fun getHistoryItems(): ArrayList<Track> {
        return trackHistoryInteractor.getItems()
    }

    fun clearTrackHistory() {
        trackHistoryInteractor.clearTrackHistory()
    }

    fun saveTrackToHistory(track: Track) {
        trackHistoryInteractor.saveTrack(track)
    }
}