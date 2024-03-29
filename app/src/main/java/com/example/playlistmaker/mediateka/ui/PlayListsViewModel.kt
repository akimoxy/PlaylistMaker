package com.example.playlistmaker.mediateka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayListsViewModel(text: String) : ViewModel() {
    private val playlistsLiveData = MutableLiveData(text)
    fun observePlaylists(): LiveData<String> = playlistsLiveData
}