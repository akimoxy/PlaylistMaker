package com.example.playlistmaker

import java.io.Serializable


data class Track(
    var trackName: String,
    var artistName: String,
    var collectionName: String,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String,
    var trackTimeMillis: Long,
    var artworkUrl100: String,
    var trackId: String
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

}