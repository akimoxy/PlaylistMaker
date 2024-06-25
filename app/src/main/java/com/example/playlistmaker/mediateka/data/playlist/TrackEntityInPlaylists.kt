package com.example.playlistmaker.mediateka.data.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_in_playlists_table")
data class TrackEntityInPlaylists(
    @PrimaryKey
    val id:Int?,
    val trackId: String?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    var isFavorite: Boolean?
)


