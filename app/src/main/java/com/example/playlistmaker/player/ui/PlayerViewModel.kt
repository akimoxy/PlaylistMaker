package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.FavTracksInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private const val DELAY_300_MILLISEC = 300L

class PlayerViewModel(
    val playerInteractor: MediaPlayerInteractor,
    val favTracksInteractor: FavTracksInteractor
) : ViewModel() {
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val playerLiveData = MutableLiveData<ScreenState>(ScreenState.Default)
    private var timerJob: Job? = null
    private var trackId = ""
    private val favoriteLiveData = MutableLiveData(false)

    val mediatorLiveDataObserver = MediatorLiveData<Pair<ScreenState?, Boolean?>>().apply {
        addSource(playerLiveData) { value = Pair(it, favoriteLiveData.value) }
        addSource(favoriteLiveData) { value = Pair(playerLiveData.value, it) }
    }

    init {
        viewModelScope.launch {
            favTracksInteractor.getTrackId().collect { id ->
                favoriteLiveData.value = id.contains(trackId)
            }
        }
    }

    fun preparePlayer(url: String, track: Track) {
        trackId = track.trackId!!
        playerInteractor.prepareMediaPlayer(url)
        playerInteractor.setOnCompletionListnr {
            playerLiveData.postValue(ScreenState.Prepare)
        }
        playerInteractor.setOnPreparedListnr {
            playerLiveData.postValue(ScreenState.Prepare)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY_300_MILLISEC)
                playerLiveData.postValue(ScreenState.PlayingTime(dateFormat.format(playerInteractor.getCurrentPosition())))
            }
        }
    }

    fun startPlayer() {
        playerInteractor.startMediaPlayer()
        playerLiveData.postValue(ScreenState.PlayingTime(dateFormat.format(playerInteractor.getCurrentPosition())))
        startTimer()
    }

    fun pausePlayer() {
        timerJob?.cancel()
        playerInteractor.pauseMediaPlayer()
        playerLiveData.postValue(ScreenState.Pause(dateFormat.format(playerInteractor.getCurrentPosition())))
    }

    fun addToFavTracks(track: Track) {
        track.isFavorite = true
        viewModelScope.launch {
            favTracksInteractor.addToFavTracks(track)
        }
    }

    fun deleteFromFavTrack(track: Track) {
        track.isFavorite = false
        viewModelScope.launch {
            favTracksInteractor.deleteFromFavTracks(track)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.mediaPlayerRelease()
    }
}