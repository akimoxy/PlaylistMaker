package com.example.playlistmaker.mediateka.data.playlist

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.mediateka.ui.newPlaylists.MY_PLAYLIST_IMAGE
import com.example.playlistmaker.search.domain.models.Track
import java.io.File

class PlaylistsDBConverter(private val context: Context) {
    fun mapInToPlaylistEntity(playlist: PlaylistsModel): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistsImageName,
            playlist.playlistsImageName,
            toString(playlist.tracksId),
            playlist.countOfTracks
        )
    }

    fun mapInToPlaylistModel(playlistEntity: PlaylistEntity): PlaylistsModel {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), MY_PLAYLIST_IMAGE)
        val uri = if (playlistEntity.imageStorageLink.isEmpty()) null
        else File(filePath, playlistEntity.imageStorageLink).toUri()
        Log.d("мэп ту модел", playlistEntity.tracksId.toString())
        return PlaylistsModel(
            playlistEntity.playlistId,
            playlistEntity.playlistName!!,
            playlistEntity.playlistDescription,
            uri,
            playlistEntity.playlistsImageName,
            fromString(playlistEntity.tracksId),
            playlistEntity.countOfTracks
        )

    }

    fun mapToTrackInPlaylistEntity(track: Track): TrackEntityInPlaylists {
        return TrackEntityInPlaylists(
            id = null,
            track.trackId!!,
            track.trackName!!,
            track.artistName!!,
            track.collectionName!!,
            track.releaseDate!!,
            track.primaryGenreName!!,
            track.country!!,
            track.trackTimeMillis!!,
            track.artworkUrl100!!,
            track.previewUrl!!,
            track.isFavorite!!

        )
    }

    fun mapToTrack(track: TrackEntityInPlaylists): Track {
        return Track(
            track.trackName!!,
            track.artistName!!,
            track.collectionName!!,
            track.releaseDate!!,
            track.primaryGenreName!!,
            track.country!!,
            track.trackTimeMillis!!,
            track.artworkUrl100!!,
            track.trackId!!,
            track.previewUrl!!,
            track.isFavorite!!
        )
    }

    @TypeConverter
    fun fromString(string: String): ArrayList<String> {
        val list = string.split(",")
        val arrayList = arrayListOf<String>()
        arrayList.addAll(list)
        return arrayList
    }

    @TypeConverter
    fun toString(stringList: ArrayList<String>): String {
        return stringList.joinToString(separator = ",")
    }

}

