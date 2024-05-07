package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.ui.SERVER_CODE_400
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(val iTunesService: ITunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            withContext(Dispatchers.IO) {
                try {
                    val resp = iTunesService.findTrack(dto.expression)
                    resp.apply { resultCode = 200 }
                } catch (e: Throwable) {
                    Response().apply { resultCode = SERVER_CODE_400 }
                }
            }
        } else {
            Response().apply { resultCode = SERVER_CODE_400 }
        }
    }


}