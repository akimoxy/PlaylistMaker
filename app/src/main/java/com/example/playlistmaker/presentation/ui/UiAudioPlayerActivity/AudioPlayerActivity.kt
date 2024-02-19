package com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.MediaPlayerState
import com.example.playlistmaker.domain.models.MediaPlayerState.STATE_PAUSED
import com.example.playlistmaker.domain.models.MediaPlayerState.STATE_PLAYING
import com.example.playlistmaker.domain.models.MediaPlayerState.STATE_PREPARED
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.UiSearchActivity.SearchActivity.Companion.TRACK
import kotlinx.coroutines.Runnable
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.Locale

private const val DELAY_1SEC = 1000L

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var mainThreadHandler: Handler
    private lateinit var track: Track
    private lateinit var timing: Runnable
    private val playerInteractor = Creator.provideMediaPlayerInteractor()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainThreadHandler = Handler(Looper.getMainLooper())

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonAudioPlayer.setOnClickListener {
            finish()
        }
        val json = getSerializable(intent, TRACK, String::class.java)

        track = Json.decodeFromString(json)
        if (track.releaseDate.length > 4) track.releaseDate = track.releaseDate.substring(0, 4)
        track.artworkUrl100 = track.getCoverArtwork()
        binding.genreTextPlayer.text = track.primaryGenreName
        binding.yearTextPlayer.text = track.releaseDate
        binding.collectionNamePlayer.text = track.collectionName
        binding.countryTextPlayer.text = track.country
        binding.albumBigTextPlayer.text = track.trackName
        binding.trackArtistPlayer.text = track.artistName
        binding.trackTiming.text = getString(R.string.start_timing_mm_ss)
        binding.trackTimeMediapl.text = dateFormat.format(track.trackTimeMillis)

        Glide.with(binding.trackAlbumImagePlayer)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(binding.trackAlbumImagePlayer, 12f)))
            .placeholder(R.drawable.placeholder)
            .into(binding.trackAlbumImagePlayer)

        preparePlayer()

        playerInteractor.getMediaPlayerState()

        timing = timer()

        binding.playIcon.setOnClickListener {
            playbackControl(playerInteractor.getMediaPlayerState())
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.mediaPlayerRelease()
    }

    private fun startPlayer() {
        playerInteractor.startMediaPlayer()
        binding.playIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.pause_button_day, theme
            )
        )
        playerInteractor.initPlayerState(STATE_PLAYING)
        mainThreadHandler.postDelayed(timing, DELAY_1SEC)
    }

    private fun pausePlayer() {
        playerInteractor.pauseMediaPlayer()
        binding.playIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.play_btn_day, theme
            )
        )
        playerInteractor.initPlayerState(STATE_PAUSED)
        mainThreadHandler.removeCallbacks(timing)
    }

    private fun playbackControl(playerState: MediaPlayerState) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }

            else -> {
                preparePlayer()
            }
        }
    }

    private fun timer(): Runnable {
        return object : Runnable {
            override fun run() {
                val time = dateFormat.format(playerInteractor.getCurrentPosition())
                binding.trackTiming.text = time
                mainThreadHandler.postDelayed(this, DELAY_1SEC)
            }
        }
    }

    private fun dpToPx(view: View, dp: Float): Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            .toInt()
    }

    private fun preparePlayer() {
        binding.playIcon.visibility = View.VISIBLE
        playerInteractor.prepareMediaPlayer(track.previewUrl)
        playerInteractor.initMediaPlayer().setOnCompletionListener {
            playerInteractor.initPlayerState(STATE_PREPARED)
            binding.playIcon.isEnabled = true
        }
        playerInteractor.initMediaPlayer().setOnPreparedListener {
            binding.playIcon.isEnabled = true
            playerInteractor.initPlayerState(STATE_PREPARED)
        }
        playerInteractor.initMediaPlayer().setOnCompletionListener {
            mainThreadHandler.removeCallbacks(timing)
            playerInteractor.initPlayerState(STATE_PREPARED)
            binding.trackTiming.text = getString(R.string.start_timing_mm_ss)
            binding.playIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.play_btn_day, theme
                )
            )
        }
    }

    private fun <T : Serializable?> getSerializable(
        intent: Intent,
        key: String,
        className: Class<T>
    ): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(key, className)!!
        else
            intent.getSerializableExtra(key) as T
    }
}