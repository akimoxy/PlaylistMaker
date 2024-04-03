package com.example.playlistmaker.mediateka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteTracksViewModel(text: String) : ViewModel() {
    private val favoriteTracksLiveData = MutableLiveData(text)
    fun observeFavoriteTracks(): LiveData<String> = favoriteTracksLiveData

}