package com.example.playlistmaker.mediateka.ui.newPlaylists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.mediateka.domain.models.PlaylistsModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Date

const val MY_PLAYLIST_IMAGE = "myPlaylistImage"

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    var namePlaylist = ""
    var descriptionPlaylist = ""
    var nameImage = ""
    private var imageIsNotEmpty = false
    private val viewModel by viewModel<NewPlaylistViewModel>()
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showDialog()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonNewPlaylist.setOnClickListener { showDialog() }
        viewModel!!.observeState()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        //прошу прощения, за невнимательность в прочтении задания (◕‿◕)

        playlist()
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.imageViewPlaylistPlaceholder.setImageURI(uri)
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.descriptionNewPlaylistEdittext.setOnFocusChangeListener { _, _ ->
        }
        binding.edittextPlaylistName.setOnFocusChangeListener { view, hasFocus ->
            if (binding.edittextPlaylistName.text.isNotEmpty()) {
                binding.buttonCreatePlaylist.isEnabled
            }
        }
        binding.buttonCreatePlaylist.setOnClickListener {
            if (binding.edittextPlaylistName.text.isNotEmpty()) {
                playlist()
                viewModel.createPlaylist(viewModel.playlist)
                Toast.makeText(
                    requireContext(),
                    " «Плейлист $namePlaylist создан»",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
        binding.edittextPlaylistName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel!!.changeState(NewPlaylistScreenState.NamePlaylists(binding.edittextPlaylistName.text.toString()))
                    if (p0.isNullOrEmpty().not()) {
                        viewModel.changeName(p0.toString())
                        binding.buttonCreatePlaylist.isEnabled = true
                    } else {
                        binding.buttonCreatePlaylist.isEnabled = false
                    }
                }
            })
        binding.descriptionNewPlaylistEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.changeDescription(p0.toString())
            }
        })
        binding.imageViewPlaylistPlaceholder.setOnClickListener { view ->
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            viewModel.imageIsNotEmpty(true)
        }
        binding.edittextPlaylistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.edittextPlaylistName.hideKeyboard()
            }
            false
        }
        binding.descriptionNewPlaylistEdittext.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.edittextPlaylistName.hideKeyboard()
            }
            false
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            MY_PLAYLIST_IMAGE
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val imageName = SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(Date()) + ".jpg"
        viewModel.saveImageName(imageName)
        val file = File(filePath, imageName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun playlist() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is NewPlaylistScreenState.NamePlaylists -> namePlaylist = it.name
                is NewPlaylistScreenState.DescriptionPlaylists -> descriptionPlaylist =
                    it.description

                is NewPlaylistScreenState.ImageName -> nameImage = it.name
                is NewPlaylistScreenState.ImageIsNotEmpty -> imageIsNotEmpty = it.boolean
                is NewPlaylistScreenState.Empty -> {}
            }
            viewModel.playlist = PlaylistsModel(
                playlistId = null,
                playlistName = namePlaylist,
                playlistDescription = descriptionPlaylist,
                imageStorageLink = viewModel.uri,
                playlistsImageName = nameImage,
                tracksId = arrayListOf(),
                countOfTracks = 0
            )
        }
    }

    private fun showDialog() {
        if (binding.edittextPlaylistName.text.isNotEmpty() || binding.descriptionNewPlaylistEdittext.text.isNotEmpty() || imageIsNotEmpty) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.end_create_new_playlist))
                .setMessage(requireContext().getString(R.string.all_unsaved_data_will_be_lost))
                .setNeutralButton(requireContext().getString(R.string.cancel)) { _, which ->
                }
                .setPositiveButton(requireContext().getString(R.string.finished)) { _, which ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun View.hideKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}