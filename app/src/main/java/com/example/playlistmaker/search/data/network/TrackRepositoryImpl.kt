package com.example.playlistmaker.search.data.network

import android.util.Log
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
                isFavorite = false
            )
        }
        if (response.resultCode == SERVER_CODE_200) {
            Log.d("поиск репоз.импл", response.toString())
            emit(TrackResponseDomain(trackList as ArrayList<Track>, response.resultCode))
            Log.d("репоз.импл", trackList.toString())
        } else {
            Log.d("элс поиск репоз.импл", emptyArray.toString())
            emit(TrackResponseDomain(emptyArray, response.resultCode))
        }
    }
}

