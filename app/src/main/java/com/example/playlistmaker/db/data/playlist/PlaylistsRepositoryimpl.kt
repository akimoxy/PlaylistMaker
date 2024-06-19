package com.example.playlistmaker.db.data.playlist

import android.util.Log
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

    override fun getTrackIds(): Flow<List<String>> {
        return appDataBase.trackEntityInPlDao().getTrackIds()
    }

    override fun updatePlaylistEntity(playlist: PlaylistsModel) {
        val trackEnt = conv.mapInToPlaylistEntity(playlist)
        appDataBase.playlistDao().updatePlaylistEntity(trackEnt)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: PlaylistsModel): Boolean {
        val playlistTracks = playlist.tracksId
        if (playlistTracks.contains(track.trackId!!)) {
            Log.d("одинаковые id", "ololo")
            return false
        } else {
            val trackEnt = conv.mapToTrackInPlaylistEntity(track)
            appDataBase.trackEntityInPlDao().insertTrack(trackEnt)
            playlist.tracksId.add(track.trackId!!)
            playlist.countOfTracks = playlist.tracksId.size - 1
            updatePlaylistEntity(playlist)
        }
        return true
    }
}