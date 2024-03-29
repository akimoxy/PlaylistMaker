package com.example.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayListsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding

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
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediatekaTextView.text = requireArguments().getString(PLAYLIST)
        //   playListsViewModel.observePlaylists().observe(viewLifecycleOwner) {
        // }
    }
}