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
    override fun getTrackIds(): Flow<List<String>> {
      return  plRepository.getTrackIds()
    }
    override fun updatePlaylistEntity( playlist: PlaylistsModel){
        plRepository.updatePlaylistEntity(playlist)

    }
   override suspend fun addTrackToPlaylist(track: Track,playlist: PlaylistsModel):Boolean{
     return  plRepository.addTrackToPlaylist(track,playlist)
   }
}