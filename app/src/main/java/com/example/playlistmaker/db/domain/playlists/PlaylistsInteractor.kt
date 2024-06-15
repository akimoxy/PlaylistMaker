package com.example.playlistmaker.db.domain.playlists

import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<PlaylistsModel>>
    suspend fun insertPlaylist(playlist: PlaylistsModel)
    fun getTrackIds(): Flow<List<String>>
    fun updatePlaylistEntity(playlist: PlaylistsModel)
    suspend fun addTrackToPlaylist(track: Track, playlist: PlaylistsModel):Boolean

}