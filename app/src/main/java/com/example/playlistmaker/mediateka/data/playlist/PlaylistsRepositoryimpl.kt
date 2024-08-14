package com.example.playlistmaker.mediateka.data.playlist

import android.content.Context
import android.util.Log
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.data.AppDataBase
import com.example.playlistmaker.mediateka.domain.PlaylistsRepository
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryimpl(
    val appDataBase: AppDataBase,
    val conv: PlaylistsDBConverter,
    val context: Context
) :
    PlaylistsRepository {
    override fun getPlaylists(): Flow<List<PlaylistsModel>> {
        val playlists =  appDataBase.playlistDao().getPlaylists()
        var i = playlists.map { list ->
            list.map { conv.mapInToPlaylistModel( it) }
        }
        return i
    }

    override suspend fun insertPlaylist(playlist: PlaylistsModel) {
        val count = playlist.countOfTracks
        val pluralCount = context.resources.getQuantityString(
            R.plurals.plurals_tracks, playlist.countOfTracks
        )
        playlist.countOfTracksWithText = "$count $pluralCount"
        val playlistEnt = conv.mapInToPlaylistEntity(playlist)
        appDataBase.playlistDao().insertPlaylist(playlistEnt)
    }

    override suspend fun getTrackIds(): List<String> {
        return appDataBase.trackEntityInPlDao().getTrackIds()
    }

    override fun updatePlaylistEntity(playlist: PlaylistsModel) {
        val trackEnt = conv.mapInToPlaylistEntity(playlist)
        appDataBase.playlistDao().updatePlaylistEntity(trackEnt)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: PlaylistsModel): Boolean {
        val playlistTracks = playlist.tracksId
        if (playlistTracks.contains(track.trackId!!)) {
            return false
        } else {
            val trackEnt = conv.mapToTrackInPlaylistEntity(track)
            val track2 = appDataBase.trackEntityInPlDao().getTrackById(track.trackId!!)
            if (track2 != null) {
                Log.d(" НЕ добавили", "еруер")
            } else {
                Log.d("добавили", "еруер")
                appDataBase.trackEntityInPlDao().insertTrack(trackEnt)
            }

            playlist.tracksId.add(track.trackId!!)
            playlist.countOfTracks = playlist.tracksId.size - 1
            val count = playlist.countOfTracks
            val pluralCount = context.resources.getQuantityString(
                R.plurals.plurals_tracks, playlist.countOfTracks
            )
            playlist.countOfTracksWithText = "$count $pluralCount"

            updatePlaylistEntity(playlist)
            return true
        }
    }

    override suspend fun getPlaylistById(id: Int): PlaylistsModel {
        val pl = appDataBase.playlistDao().getPlaylistId(id)
        var model = conv.mapInToPlaylistModel(pl)
        return model
    }

    override suspend fun getTracksById(string: String): Track {
        val trackEnt = appDataBase.trackEntityInPlDao().getTrackById(string)
        return conv.mapToTrack(trackEnt)
    }

    override suspend fun updateBeforeDeletePMById(string: String, playlist: PlaylistsModel) {
        if (playlist.countOfTracks > 0) {
            playlist.countOfTracks = playlist.tracksId.size - 1
            val pluralCount = context.resources.getQuantityString(
                R.plurals.plurals_tracks, playlist.countOfTracks
            )
            val count = playlist.countOfTracks
            playlist.countOfTracksWithText = "$count $pluralCount"
        }
        if (playlist.countOfTracks == 0) {
            val pluralCount = context.resources.getQuantityString(
                R.plurals.plurals_tracks, playlist.countOfTracks
            )
            val count = playlist.countOfTracks
            playlist.countOfTracksWithText = "$count $pluralCount"

        }
        updatePlaylistEntity(playlist)
    }

    override suspend fun deleteById(string: String, playlist: PlaylistsModel) {
        var tracks = true
        appDataBase.playlistDao().getPlaylistsSuspend().forEach { list ->
            if (list.tracksId.contains(string)) {
                tracks = false
            }
        }
        if (tracks) {
            appDataBase.trackEntityInPlDao().deleteTrack(string)
        }
    }

    override suspend fun deletePlaylist(playlist: PlaylistsModel) {
        val plEnt = conv.mapInToPlaylistEntity(playlist)
        appDataBase.playlistDao().deletePlaylistEnt(plEnt)
        playlist.tracksId.forEach {
            deleteById(it, playlist)
        }
        if (appDataBase.playlistDao().getPlaylistsSuspend().isEmpty()) {
            for (i in playlist.tracksId) {
                appDataBase.trackEntityInPlDao().deleteTrack(i)
            }
        }
    }

}


