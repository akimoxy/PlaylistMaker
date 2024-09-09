package com.example.playlistmaker.mediateka.data

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.mediateka.data.playlist.PlaylistsDBConverter
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import java.util.Locale

class SharePlaylistImpl(
    private val context: Context, val appDataBase: AppDataBase, val conv: PlaylistsDBConverter
) : SharePlaylist {
    var number = 0
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    override suspend fun sharePlaylist(playlistsModel: PlaylistsModel) {
        val playlistName = playlistsModel.playlistName
        val playlistDescr = playlistsModel.playlistDescription
        val arrayOfString = arrayListOf<String>()
        var name = ""
        var trackName = ""
        var trackTime = ""
        playlistsModel.apply {
            arrayOfString.clear()
            for (i in playlistsModel.tracksId) {
                if (i.isNotEmpty()) {
                    number += 1
                    var trackEnt = appDataBase.trackEntityInPlDao().getTrackById(i)
                    name = trackEnt.artistName!!
                    trackName = trackEnt.trackName!!
                    trackTime = dateFormat.format(trackEnt.trackTimeMillis!!)
                    var string = "$number.$name - $trackName\n$trackTime\n"
                    arrayOfString.add(string)
                }
            }
            val text = arrayOfString.toString().replace(",", "", true).replace("[", "", true)
                .replace("]", "", true)
            val textToSend = "$playlistName \n$playlistDescr\n$text"

            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, playlistsModel.playlistName)
                putExtra(Intent.EXTRA_TEXT, textToSend)
                type = "text/plain"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.let(context::startActivity)
            number = 0
        }
    }
}