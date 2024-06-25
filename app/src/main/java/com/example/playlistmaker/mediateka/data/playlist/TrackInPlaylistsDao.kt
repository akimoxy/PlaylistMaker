package com.example.playlistmaker.mediateka.data.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TrackInPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: TrackEntityInPlaylists)

    @Query("SELECT trackId  FROM track_in_playlists_table")
    suspend fun getTrackIds(): List<String>

    @Query("SELECT * FROM track_in_playlists_table WHERE trackId = :trId")
    suspend fun getTrackById(trId: String): TrackEntityInPlaylists

    @Query("DELETE FROM track_in_playlists_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)

    @Update(entity = TrackEntityInPlaylists::class, onConflict = OnConflictStrategy.REPLACE)
    fun updatePlaylistEntity(playlist: TrackEntityInPlaylists)
}