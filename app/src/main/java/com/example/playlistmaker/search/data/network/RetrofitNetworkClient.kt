package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.ui.SERVER_CODE_400

class RetrofitNetworkClient(val iTunesService:ITunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                val resp = iTunesService.findTrack(dto.expression).execute()
                val body = resp.body() ?: Response()
                return body.apply {
                    resultCode = resp.code()
                }
            } else {
                return Response().apply { resultCode = SERVER_CODE_400 }
            }
        } catch (e: Throwable) {
            Response().apply { resultCode = SERVER_CODE_400 }
        }
    }
}