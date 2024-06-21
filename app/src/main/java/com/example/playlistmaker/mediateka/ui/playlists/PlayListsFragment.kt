package com.example.playlistmaker.mediateka.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.mediateka.domain.model.PlaylistsModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val PLAYLIST = "playlist"

class PlayListsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: PlayListsAdapter
    private val playLists = ArrayList<PlaylistsModel>()
    private var recyclerViewPlaylists: RecyclerView? = null

    companion object {
        private const val PLAYLIST = "playlist"
        fun newInstance(textForFragment: String) = PlayListsFragment().apply {
            arguments = Bundle().apply {
                putString(PLAYLIST, textForFragment)
            }
        }
    }

    private val playListsViewModel: PlayListsViewModel by viewModel {
        parametersOf(requireArguments().getString(PLAYLIST))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediatekaTextView.text = requireArguments().getString(PLAYLIST)
        binding.newPlaylistBtnMediateka.setOnClickListener {
            findNavController().navigate(R.id.action_mediatekaFragment_to_newPlaylist)
        }
        adapter = PlayListsAdapter(playLists, clickListener())
        recyclerViewPlaylists = binding.recyclerViewPlaylists
        binding.recyclerViewPlaylists.adapter = adapter
        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)

        playListsViewModel.observeState().observe(viewLifecycleOwner) {
            adapter!!.updateList(playLists)
            when (it) {
                is PlayListsMediatekaState.Playlists -> {
                    binding.recyclerViewPlaylists.visibility = View.VISIBLE
                    playLists.clear()
                    playLists.addAll(playListsViewModel.playlistsList)
                    adapter.updateList(playLists)
                    binding.noResultsImagePlaylists.visibility = View.GONE
                    binding.mediatekaTextView.visibility = View.GONE
                }

                is PlayListsMediatekaState.Empty -> {
                    playLists.clear()
                    adapter.updateList(playLists)
                    binding.noResultsImagePlaylists.visibility = View.VISIBLE
                    binding.mediatekaTextView.visibility = View.VISIBLE
                    binding.recyclerViewPlaylists.visibility = View.GONE
                }
            }
        }
    }

    private fun clickListener() = object : OnPlaylistClick {
        override fun onItemClick(playList: PlaylistsModel) {
            val json = Json.encodeToString(playList.playlistId)
            var bundle = bundleOf(PLAYLIST to json)
            findNavController().navigate(R.id.action_mediatekaFragment_to_playlistFragment,bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}