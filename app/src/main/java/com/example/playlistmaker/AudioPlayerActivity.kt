package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.TRACK
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import kotlinx.serialization.json.Json
import java.io.Serializable
class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backBtnAudioPlayer = findViewById<Button>(R.id.back_button_audio_player)
        backBtnAudioPlayer?.setOnClickListener {
            finish()
        }
        fun <T : Serializable?> getSerializable(intent: Intent, key: String, className: Class<T>): T {
            return if (Build.VERSION.SDK_INT >= 33)
                intent.getSerializableExtra(key, className)!!
            else
                intent.getSerializableExtra(key) as T
        }
        val json = getSerializable(intent,TRACK,String::class.java)
        val track = Json.decodeFromString<Track>(json)

        // val track = intent.getSerializableExtra(TRACK) as Track
        //track= Json.decodeFromString<Track>(track)
        track.artworkUrl100 = track.getCoverArtwork()

        @SuppressLint("SimpleDateFormat")
        fun trackTime(trackTimeMillis: Long): String {
          val dateFormat = SimpleDateFormat("mm:ss")
           val trackTimeMillis777 = dateFormat.format(trackTimeMillis)
            return trackTimeMillis777.toString()
        }

        @SuppressLint("SimpleDateFormat")

        binding.genreTextPlayer.text = track.primaryGenreName
        binding.yearTextPlayer.text = track.releaseDate
        binding.collectionNamePlayer.text = track.collectionName
        binding.countryTextPlayer.text = track.country
        binding.albumBigTextPlayer.text = track.trackName
        binding.trackArtistPlayer.text = track.artistName
        binding.trackTimeMediapl.text = trackTime(track.trackTimeMillis)

        Glide.with(binding.trackAlbumImagePlayer)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(binding.trackAlbumImagePlayer, 12f)))
            .placeholder(R.drawable.placeholder)
            .into(binding.trackAlbumImagePlayer)
    }

    private fun dpToPx(view: View, dp: Float): Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            .toInt()
    }
}