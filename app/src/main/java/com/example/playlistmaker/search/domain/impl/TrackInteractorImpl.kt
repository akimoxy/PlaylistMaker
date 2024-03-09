package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.TrackResponseDomain

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTrack(
        expression: String,
        consumer: TrackInteractor.Consumer<TrackResponseDomain>
    ) {
        val t = Thread {
            consumer.consume(repository.searchTrack(expression))
        }
        t.start()
    }


}