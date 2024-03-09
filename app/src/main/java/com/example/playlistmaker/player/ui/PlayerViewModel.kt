package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.DELAY_1SEC
import java.util.Locale

class PlayerViewModel(val playerInteractor: MediaPlayerInteractor) : ViewModel() {
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val time = timer()
    private val playerLiveData = MutableLiveData<ScreenState>(ScreenState.Default)
    fun observeState(): LiveData<ScreenState> = playerLiveData

    companion object {
        fun getViewModelFactory(url: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    return PlayerViewModel(
                        playerInteractor = Creator.provideMediaPlayerInteractor(url)
                    ) as T
                }
            }
    }

    fun preparePlayer() {
        playerInteractor.prepareMediaPlayer()
        playerInteractor.setOnCompletionListnr {
            mainThreadHandler.removeCallbacks(time)
            playerLiveData.postValue(ScreenState.Prepare)
        }
        playerInteractor.setOnPreparedListnr { playerLiveData.postValue(ScreenState.Prepare) }


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