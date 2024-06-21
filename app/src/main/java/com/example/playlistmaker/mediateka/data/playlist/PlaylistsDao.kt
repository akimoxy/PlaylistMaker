package com.example.playlistmaker.mediateka.data.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>
    @Query("SELECT * FROM playlists_table")
    suspend fun getPlaylistsSuspend(): List<PlaylistEntity>

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updatePlaylistEntity(playlist: PlaylistEntity)

    @Query("SELECT * FROM  playlists_table WHERE playlistId = :id")
  suspend  fun getPlaylistId(id: Int): PlaylistEntity

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylistEnt(playlist: PlaylistEntity)
}
