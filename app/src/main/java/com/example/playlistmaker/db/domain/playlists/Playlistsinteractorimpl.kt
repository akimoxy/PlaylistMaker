package com.example.playlistmaker.db.domain.playlists

import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class Playlistsinteractorimpl(val plRepository: PlaylistsRepository):PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<PlaylistsModel>> {
       return plRepository.getPlaylists()
    }

    override suspend fun insertPlaylist(playlist: PlaylistsModel) {
        plRepository.insertPlaylist(playlist)
    }
    override suspend fun addToPlaylists(track: Track) {
        plRepository.addToPlaylists(track)
    }

    override fun getTrackId(): Flow<List<String>> {
      return  plRepository.getTrackId()
    }
}