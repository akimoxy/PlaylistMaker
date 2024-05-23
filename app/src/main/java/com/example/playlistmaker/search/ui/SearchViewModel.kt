package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

 const val SERVER_CODE_200 = 200
 const val SERVER_CODE_400 = 400
private const val SEARCH_DEBOUNCE_DELAY = 2000L
private const val CLICK_DEBOUNCE_DELAY = 1000L
class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor,
) : ViewModel() {
    private val searchLiveData = MutableLiveData<SearchActivityState>(SearchActivityState.Loading)
    private var latestSearchText: String? = null
    private var searchJob: Job? = null
    private var isClickAllowed = true
    fun activityStateLiveData(): LiveData<SearchActivityState> = searchLiveData

    init {
        setHistory()
    }

    private fun updateState(state: SearchActivityState) {
        searchLiveData.postValue(state)
    }

    private fun search(text: String) {
        updateState(SearchActivityState.Loading)
        viewModelScope.launch {
            trackInteractor.searchTrack(text).collect { foundTracks ->
                if (foundTracks.result.isNotEmpty() && foundTracks.resulCode == SERVER_CODE_200) {
                    updateState(SearchActivityState.SearchTracks(foundTracks.result))
                } else if (foundTracks.result.isEmpty() && foundTracks.resulCode == SERVER_CODE_200) {
                    updateState(SearchActivityState.NoResult)
                } else if (foundTracks.resulCode == SERVER_CODE_400) {
                    updateState(SearchActivityState.ServerError)
                }
            }
        }
    }
    private fun setHistory() {
        updateState(SearchActivityState.SearchHistory(getHistoryItems()))
    }

    fun searchDebounce(text: String) {
        if (latestSearchText == text) {
            return
        }
        latestSearchText = text
        updateState(SearchActivityState.Loading)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(text)
        }
    }
    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch{
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
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