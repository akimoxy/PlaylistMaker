package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val resulCode400 = 400
    private val iTunsBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunsBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                val resp = iTunesService.findTrack(dto.expression).execute()
                val body = resp.body() ?: Response()
                return body.apply {
                    resultCode = resp.code()
                }
            } else {
                return Response().apply { resultCode = resulCode400 }
            }
        } catch (e: Throwable) {
            Response().apply { resultCode = resulCode400 }
        }
    }
}