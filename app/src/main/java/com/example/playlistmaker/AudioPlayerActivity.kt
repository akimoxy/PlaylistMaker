package com.example.playlistmaker

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val backBtnAudioPlayer = findViewById<Button>(R.id.back_button_audio_player)
        backBtnAudioPlayer?.setOnClickListener {
            finish()
        }

        val trackName = intent.getStringExtra("trackName") ?: ""
        val artistName = intent.getStringExtra("artistName") ?: ""
        val trackTimeMillis = intent.getLongExtra("trackTimeMillis", 0)
        var artworkUrl1001 = intent.getStringExtra("artworkUrl100") ?: ""
        val collectionName = intent.getStringExtra("collectionName") ?: ""
        val releaseDate = intent.getStringExtra("releaseDate") ?: ""
        val primaryGenreName = intent.getStringExtra("primaryGenreName") ?: ""
        val country = intent.getStringExtra("country") ?: ""

        artworkUrl1001 = artworkUrl1001.replaceAfterLast('/', "512x512bb.jpg")

        @SuppressLint("SimpleDateFormat")
        fun trackTime(trackTimeMillis: Long): String {
            val dateFormat = SimpleDateFormat("mm:ss")
            val trackTimeMillis777 = dateFormat.format(trackTimeMillis)
            return trackTimeMillis777.toString()
        }

        @SuppressLint("SimpleDateFormat")

        val artistNameView: TextView = findViewById(R.id.track_artist_player)
        val trackNameView: TextView = findViewById(R.id.album_big_text_player)
        val trackTimeView: TextView = findViewById(R.id.track_time_mediapl)
        val artworkUrlView: ImageView = findViewById(R.id.track_album_image_player)
        val countryView: TextView = findViewById(R.id.country_text_player)
        val collectionNameView: TextView = findViewById(R.id.collection_name_player)
        val releaseDateView: TextView = findViewById(R.id.year_text_player)
        val primaryGenreNameView: TextView = findViewById(R.id.genre_text_player)

        primaryGenreNameView.text = primaryGenreName
        releaseDateView.text = releaseDate
        collectionNameView.text = collectionName
        countryView.text = country
        trackNameView.text = trackName
        artistNameView.text = artistName
        trackTimeView.text = trackTime(trackTimeMillis)

        Glide.with(artworkUrlView)
            .load(artworkUrl1001)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(artworkUrlView, 2f)))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrlView)
    }
    private fun dpToPx(view: View, dp: Float): Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            .toInt()
    }
}