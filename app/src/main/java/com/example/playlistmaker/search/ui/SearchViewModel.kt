package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.SearchActivityState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackResponseDomain
import com.example.playlistmaker.search.ui.SearchActivity.Companion.SEARCH_DEBOUNCE_DELAY

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor,
) : ViewModel() {
    val handler = Handler(Looper.getMainLooper())
    private val serverCode200 = 200
    private val serverCode400 = 400
    private val searchLiveData = MutableLiveData(SearchActivityState.LOADING)
    fun activityStateLiveData(): LiveData<SearchActivityState> = searchLiveData
    private fun updateState(state: SearchActivityState) {
        searchLiveData.postValue(state)
    }

    private val arrayList: ArrayList<Track> = arrayListOf()
    fun consume() = object : TrackInteractor.Consumer<TrackResponseDomain> {
        @SuppressLint("NotifyDataSetChanged")
        override fun consume(foundTracks: TrackResponseDomain) {
            handler.post {
                if (foundTracks.result.isNotEmpty() && foundTracks.resulCode == 200) {
                    updateState(SearchActivityState.SEARCH_TRACKS)
                    arrayList.clear()
                    arrayList.addAll(foundTracks.result)
                } else if (getTracks().isEmpty() && foundTracks.result.isEmpty() && foundTracks.resulCode == serverCode200) {
                    updateState(SearchActivityState.NO_RESULTS)
                } else if (foundTracks.resulCode == serverCode400) {
                    updateState(SearchActivityState.SERVER_ERROR)
                }
            }
        }
    }

    private val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
    private fun search(text: String) {
        trackInteractor.searchTrack(text, consume())
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun setHistory() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        getTrackHistoryInteractor().getItems()
        updateState(SearchActivityState.SEARCH_HISTORY)
    }

    fun searchDebounce(text: String) {
        getTracks().clear()
        val searchRunnable = Runnable { search(text) }
        updateState(SearchActivityState.LOADING)
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    init {
        setHistory()
    }

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application =
                        checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    return SearchViewModel(
                        trackInteractor = Creator.provideTrackInteractor(),
                        trackHistoryInteractor = Creator.getTrackHistory(application),
                    ) as T
                }
            }
    }

    fun getTracks(): ArrayList<Track> {
        return arrayList
    }

    fun getTrackHistoryInteractor(): TrackHistoryInteractor {
        return trackHistoryInteractor
    }

    fun getTrackInteractor(): TrackInteractor {
        return trackInteractor
    }
}