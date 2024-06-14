package com.example.playlistmaker.db.domain.playlists

import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<PlaylistsModel>>
    suspend fun insertPlaylist(playlist: PlaylistsModel)
    suspend fun addToPlaylists(track: Track)
    fun getTrackId(): Flow<List<String>>

}