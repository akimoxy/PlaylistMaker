package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.TrackResponseDomain

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: Consumer<TrackResponseDomain>)
    interface Consumer<TrackResponseDomain> {
        fun consume(foundTracks: TrackResponseDomain)
    }

}