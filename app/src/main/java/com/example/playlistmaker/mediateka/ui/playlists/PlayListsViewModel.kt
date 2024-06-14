package com.example.playlistmaker.mediateka.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import kotlinx.coroutines.launch

class PlayListsViewModel(val playlists: PlaylistsInteractor) : ViewModel() {
    private val playlistsLiveData =
        MutableLiveData<PlayListsMediatekaState>(PlayListsMediatekaState.Empty)

    fun observeState(): LiveData<PlayListsMediatekaState> = playlistsLiveData
    var playlistsList: ArrayList<PlaylistsModel> = arrayListOf()

    init {
        viewModelScope.launch {
            playlists.getPlaylists().collect { pl ->
                playlistsList = ArrayList(pl)
                playlistsList.reverse()
                processResult(playlistsList)
            }
        }
    }

    private fun processResult(playlists: ArrayList<PlaylistsModel>) {
        if (playlists.isEmpty()) {
            renderState(PlayListsMediatekaState.Empty)
        } else {
            renderState(PlayListsMediatekaState.Playlists(playlists))
        }
    }

    private fun renderState(state: PlayListsMediatekaState) {
        playlistsLiveData.postValue(state)
    }
}