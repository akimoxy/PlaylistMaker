package com.example.playlistmaker.mediateka.data.favTracks

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavTracksInteractorImpl(
    private val favTracksRepository: FavTracksRepository
) : FavTracksInteractor {
    override fun getFavTracks(): Flow<List<Track>> {
        return favTracksRepository.getFavTracks()
    }

    override suspend fun addToFavTracks(track: Track) {
        favTracksRepository.addToFavTracks(track)
    }

    override suspend fun deleteFromFavTracks(track: Track) {
        favTracksRepository.deleteFromFavTracks(track)
    }

    override fun getTrackId(): Flow<List<String>> {

        return favTracksRepository.getTrackId()
    }

}