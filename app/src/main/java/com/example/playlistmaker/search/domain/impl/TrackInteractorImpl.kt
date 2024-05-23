package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.TrackResponseDomain
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTrack(
        expression: String
    ): Flow<TrackResponseDomain> {
        return repository.searchTrack(expression)
    }
}