package com.example.playlistmaker.mediateka.ui.newPlaylists

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import kotlinx.coroutines.launch

class NewPlaylistViewModel(val interactor: PlaylistsInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<NewPlaylistScreenState>()
    fun observeState(): LiveData<NewPlaylistScreenState> = stateLiveData
    lateinit var playlist: PlaylistsModel


    init {
        changeState(NewPlaylistScreenState.Empty)
    }

    fun changeState(state: NewPlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    fun createPlaylist(newPlaylist: PlaylistsModel) {
        viewModelScope.launch { interactor.insertPlaylist(newPlaylist) }
    }

    fun changeUri(uri1: Uri) {
        changeState(NewPlaylistScreenState.PlaylistsUri(uri1))
    }

    fun changeName(string: String) {
        changeState(NewPlaylistScreenState.NamePlaylists(string))
    }

    fun changeDescription(string: String) {
        changeState(NewPlaylistScreenState.DescriptionPlaylists(string))
    }
    fun saveImageName(string: String) {
        changeState(NewPlaylistScreenState.ImageName(string))
    }

}



