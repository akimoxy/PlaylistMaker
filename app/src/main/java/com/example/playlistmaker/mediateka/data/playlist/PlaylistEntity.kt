package com.example.playlistmaker.mediateka.data.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int?,
    val playlistName: String?,
    val playlistDescription: String,
    val imageStorageLink: String,
    var playlistsImageName: String,
    var tracksId: String,
    var countOfTracks: Int,
    var countOfTracksWithText: String
)


