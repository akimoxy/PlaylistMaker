package com.example.playlistmaker.mediateka.ui.playlist

import THIRTY
import TRACK
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import com.example.playlistmaker.mediateka.ui.playlists.PLAYLIST
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

const val TWENTY = 20

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    var playlistId: Int = -1
    private val viewModel by viewModel<PlaylistViewModel>()
    lateinit var adapter: PlaylistBttmShtAdapter
    private var rvPlaylist: RecyclerView? = null
    private val clickListener: RVonItemClick = clickListenerFun()
    private val longClickListnr: OnLongClickListnr = longClickListnr()


    var tracks: ArrayList<Track> = arrayListOf()
    lateinit var timing: String
    lateinit var countOfTracks: String
    lateinit var playlist: PlaylistsModel
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonPlaylistFr.setOnClickListener { findNavController().navigateUp() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val arg = requireArguments()
        val json = getSerializableExtraCompat(arg, PLAYLIST, String::class.java)
        playlistId = Json.decodeFromString(json!!)
        viewModel.playlist(playlistId)
        viewModel.observeState().observe(viewLifecycleOwner) { it ->
            when (it) {
                is PlaylistState.Playlist -> {
                    playlist = it.playlists!!

                    if (playlist.playlistName!!.length > TWENTY) playlist.playlistName =
                        playlist.playlistName!!.substring(0, TWENTY) + ("...")
                    if (playlist.playlistDescription!!.length > THIRTY) playlist.playlistDescription =
                        playlist.playlistDescription!!.substring(0, THIRTY) + ("...")
                    Glide.with(binding.bigIvPlaylistFr)
                        .load(playlist.imageStorageLink)
                        .centerCrop()
                        .into(binding.bigIvPlaylistFr)
                    if (playlist.imageStorageLink == null) {
                        Glide.with(binding.imageViewPlaylistPlaceholder)
                            .load((R.drawable.playlist_placeholder_layer_list))
                            .centerCrop()
                            .into(binding.imageViewPlaylistPlaceholder)
                    }
                    binding.playlistsNameBigText.text = playlist.playlistName
                    binding.playlistsFDescription.text = playlist.playlistDescription

                    countOfTracks = playlist.countOfTracks.toString()

                    val pluralCount = resources.getQuantityString(
                        R.plurals.plurals_tracks, countOfTracks!!.toInt()
                    )
                    val count = "$countOfTracks $pluralCount"
                    binding.tvAllTracks.text = count
                    tracks.clear()
                    adapter = PlaylistBttmShtAdapter(tracks, clickListener, longClickListnr)

                    rvPlaylist = binding.rvPlaylistFragment
                    binding.rvPlaylistFragment.adapter = adapter
                    binding.rvPlaylistFragment.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    Log.d("Adapterr before clear", tracks.size.toString())
                    tracks.clear()
                    Log.d("after  clear", tracks.size.toString())
                    tracks.addAll(it.tracks!!)
                    Log.d("Adapter1", tracks.size.toString())
                    adapter.updateList(tracks)
                    Log.d("Adapter22", tracks.size.toString())


                    timing = it.tracksTiming.toString()
                    val plural = resources.getQuantityString(
                        R.plurals.plurals, it.tracksTiming!!.toInt()
                    )
                    val time = it.tracksTiming.toString() + " " + plural
                    binding.tvAllTimePlaylist.text = time
                }
            }
        }

        binding.sharePlaylistFr.setOnClickListener {
            share()
        }
        val bottomSheetBehaviorThreeDot =
            BottomSheetBehavior.from(binding.plFrBottomSheetThreeDot).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        binding.threeDotPlaylistFrIv.setOnClickListener {
            bottomSheetBehaviorThreeDot.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehaviorThreeDot.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlayPlaylist.visibility = View.GONE
                    }

                    else -> {
                        binding.overlayPlaylist.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        binding.sharePlaylistFrThreeDot.setOnClickListener { share() }
        binding.tvDeletePlaylist.setOnClickListener {
            bottomSheetBehaviorThreeDot.state = BottomSheetBehavior.STATE_HIDDEN


            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.delete_track_question))
                .setNeutralButton(requireContext().getString(R.string.no)) { _, which ->

                }
                .setPositiveButton(requireContext().getString(R.string.yes)) { _, which ->
                    viewModel.deletePlaylistModel(playlist)
                    findNavController().navigateUp()

                }
                .show()
        }

}

private fun clickListenerFun() = object : RVonItemClick {
    @SuppressLint("SuspiciousIndentation")
    override fun onItemClick(track: Track) {
        val json = Json.encodeToString(track)
        var bundle = bundleOf(TRACK to json)
        findNavController().navigate(
            R.id.action_playlistFragment_to_audioPlayerFragment,
            bundle
        )
    }
}

private fun longClickListnr() = object : OnLongClickListnr {
    @SuppressLint("SuspiciousIndentation")
    override fun onItemClick(track: Track, arrayOfTrack: ArrayList<Track>) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.delete_track))
            .setMessage(requireContext().getString(R.string.delete_track_question))
            .setNeutralButton(requireContext().getString(R.string.cancel)) { _, which ->
            }
            .setPositiveButton(requireContext().getString(R.string.delete)) { _, which ->
                viewModel.deleteTrack(track.trackId!!)
                tracks.remove(track)
                adapter.updateList(tracks)
                val plural = resources.getQuantityString(
                    R.plurals.plurals, timing.toInt()
                )
                var time = timing.toInt() - track.trackTimeMillis!! / 60000
                binding.tvAllTimePlaylist.text = "$time $plural"
                val countInt = countOfTracks.toInt() - 1
                val pluralCount = resources.getQuantityString(
                    R.plurals.plurals_tracks, countInt
                )
                val count = "$countInt $pluralCount"
                binding.tvAllTracks.text = count

            }
            .show()
    }
}

fun share() {
    if (countOfTracks.toInt() > 0) {
        viewModel.share(playlist)

    } else {
        Toast.makeText(
            requireContext(),
            requireActivity().getString(R.string.dont_have_a_tracks),
            Toast.LENGTH_SHORT
        ).show()
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