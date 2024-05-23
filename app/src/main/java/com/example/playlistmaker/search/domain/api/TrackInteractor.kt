package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.TrackResponseDomain
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTrack(expression: String): Flow<TrackResponseDomain>

}