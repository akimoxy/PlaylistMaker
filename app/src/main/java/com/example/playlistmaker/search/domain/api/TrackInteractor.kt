package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.TrackResponseDomain

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: Consumer<TrackResponseDomain>)
    interface Consumer<TrackResponseDomain> {
        fun consume(foundTracks: TrackResponseDomain)
    }


}