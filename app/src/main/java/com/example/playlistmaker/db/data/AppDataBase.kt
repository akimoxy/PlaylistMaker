package com.example.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.data.favTracks.TrackDao
import com.example.playlistmaker.db.data.favTracks.TrackEntity
import com.example.playlistmaker.db.data.playlist.PlaylistEntity
import com.example.playlistmaker.db.data.playlist.PlaylistsDao
import com.example.playlistmaker.db.data.playlist.TrackEntityInPlaylists
import com.example.playlistmaker.db.data.playlist.TrackInPlaylistsDao

@Database(version = 1, entities = [TrackEntity::class,PlaylistEntity::class,TrackEntityInPlaylists::class])
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistsDao
    abstract fun trackEntityInPlDao(): TrackInPlaylistsDao
}