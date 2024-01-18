package com.example.playlistmaker

import android.os.Parcel
import android.os.Parcelable

//import kotlinx.serialization.Serializable

//@Serializable
data class Track(
    var trackName: String,
    var artistName: String,
    var collectionName:String,
    var releaseDate:String,
    var primaryGenreName:String,
    var country:String,
    var trackTimeMillis: Long,
    var artworkUrl100: String,
    var trackId: String
) : Parcelable {
fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
        parcel.writeLong(trackTimeMillis)
        parcel.writeString(artworkUrl100)
        parcel.writeString(trackId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}