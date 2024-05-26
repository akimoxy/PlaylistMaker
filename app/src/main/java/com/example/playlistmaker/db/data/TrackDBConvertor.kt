package com.example.playlistmaker.db.data

import com.example.playlistmaker.search.domain.models.Track


class TrackDBConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId!!,
            track.trackName!!,
            track.artistName!!,
            track.collectionName!!,
            track.releaseDate!!,
            track.primaryGenreName!!,
            track.country!!,
            track.trackTimeMillis!!,
            track.artworkUrl100!!,
            track.previewUrl!!,
            track.isFavorite!!

        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.previewUrl,
            track.isFavorite
        )
    }
}