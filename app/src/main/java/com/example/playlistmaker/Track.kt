package com.example.playlistmaker


import kotlinx.serialization.Serializable

@Serializable
data class Track(
    var trackName: String,
    var artistName: String,
    var collectionName: String,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String,
    var trackTimeMillis: Long,
    var artworkUrl100: String,
    var trackId: String,
    var previewUrl:String
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

}