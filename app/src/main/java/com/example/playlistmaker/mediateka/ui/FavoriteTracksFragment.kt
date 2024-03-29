package com.example.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritTracksBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoritTracksBinding

    companion object {
        private const val FAVORITE_TRACKS = "favorite"
        fun newInstance(text: String) = FavoriteTracksFragment().apply {
            arguments = Bundle().apply {
                putString(FAVORITE_TRACKS, text)
            }
        }
    }

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel {
        parametersOf(requireArguments().getString(FAVORITE_TRACKS))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediatekaTextViewFavorite.text =
            requireArguments().getString(FAVORITE_TRACKS).toString()
        //  favoriteTracksViewModel.observeFavoriteTracks().observe(viewLifecycleOwner) {
        //   }
    }
}