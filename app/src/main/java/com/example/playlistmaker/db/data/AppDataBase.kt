package com.example.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}