package com.example.playlistmaker.db.data.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(tracks: TrackEntityInPlaylists)

    @Query("SELECT trackId  FROM track_in_playlists_table")
    fun getTrackIds(): Flow<List<String>>
}