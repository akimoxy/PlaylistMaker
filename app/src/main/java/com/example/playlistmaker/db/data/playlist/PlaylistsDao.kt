package com.example.playlistmaker.db.data.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>
}
