package com.example.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatekaBinding
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediatekaFragment : Fragment() {
    private lateinit var binding: FragmentMediatekaBinding
    private lateinit var tabMediator: TabLayoutMediator
    private val viewModelTheme by viewModel<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelTheme.firstInitTheme()
        val textForMediateka = getString(R.string.mediateka_empty_text)
        val textForFavTracks = getString(R.string.mediateka_playlist)

        binding.viewPagerMediateka.adapter = MediatekaViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            textForFavTracks,
            textForMediateka
        )
        tabMediator = TabLayoutMediator(
            binding.tabLayoutMediateka,
            binding.viewPagerMediateka
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.mediateka_favorite_tracks_tablayout)
                1 -> tab.text = getString(R.string.mediateka_playlists_tablayout)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        super.onDestroy()
        tabMediator.detach()
    }
}