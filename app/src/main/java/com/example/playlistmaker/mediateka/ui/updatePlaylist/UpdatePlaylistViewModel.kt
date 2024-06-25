package com.example.playlistmaker.mediateka.ui.updatePlaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.PlaylistsInteractor
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.mediateka.ui.newPlaylists.NewPlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpdatePlaylistViewModel(override val interactor: PlaylistsInteractor) :
    NewPlaylistViewModel(interactor) {

    private val stateLD = MutableLiveData<UpdPlaylistScreenState>()
    fun observe(): LiveData<UpdPlaylistScreenState> = stateLD
    override var uri: Uri? = null
    private var job: Job? = null
    lateinit var plM: PlaylistsModel
    fun changeSt(state: UpdPlaylistScreenState) {
        stateLD.postValue(state)
    }

    fun updatePlaylist(playlist: PlaylistsModel) {
        viewModelScope.launch(Dispatchers.IO) { interactor.updatePlaylistEntity(playlist) }
    }

    fun playlistThis(id: Int) {
        job = viewModelScope.launch {
            plM = interactor.getPlaylistById(id)
            uri = plM.imageStorageLink
            changeSt(UpdPlaylistScreenState.Playlist(plM))
        }
    }

}