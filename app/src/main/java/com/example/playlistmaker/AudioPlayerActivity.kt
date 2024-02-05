package com.example.playlistmaker

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.TRACK
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import kotlinx.coroutines.Runnable
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null
    private lateinit var track: Track
    private lateinit var timing: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainThreadHandler = Handler(Looper.getMainLooper())

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonAudioPlayer.setOnClickListener {
            finish()
        }

        fun <T : Serializable?> getSerializable(
            intent: Intent,
            key: String,
            className: Class<T>
        ): T {
            return if (Build.VERSION.SDK_INT >= 33)
                intent.getSerializableExtra(key, className)!!
            else
                intent.getSerializableExtra(key) as T
        }

        val json = getSerializable(intent, TRACK, String::class.java)

        track = Json.decodeFromString(json)
        track.artworkUrl100 = track.getCoverArtwork()
        binding.genreTextPlayer.text = track.primaryGenreName
        binding.yearTextPlayer.text = track.releaseDate.substring(0, 4)
        binding.collectionNamePlayer.text = track.collectionName
        binding.countryTextPlayer.text = track.country
        binding.albumBigTextPlayer.text = track.trackName
        binding.trackArtistPlayer.text = track.artistName
        binding.trackTiming.text=getString(R.string.start_timing_mm_ss)
        binding.trackTimeMediapl.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(track.trackTimeMillis)

        Glide.with(binding.trackAlbumImagePlayer)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(binding.trackAlbumImagePlayer, 12f)))
            .placeholder(R.drawable.placeholder)
            .into(binding.trackAlbumImagePlayer)


        preparePlayer()
        timing = timer()
        binding.playIcon.setOnClickListener {
            playbackControl()
        }
        binding.pauseIcon.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playIcon.visibility = View.GONE
        binding.pauseIcon.visibility = View.VISIBLE
        playerState = STATE_PLAYING
        mainThreadHandler?.postDelayed(timing, DELAY_1SEC)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.pauseIcon.visibility = View.GONE
        playerState = STATE_PAUSED
        binding.playIcon.visibility = View.VISIBLE
        mainThreadHandler?.removeCallbacks(timing)

    }
    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun timer(): Runnable {
        return object : Runnable {
            override fun run() {
              val time =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                binding.trackTiming.text= time
                mainThreadHandler?.postDelayed(this, DELAY_1SEC)
            }
        }
    }

    private fun dpToPx(view: View, dp: Float): Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            .toInt()
    }
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_1SEC = 1000L
    }

    private fun preparePlayer() {
        binding.playIcon.visibility = View.VISIBLE
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playIcon.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(timing)
            playerState = STATE_PREPARED
            binding.trackTiming.text=getString(R.string.start_timing_mm_ss)
            binding.pauseIcon.visibility = View.GONE
            binding.playIcon.visibility = View.VISIBLE
        }
    }
}