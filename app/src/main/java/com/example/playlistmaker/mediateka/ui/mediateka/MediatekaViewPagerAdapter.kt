package com.example.playlistmaker.mediateka.ui.mediateka

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.mediateka.ui.favTracks.FavoriteTracksFragment
import com.example.playlistmaker.mediateka.ui.playlists.PlayListsFragment


class MediatekaViewPagerAdapter(
    fragment: FragmentManager, lifecycle: Lifecycle,
    private val textForMediatekaView: String,
    private val textForFavoriteView: String
) : FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteTracksFragment.newInstance(textForFavoriteView)
            else -> PlayListsFragment.newInstance(textForMediatekaView)
        }
    }
}