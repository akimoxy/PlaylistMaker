package com.example.playlistmaker.db.domain.favTracks

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {
    fun getFavTracks(): Flow<List<Track>>
    suspend fun addToFavTracks(track: Track)
    suspend fun deleteFromFavTracks(track: Track)
    fun getTrackId(): Flow<List<String>>
}