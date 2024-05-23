package com.example.playlistmaker.mediateka.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayListsFragment : Fragment() {

    private var _binding:  FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}