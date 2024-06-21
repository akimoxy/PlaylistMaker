package com.example.playlistmaker.mediateka.ui.favTracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.data.favTracks.FavTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favTracksInteractor: FavTracksInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavTracksState>()
    fun observeState(): LiveData<FavTracksState> = stateLiveData
     var trackList: ArrayList<Track> = arrayListOf()


    init {
        viewModelScope.launch {
            favTracksInteractor.getFavTracks().collect { tracks ->
                trackList = ArrayList(tracks)
                trackList.reverse()
                processResult(trackList)
            }
        }
    }

    private fun renderState(state: FavTracksState) {
        stateLiveData.postValue(state)
    }

    private fun processResult(tracks: ArrayList<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavTracksState.Empty)
        } else {
            renderState(FavTracksState.Content(tracks))
        }
    }

}