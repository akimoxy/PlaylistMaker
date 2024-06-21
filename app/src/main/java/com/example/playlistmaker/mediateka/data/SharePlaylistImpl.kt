package com.example.playlistmaker.mediateka.data

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.playlistmaker.mediateka.data.playlist.PlaylistsDBConverter
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import java.util.Locale

class SharePlaylistImpl(
    private val context: Context, val appDataBase: AppDataBase, val conv: PlaylistsDBConverter
) : SharePlaylist {
    var number = 0
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    override suspend fun sharePlaylist(playlistsModel: PlaylistsModel) {

        var arrayOfString = arrayListOf<String>()
        var name: String = ""
        var trackName: String = ""
        var trackTime: String = ""
        playlistsModel.apply {
            for (i in playlistsModel.tracksId) {
                if (i.isNotEmpty()) {
                    number +=1
                    Log.d("id", i)
                    var trackEnt = appDataBase.trackEntityInPlDao().getTrackById(i)
                    name = trackEnt.artistName!!
                    Log.d("имя", trackEnt.artistName!!)
                     trackName = trackEnt.trackName!!
                    Log.d("трек нэйм", trackEnt.trackName!!)
                    trackTime = dateFormat.format(trackEnt.trackTimeMillis!!)
                    Log.d("тайм", trackEnt.trackTimeMillis.toString()!!)
                    var string = "$number. $name - $trackName \n $trackTime \n"
                    Log.d("стринг", string!!)
                    arrayOfString.add(string)
                    Log.d("эррэйр", arrayOfString.toString())
                }
                Log.d("эррэйр", arrayOfString.toString())
            }
            val text = arrayOfString.toString().replace(","," ",true ).replace("["," ",true ).replace("]"," ",true )
            Log.d("ттекст", text)

            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, playlistsModel.playlistName)
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.let(context::startActivity)
            number=0




        }
    }
}