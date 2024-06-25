package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<PlaylistsModel>>
    suspend fun insertPlaylist(playlist: PlaylistsModel)
    suspend fun getTrackIds(): List<String>
    fun updatePlaylistEntity(playlist: PlaylistsModel)
    suspend fun addTrackToPlaylist(track: Track, playlist: PlaylistsModel):Boolean
   suspend fun getPlaylistById(id:Int): PlaylistsModel
   suspend fun getTracksById(string: String):Track
    suspend fun deleteById(string: String,playlist: PlaylistsModel)
    suspend  fun sharePlaylist( playlistsModel: PlaylistsModel)
    suspend fun deletePlaylist(
        playlist: PlaylistsModel)

    suspend fun updateBeforeDeletePMById(string: String, playlist: PlaylistsModel)
}