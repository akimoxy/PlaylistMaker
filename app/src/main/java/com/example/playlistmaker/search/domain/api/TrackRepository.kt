package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.TrackResponseDomain

interface TrackRepository {
    fun searchTrack(expression: String): TrackResponseDomain

}