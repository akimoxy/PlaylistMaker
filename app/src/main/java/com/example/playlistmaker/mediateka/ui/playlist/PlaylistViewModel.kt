package com.example.playlistmaker.mediateka.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.PlaylistsInteractor
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistViewModel(val playlists: PlaylistsInteractor) : ViewModel() {
    private val playlistLiveData =
        MutableLiveData<PlaylistState>()
    var tracks = arrayListOf<Track>()
    var tracksTiming: Long = 0
    var tracksTimingMin: Long = 0
    lateinit var playlistsM: PlaylistsModel
    fun observeState(): LiveData<PlaylistState> = playlistLiveData
    private var job: Job? = null
    private var job2: Job? = null

    fun playlist(id: Int) {
        job = viewModelScope.launch {
            playlistsM = playlists.getPlaylistById(id)
            tracks.clear()
            for (id in playlistsM.tracksId) {
                if (id.isNotEmpty()) {

                    val track = playlists.getTracksById(id)
                    tracks.add(track)
                    tracksTiming += track.trackTimeMillis!!
                }
            }
            tracksTimingMin = tracksTiming / 60000
            renderState(PlaylistState.Playlist(playlistsM, tracks, tracksTimingMin))
        }
    }

    private fun renderState(state: PlaylistState) {
        playlistLiveData.postValue(state)
    }

    fun deleteTrack(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsM.tracksId.remove(id)
            playlists.updateBeforeDeletePMById(id,playlistsM)
            playlists.deleteById(id, playlistsM)
        }
    }

    fun share(playlistsModel: PlaylistsModel) {
        viewModelScope.launch { playlists.sharePlaylist(playlistsModel) }
    }
    fun deletePlaylistModel(playlistsModel: PlaylistsModel){
       viewModelScope.launch(Dispatchers.IO) {
           playlists.deletePlaylist(playlistsModel) }

    }

}