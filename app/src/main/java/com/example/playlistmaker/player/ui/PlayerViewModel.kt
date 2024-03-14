package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.DELAY_1SEC
import java.util.Locale

class PlayerViewModel(val playerInteractor: MediaPlayerInteractor) : ViewModel() {
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val time = timer()
    private val playerLiveData = MutableLiveData<ScreenState>(ScreenState.Default)
    fun observeState(): LiveData<ScreenState> = playerLiveData

    fun preparePlayer(url: String) {
        playerInteractor.prepareMediaPlayer(url)
        Log.d("преп","ыпцкп")
        playerInteractor.setOnCompletionListnr {
            mainThreadHandler.removeCallbacks(time)
            playerLiveData.postValue(ScreenState.Prepare)
        }
        playerInteractor.setOnPreparedListnr { playerLiveData.postValue(ScreenState.Prepare)
            Log.d("2","укрук")
        }
    }

    fun startPlayer() {
        playerInteractor.startMediaPlayer()
        playerLiveData.postValue(ScreenState.PlayingTime(dateFormat.format(playerInteractor.getCurrentPosition())))
        mainThreadHandler.post(time)
    }

    private fun timer(): Runnable {
        return object : Runnable {
            override fun run() {
                playerLiveData.postValue(ScreenState.PlayingTime(dateFormat.format(playerInteractor.getCurrentPosition())))
                mainThreadHandler.postDelayed(this, DELAY_1SEC)
            }
        }
    }

    fun pausePlayer() {
        mainThreadHandler.removeCallbacks(time)
        playerInteractor.pauseMediaPlayer()
        playerLiveData.postValue(ScreenState.Pause(dateFormat.format(playerInteractor.getCurrentPosition())))
    }

    override fun onCleared() {
        super.onCleared()
       playerInteractor.mediaPlayerRelease()
    }
}