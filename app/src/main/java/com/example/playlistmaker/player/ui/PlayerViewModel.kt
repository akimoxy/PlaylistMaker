package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
const val DELAY_300_MILLISEC = 300L
class PlayerViewModel(val playerInteractor: MediaPlayerInteractor) : ViewModel() {
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val playerLiveData = MutableLiveData<ScreenState>(ScreenState.Default)
    private var timerJob: Job? = null
    fun observeState(): LiveData<ScreenState> = playerLiveData

    fun preparePlayer(url: String) {
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

    override fun onCleared() {
        super.onCleared()
        playerInteractor.mediaPlayerRelease()
    }
}