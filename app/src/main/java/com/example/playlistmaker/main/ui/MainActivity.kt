package com.example.playlistmaker.main.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.mediateka.ui.MediatekaActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity
import com.example.playlistmaker.settings.ui.SettingsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]
        viewModel.firstInitTheme()

            binding.searchButtonMain.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.mediatekaButton.setOnClickListener {
            startActivity(Intent(this, MediatekaActivity::class.java))
        }

      binding.settingsButton.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
    }
}
