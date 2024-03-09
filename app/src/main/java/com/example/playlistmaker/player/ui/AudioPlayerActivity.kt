package com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.ScreenState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.Locale

const val DELAY_1SEC = 1000L
const val FOUR = 4
const val TRACK = "track"

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var track: Track
    lateinit var playerViewModel: PlayerViewModel
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonAudioPlayer.setOnClickListener {
            finish()
        }

        val json = getSerializableExtraCompat(intent, TRACK, String::class.java)

        track = Json.decodeFromString(json)
        if (track.releaseDate.length > FOUR) track.releaseDate =
            track.releaseDate.substring(0, FOUR)
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

        playerViewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(track.previewUrl)
        )[PlayerViewModel::class.java]

        playerViewModel.observeState().observe(this)
        {
            when (it) {
                is ScreenState.Default -> {
                    preparePlayer()
                }

                is ScreenState.Pause -> {
                    binding.trackTiming.text = it.time
                    binding.playIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.play_btn_day, theme
                        )
                    )
                }

                ScreenState.Prepare -> {
                    binding.trackTiming.text = getString(R.string.start_timing_mm_ss)
                    binding.playIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources, R.drawable.play_btn_day, theme
                        )
                    )
                }

                is ScreenState.PlayingTime -> {
                    binding.trackTiming.text = it.time
                    binding.playIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(resources, R.drawable.pause_button_day, theme)
                    )
                }

                else -> { //
                }
            }
        }
        binding.playIcon.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.playerInteractor.mediaPlayerRelease()
    }

    private fun startPlayer() {
        playerViewModel.startPlayer()
    }

    private fun pausePlayer() {
        playerViewModel.pausePlayer()
    }

    private fun preparePlayer() {
        binding.playIcon.visibility = View.VISIBLE
        playerViewModel.preparePlayer()
        binding.playIcon.isEnabled = true
        binding.trackTiming.text = getString(R.string.start_timing_mm_ss)

    }

    private fun playbackControl() {
        when (playerViewModel.observeState().value) {
            is ScreenState.Default -> preparePlayer()
            is ScreenState.PlayingTime -> {
                pausePlayer()
            }

            is ScreenState.Prepare -> {
                startPlayer()
            }

            is ScreenState.Pause -> {
                startPlayer()
            }

            else -> {
                preparePlayer()
            }
        }
    }

    private fun dpToPx(view: View, dp: Float): Int {
        val displayMetrics = view.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
            .toInt()
    }

}

private fun <T : Serializable?> getSerializableExtraCompat(
    intent: Intent,
    key: String,
    className: Class<T>
): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        intent.getSerializableExtra(key, className)!!
    else
        intent.getSerializableExtra(key) as T
}
