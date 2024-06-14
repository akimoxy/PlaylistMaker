package com.example.playlistmaker.db.data.playlist

import com.example.playlistmaker.db.data.AppDataBase
import com.example.playlistmaker.db.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryimpl(val appDataBase: AppDataBase, val conv: PlaylistsDBConverter) :
    PlaylistsRepository {
    override fun getPlaylists(): Flow<List<PlaylistsModel>> {
       val playlists = appDataBase.playlistDao().getPlaylists()
      return playlists.map { list -> list.map { conv.mapInToPlaylistModel(it) } }

    }
    override suspend fun insertPlaylist(playlist: PlaylistsModel) {
        val playlistEnt = conv.mapInToPlaylistEntity(playlist)
        appDataBase.playlistDao().insertPlaylist(playlistEnt)
    }

    override suspend fun addToPlaylists(track: Track) {
        val trackEnt=conv.mapToTrackInPlaylistEntity(track)
        appDataBase.trackEntityInPlDao().insertTrack(trackEnt)
    }

    override fun getTrackId(): Flow<List<String>> {
      return  appDataBase.trackEntityInPlDao().getTrackId()
    }
}