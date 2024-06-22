package com.example.playlistmaker.mediateka.ui.updatePlaylist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateka.ui.newPlaylists.MY_PLAYLIST_IMAGE
import com.example.playlistmaker.mediateka.ui.newPlaylists.NewPlaylistFragment
import com.example.playlistmaker.mediateka.ui.playlist.PLAYLIST_TO_UPDATE
import com.example.playlistmaker.mediateka.ui.playlist.getSerializableExtraCompat
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class UpdPlaylistFragment : NewPlaylistFragment() {
    override val viewModel by viewModel<UpdatePlaylistViewModel>()
    override var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.imageViewPlaylistPlaceholder.setImageURI(uri)
                saveImageToPrivateStorage(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextInTitleAndButton()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback())
        binding.backButtonNewPlaylist.setOnClickListener { findNavController().navigateUp() }
        imageViewPlaylistPlaceholder()
        buttonSaveUpdPlaylist()
        val arg = requireArguments()
        val json = getSerializableExtraCompat(arg, PLAYLIST_TO_UPDATE, String::class.java)
        var playlistId: String = Json.decodeFromString(json!!)
        viewModel.playlistThis(playlistId.toInt())

        binding.imageViewPlaylistPlaceholder.setOnClickListener { view ->
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        viewModel.observe().observe(viewLifecycleOwner) { it ->
            when (it) {
                is UpdPlaylistScreenState.Playlist -> {
                    binding.edittextPlaylistName.setText(it.playlistsModel.playlistName)
                    binding.descriptionNewPlaylistEdittext.setText(it.playlistsModel.playlistDescription)
                    Glide.with(binding.imageViewPlaylistPlaceholder)
                        .load(viewModel.plM.imageStorageLink)
                        .centerCrop()
                        .placeholder(R.drawable.playlist_placeholder_layer_list)
                        .into(binding.imageViewPlaylistPlaceholder)
                }
            }
        }
    }

    private fun setTextInTitleAndButton() {
        binding.buttonCreatePlaylist.text = requireActivity().getString(R.string.save)
        binding.newPlaylistTitle.text =
            requireActivity().getString(R.string.edit)
    }

    private fun callback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback().remove()
    }

    private fun buttonSaveUpdPlaylist() {
        binding.buttonCreatePlaylist.setOnClickListener {
            if (binding.edittextPlaylistName.text.isNotEmpty()) {
                viewModel.plM.playlistName = binding.edittextPlaylistName.text.toString()
                viewModel.plM.playlistDescription =
                    binding.descriptionNewPlaylistEdittext.text.toString()
                viewModel.updatePlaylist(viewModel.plM)
                findNavController().navigateUp()
            }
        }
    }

    override fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            MY_PLAYLIST_IMAGE
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val imageName = SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(Date()) + ".jpg"
        viewModel.plM.playlistsImageName = imageName
        val file = File(filePath, imageName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    override fun imageViewPlaylistPlaceholder() {
        binding.imageViewPlaylistPlaceholder.setOnClickListener { view ->
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}
