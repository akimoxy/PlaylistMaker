package com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.ScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.ui.SettingsViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.util.Locale


const val FOUR = 4
const val THIRTY = 30
const val TRACK = "track"

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var track: Track
    private val playerViewModel by viewModel<PlayerViewModel>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val viewModelTheme by viewModel<SettingsViewModel>()
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
        if (track.artistName.length > THIRTY) track.artistName =
            track.artistName.substring(0, THIRTY) + ("...")
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


        playerViewModel.mediatorLiveDataObserver.observe(this@AudioPlayerActivity) { pair ->
            val screenState = pair.first
            val isFavorite = pair.second
            when (screenState) {
                is ScreenState.Default -> {
                    preparePlayer()
                }

                is ScreenState.Pause -> {
                    binding.trackTiming.text = screenState.time
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
                    binding.trackTiming.text = screenState.time
                    binding.playIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(resources, R.drawable.pause_button_day, theme)
                    )
                }

                else -> {
                    preparePlayer()
                }
            }
            track.isFavorite = isFavorite!!
            if (isFavorite) {
                likeView()
            } else {
                dontLikeView()
            }
        }


        binding.likeButton.setOnClickListener {
            if (track.isFavorite) {
                playerViewModel.deleteFromFavTrack(track)
            } else {
                playerViewModel.addToFavTracks(track)
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
        playerViewModel.preparePlayer(track.previewUrl, track)
        binding.playIcon.isEnabled = true
        binding.trackTiming.text = getString(R.string.start_timing_mm_ss)

    }

    private fun likeView() {
        if (viewModelTheme.firstInitTheme()) {
            binding.likeButton.setImageDrawable(getDrawable(R.drawable.button_like_night_red))
        } else {
            binding.likeButton.setImageDrawable(getDrawable(R.drawable.button_like_day_red))
        }
    }

    private fun dontLikeView() {
        if (viewModelTheme.firstInitTheme()) {
            binding.likeButton.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.btn_like_night_playlist, theme)
            )
        } else {
            binding.likeButton.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.btn_like_day_playlist, theme)
            )
        }
    }

    private fun playbackControl() {
        when (playerViewModel.mediatorLiveDataObserver.value!!.first) {
            is ScreenState.Default -> {
                preparePlayer()

            }

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
