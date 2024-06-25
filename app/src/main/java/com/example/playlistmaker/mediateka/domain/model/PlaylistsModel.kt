package com.example.playlistmaker.mediateka.domain.model

import android.net.Uri


data class PlaylistsModel(
    var playlistId:Int?,
    var playlistName: String,
    var playlistDescription: String,
    var imageStorageLink: Uri?,
    var playlistsImageName:String,
    var tracksId: ArrayList<String>,
    var countOfTracks: Int,
    var countOfTracksWithText: String

)
