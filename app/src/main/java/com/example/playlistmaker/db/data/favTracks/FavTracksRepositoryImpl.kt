package com.example.playlistmaker.db.data.favTracks

import com.example.playlistmaker.db.data.AppDataBase
import com.example.playlistmaker.db.domain.favTracks.FavTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavTracksRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val trackDBConvertor: TrackDBConvertor,

    ) : FavTracksRepository {
    override fun getFavTracks(): Flow<List<Track>>  {
     val favTrack = appDataBase.trackDao().getTracks()
    return  covertFromTrackEntity(favTrack)
    }

    override suspend fun addToFavTracks(track: Track) {
        val trackEntities = trackDBConvertor.map(track)
        appDataBase.trackDao().insertTrack(trackEntities)
    }

    override suspend fun deleteFromFavTracks(track: Track) {
        val trackEntities = trackDBConvertor.map(track)

        appDataBase.trackDao().deleteTrackEntity(trackEntities)

    }

    override fun getTrackId(): Flow<List<String>> {
        return appDataBase.trackDao().getTrackId()
    }

    private fun covertFromTrackEntity(tracks: Flow< List<TrackEntity>>):Flow< List<Track> >{
        return  tracks.map { list->list.map {trackDBConvertor.map(it)  } }
    }


}