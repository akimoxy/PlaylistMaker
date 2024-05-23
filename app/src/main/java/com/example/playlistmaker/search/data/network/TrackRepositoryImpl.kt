package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackResponseDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val SERVER_CODE_200 = 200

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,

) : TrackRepository {
    private var emptyArray: ArrayList<Track> = arrayListOf()
    override fun searchTrack(expression: String): Flow<TrackResponseDomain> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == SERVER_CODE_200) {
            val trackList = (response as TrackResponse?)!!.results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.previewUrl,
                    false
                )
            }
        //    saveTack(response.results)
            emit(TrackResponseDomain(trackList as ArrayList<Track>, response.resultCode))
        } else {
            emit(TrackResponseDomain(emptyArray, response.resultCode))
        }
    }

 /*   private suspend fun saveTack(tracks: List<TrackDto>) {
        val trackEntity = tracks.map { tracks -> trackDBConvertor.map(tracks) }
        appDataBase.trackDao().insertTracks(trackEntity)
    }
    /
  */
}

