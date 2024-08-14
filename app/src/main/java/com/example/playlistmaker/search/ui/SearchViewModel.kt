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
    private val searchLiveData =
        MutableLiveData<SearchActivityState>(SearchActivityState.NoTextOrFocusState)
    private var latestSearchText: String? = null
    private var searchJob: Job? = null
    private var isClickAllowed = true

    init {
        updateState(SearchActivityState.NoTextOrFocusState)
    }

    fun getState(): LiveData<SearchActivityState> = searchLiveData

    fun updateState(state: SearchActivityState) {
        searchLiveData.value = state
    }

    private val searchJobDetails: Job? = null
    private fun search(text: String) {
        searchJobDetails?.cancel()
        updateState(SearchActivityState.Loading)
        searchJobDetails != viewModelScope.launch {
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

    fun setHistory() {
        val tracks = getHistoryItems()
        if (tracks.isNotEmpty()) {
            updateState(SearchActivityState.SearchHistory)
        }
    }

    fun searchDebounce(text: String) {
        latestSearchText = text
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
            viewModelScope.launch {
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