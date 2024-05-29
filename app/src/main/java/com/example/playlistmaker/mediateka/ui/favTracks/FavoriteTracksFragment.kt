package com.example.playlistmaker.mediateka.ui.favTracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentFavoritTracksBinding
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.AudioPlayerActivity
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.TRACK
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    private var _binding: FragmentFavoritTracksBinding? = null
    private val binding get() = _binding!!
    private val favoriteTracksViewModel by viewModel<FavoriteTracksViewModel>()
    private var adapter: FavTracksAdapter? = null
    private var clickListenerFav: RecyclerViewFavOnItemClick? = null
    private var favTracksRV: RecyclerView? = null
    private val favoriteTrackList = ArrayList<Track>()

    companion object {
        private const val FAVORITE_TRACKS = "favorite"
        fun newInstance(text: String) = FavoriteTracksFragment().apply {
            arguments = Bundle().apply {
                putString(FAVORITE_TRACKS, text)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noResultsImageFavorite.visibility = View.GONE
        binding.mediatekaTextViewFavorite.visibility = View.GONE
        binding.mediatekaTextViewFavorite.text =
            requireArguments().getString(FAVORITE_TRACKS).toString()

        clickListenerFav = clickListener()

        adapter =
            FavTracksAdapter(favoriteTrackList, clickListenerFav!!)
        favTracksRV = binding.recyclerViewFavTracks


        binding.recyclerViewFavTracks.adapter = adapter
        binding.recyclerViewFavTracks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        favoriteTracksViewModel.observeState().observe(viewLifecycleOwner) {
            adapter!!.updateList(favoriteTrackList)
            when (it) {
                is FavTracksState.Content -> {
                    favoriteTrackList.clear()
                    favoriteTrackList.addAll(favoriteTracksViewModel.trackList)
                    adapter!!.updateList(favoriteTrackList)
                    binding.noResultsImageFavorite.visibility = View.GONE
                    binding.mediatekaTextViewFavorite.visibility = View.GONE
                }

                is FavTracksState.Empty -> {
                    favoriteTrackList.clear()
                    adapter!!.updateList(favoriteTrackList)
                    binding.recyclerViewFavTracks.visibility = View.GONE
                    binding.noResultsImageFavorite.visibility = View.VISIBLE
                    binding.mediatekaTextViewFavorite.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun clickListener() = object : RecyclerViewFavOnItemClick {
        override fun onItemClick(track: Track) {
            val favTracksIntent =
                Intent(requireContext(), AudioPlayerActivity::class.java)
            val json = Json.encodeToString(track)
            favTracksIntent.putExtra(TRACK, json)
            startActivity(favTracksIntent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}