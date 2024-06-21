import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.player.ui.PlayerBottomSheetAdapter
import com.example.playlistmaker.player.ui.PlayerBottomSheetState
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.RVEvent
import com.example.playlistmaker.player.ui.ScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.util.Locale


const val FOUR = 4
const val THIRTY = 30
const val TRACK = "track"

class AudioPlayerFragment : Fragment() {
    private lateinit var _binding: FragmentAudioPlayerBinding
    private val binding get() = _binding!!
    private lateinit var track: Track
    private lateinit var clickListener: RVEvent
    private val playerViewModel by viewModel<PlayerViewModel>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val viewModelTheme by viewModel<SettingsViewModel>()
    lateinit var adapter: PlayerBottomSheetAdapter
    private val playLists = ArrayList<PlaylistsModel>()
    private var recyclerViewPlaylists: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonAudioPlayer.setOnClickListener {
            findNavController().navigateUp()
        }
        val arg = requireArguments()
        val json = getSerializableExtraCompat(arg, TRACK, String::class.java)
        track = Json.decodeFromString(json!!)
        if (track.releaseDate!!.length > FOUR) track.releaseDate =
            track.releaseDate!!.substring(0, FOUR)
        if (track.artistName!!.length > THIRTY) track.artistName =
            track.artistName!!.substring(0, THIRTY) + ("...")
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
        clickListener = clickListenerFun()
        adapter = PlayerBottomSheetAdapter(playLists, clickListener)
        recyclerViewPlaylists = binding.rvPlayerPlaylists
        binding.rvPlayerPlaylists.adapter = adapter

        binding.rvPlayerPlaylists.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.likeButton.setOnClickListener {
            if (track.isFavorite!!) {
                playerViewModel.deleteFromFavTrack(track)
            } else {
                playerViewModel.addToFavTracks(track)
            }
        }


        playerViewModel.mediatorLiveDataObserver.observe(viewLifecycleOwner) { pair ->
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
                            R.drawable.play_btn_day,
                            requireActivity().theme
                        )
                    )
                }

                ScreenState.Prepare -> {
                    binding.trackTiming.text = getString(R.string.start_timing_mm_ss)
                    binding.playIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources, R.drawable.play_btn_day, requireActivity().theme
                        )
                    )
                }

                is ScreenState.PlayingTime -> {
                    binding.trackTiming.text = screenState.time
                    binding.playIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.pause_button_day,
                            requireActivity().theme
                        )
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

        binding.playIcon.setOnClickListener {
            playbackControl()
        }

        binding.newPlaylistBtnPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylist)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.addToPlaylistBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


            playerViewModel.observeState().observe(viewLifecycleOwner) {
                adapter.updateList(playLists)
                when (it) {
                    is PlayerBottomSheetState.PlaylistsBottomSheet -> {
                        binding.rvPlayerPlaylists.visibility = View.VISIBLE
                        playLists.clear()
                        playLists.addAll(playerViewModel.playlistsList)
                        adapter.updateList(playLists)
                    }

                    is PlayerBottomSheetState.Empty -> {
                        playLists.clear()
                        adapter.updateList(playLists)

                    }

                    is PlayerBottomSheetState.AddToPlaylist -> {
                        val playlistsModel = it.playlistsModel
                        val addToPlaylist: Boolean = it.boolean
                        if (!addToPlaylist) {
                            Toast.makeText(
                                requireContext(),
                                " Трек уже добавлен в плейлист  « ${playlistsModel.playlistName} » ",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                " Добавлено в плейлист « ${playlistsModel.playlistName} » ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {}
                }
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
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
        playerViewModel.preparePlayer(track.previewUrl!!, track)
        binding.playIcon.isEnabled = true
        binding.trackTiming.text = getString(R.string.start_timing_mm_ss)

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun likeView() {
        if (viewModelTheme.firstInitTheme()) {
            binding.likeButton.setImageDrawable(requireActivity().getDrawable(R.drawable.button_like_night_red))
        } else {
            binding.likeButton.setImageDrawable(requireActivity().getDrawable(R.drawable.button_like_day_red))
        }
    }

    private fun dontLikeView() {
        if (viewModelTheme.firstInitTheme()) {
            binding.likeButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.btn_like_night_playlist,
                    requireActivity().theme
                )
            )
        } else {
            binding.likeButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.btn_like_day_playlist,
                    requireActivity().theme
                )
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

    private fun clickListenerFun() = object : RVEvent {
        @SuppressLint("SuspiciousIndentation")
        override fun onItemClick(playlistsModel: PlaylistsModel) {
            playerViewModel.addTrackToPlaylist(track, playlistsModel)
        }
    }
}

private fun <T : Serializable?> getSerializableExtraCompat(
    bundle: Bundle,
    key: String,
    className: Class<T>
): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        bundle.getSerializable(key, className) as T
    else
        bundle.getSerializable(key) as T
}
