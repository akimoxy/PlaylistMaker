package com.example.playlistmaker.mediateka.ui.playlists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

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
        //   playListsViewModel.observePlaylists().observe(viewLifecycleOwner) {
        // }
        binding.newPlaylistBtnMediateka.setOnClickListener {
            findNavController().navigate(R.id.action_mediatekaFragment_to_newPlaylist)

        }



        adapter = PlayListsAdapter(playLists)
        recyclerViewPlaylists=binding.recyclerViewPlaylists
        binding.recyclerViewPlaylists.adapter=adapter

        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)



        playListsViewModel.observeState().observe(viewLifecycleOwner) {
            adapter!!.updateList(playLists)
            when (it) {
                is PlayListsMediatekaState.Playlists -> {
                    binding.recyclerViewPlaylists.visibility = View.VISIBLE
                    playLists.clear()
                    playLists.addAll(playListsViewModel.playlistsList)
                    Log.d("ololo", playListsViewModel.playlistsList.toString())
                    Log.d("22222", playLists.toString())
                    Log.d("22222", playLists[0].playlistName)
                    Log.d("22222", playLists[0].playlistDescription.toString())
              //      Log.d("22222", playLists[1].playlistName)
             //       Log.d("22222", playLists[1].playlistDescription.toString())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}