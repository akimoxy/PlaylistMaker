package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel

interface SharePlaylist {
  suspend  fun sharePlaylist( playlistsModel: PlaylistsModel)
}