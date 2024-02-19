package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.TrackResponseDomain

interface TrackRepository {
    fun searchTrack(expression: String): TrackResponseDomain
}