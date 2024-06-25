package com.example.playlistmaker.mediateka.domain

import com.example.playlistmaker.mediateka.data.SharePlaylist
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class Playlistsinteractorimpl(
    val plRepository: PlaylistsRepository,
    val sharePlaylist: SharePlaylist
) : PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<PlaylistsModel>> {
        return plRepository.getPlaylists()
    }

    override suspend fun insertPlaylist(playlist: PlaylistsModel) {
        plRepository.insertPlaylist(playlist)
    }

    override suspend fun getTrackIds(): List<String> {
        return plRepository.getTrackIds()
    }

    override fun updatePlaylistEntity(playlist: PlaylistsModel) {
        plRepository.updatePlaylistEntity(playlist)

    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: PlaylistsModel): Boolean {
        return plRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistById(id: Int): PlaylistsModel {
        return plRepository.getPlaylistById(id)
    }

    override suspend fun getTracksById(string: String): Track {
        return plRepository.getTracksById(string)
    }

    override suspend fun deleteById(string: String, playlist: PlaylistsModel) {
        plRepository.deleteById(string, playlist)
    }

    override suspend fun sharePlaylist(playlistsModel: PlaylistsModel) {
        sharePlaylist.sharePlaylist(playlistsModel)
    }

    override suspend fun deletePlaylist(playlist: PlaylistsModel) {
        plRepository.deletePlaylist(playlist)
    }

    override suspend fun updateBeforeDeletePMById(string: String, playlist: PlaylistsModel) {
       plRepository.updateBeforeDeletePMById(string,playlist)
    }


}