package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
   // private lateinit var viewModel:
    private val viewModel by viewModel<SettingsViewModel>()

    @SuppressLint("UseSwitchCompatOrMaterialCode", "MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonSettings.setOnClickListener {
            finish()
        }

        binding.nightThemeSwitchSettings.isChecked = viewModel.firstInitTheme()
        binding.nightThemeSwitchSettings.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onSwitchClick(isChecked)
        }
        binding.shareButtonSettings.setOnClickListener {
            viewModel.shareApp()
        }

        binding.writeToSupportButton.setOnClickListener {
            viewModel.sendToSupport()
        }

        binding.termsOfUseButton.setOnClickListener {
            viewModel.termsOfUse()
        }
    }


}



