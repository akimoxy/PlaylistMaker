package com.example.playlistmaker.mediateka.ui.newPlaylists

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.PlaylistsInteractor
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(open val interactor: PlaylistsInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<NewPlaylistScreenState>()
    open fun observeState(): LiveData<NewPlaylistScreenState> = stateLiveData
    lateinit var playlist: PlaylistsModel
   open var uri: Uri? = null

    init {
        changeState(NewPlaylistScreenState.Empty)
    }

  open fun changeState(state: NewPlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    fun createPlaylist(newPlaylist: PlaylistsModel) {
        viewModelScope.launch { interactor.insertPlaylist(newPlaylist) }
    }

   open fun changeName(string: String) {
        changeState(NewPlaylistScreenState.NamePlaylists(string))
    }

   open fun changeDescription(string: String) {
        changeState(NewPlaylistScreenState.DescrPlaylists(string))
    }

  open  fun saveImageName(string: String) {
        changeState(NewPlaylistScreenState.ImageName(string))
    }

  open  fun imageIsNotEmpty(boolean: Boolean) {
        changeState(NewPlaylistScreenState.ImageIsNotEmpty(boolean))
    }

}



