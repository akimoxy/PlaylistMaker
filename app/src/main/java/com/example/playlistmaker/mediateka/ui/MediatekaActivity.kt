package com.example.playlistmaker.mediateka.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediatekaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediatekaActivity : AppCompatActivity() {
    lateinit var binding: ActivityMediatekaBinding
    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val textForMediateka = getString(R.string.mediateka_empty_text)
        val textForFavTracks = getString(R.string.mediateka_playlist)
        binding.backButtonMediateka.setOnClickListener { finish() }

        binding.viewPagerMediateka.adapter = MediatekaViewPagerAdapter(
            supportFragmentManager,
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

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}