package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.AudioPlayerActivity
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.TRACK
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var text = ""
    lateinit var trackAdapter: TrackAdapter
    private lateinit var adapterForHistoryTracks: TrackAdapter
    private lateinit var clickListener: RecyclerViewEvent
    private val viewModel by viewModel<SearchViewModel>()
    private var arrayList: ArrayList<Track> = arrayListOf()
    lateinit var resultArrayList: ArrayList<Track>
    private var arrayListHistory: ArrayList<Track> = arrayListOf()
    private val emptyArray: ArrayList<Track> = arrayListOf()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        if (savedInstanceState != null) {
            text = savedInstanceState.getString(KEY)!!
            binding.inputEditText.setText(text)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener = clickListenerFun()
        viewModel.updateState(SearchActivityState.NoTextOrFocusState)
        showView()

        adapterForHistoryTracks = TrackAdapter(viewModel.getHistoryItems(), clickListener)
        binding.recyclerViewHist.adapter = adapterForHistoryTracks
        binding.recyclerViewHist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        trackAdapter = TrackAdapter(arrayList, clickListener)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter

        binding.clearIconSearch.setOnClickListener {
            binding.inputEditText.text.clear()
            arrayList.clear()
            trackAdapter.updateList(arrayList)
            viewModel.setHistory()
            showView()
        }
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.inputEditText.showKeyboard()
                viewModel.setHistory()
                showView()
            }
        }
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.inputEditText.hideKeyboard()
            }
            false
        }
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.clearIconSearch.isVisible = p0.isNullOrEmpty().not()
                if (!p0.isNullOrEmpty()) {
                    viewModel.searchDebounce(p0.toString())
                } else if (p0.isNullOrEmpty() && viewModel.getHistoryItems().isEmpty()) {
                    arrayList.clear()
                    trackAdapter.updateList(arrayList)
                    viewModel.updateState(SearchActivityState.NoTextOrFocusState)
                    showView()
                } else {
                   // viewModel.setHistory()
                  //  showView()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.inputEditText.hasFocus() && viewModel.getHistoryItems().isEmpty()) {
                    viewModel.updateState(SearchActivityState.NoTextOrFocusState)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    private fun showView() {
        viewModel.activityStateLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is SearchActivityState.SearchTracks -> {
                    arrayList.clear()
                    resultArrayList = it.searchTracks as ArrayList<Track>
                    arrayList.addAll(resultArrayList)
                    trackAdapter.updateList(arrayList)
                    searchTracks()
                }

                is SearchActivityState.SearchHistory -> {
                    arrayListHistory.clear()
                    arrayListHistory.addAll(viewModel.getHistoryItems())
                    adapterForHistoryTracks.updateList(arrayListHistory)
                    showHistoryView()
                }

                is SearchActivityState.Loading -> {
                    loading()
                }

                is SearchActivityState.NoResult -> {
                    noResults()
                }

                is SearchActivityState.ServerError -> {
                    serverError()
                }

                is SearchActivityState.NoTextOrFocusState -> {
                    noTextOrFocusView()
                }

                else -> {}
            }

        }
    }

    private fun View.showKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }

    private fun View.hideKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun clickListenerFun() = object : RecyclerViewEvent {
        @SuppressLint("SuspiciousIndentation")
        override fun onItemClick(track: Track) {
            if (viewModel.clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                val buttonSearchIntent =
                    Intent(requireContext(), AudioPlayerActivity::class.java)
                val json = Json.encodeToString(track)
                buttonSearchIntent.putExtra(TRACK, json)
                startActivity(buttonSearchIntent)
            }
        }
    }

    private fun searchTracks() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.textViewYourSearch.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.GONE
        binding.progressBarSearchActivity.visibility = View.GONE
    }

    private fun showHistoryView() {
        binding.clearTrackHistory.visibility = View.VISIBLE
        binding.textViewYourSearch.visibility = View.VISIBLE
        binding.recyclerViewHist.visibility = View.VISIBLE
        binding.clearTrackHistory.setOnClickListener {
            arrayListHistory.clear()
            viewModel.clearTrackHistory()
            adapterForHistoryTracks.updateList(arrayListHistory)
            viewModel.updateState(SearchActivityState.NoTextOrFocusState)
        }
        binding.recyclerView.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.GONE
        binding.progressBarSearchActivity.visibility = View.GONE
    }

    private fun loading() {
        binding.progressBarSearchActivity.visibility = View.VISIBLE
        binding.textViewYourSearch.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
        binding.recyclerViewHist.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.GONE
    }

    private fun serverError() {
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.textViewYourSearch.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.recyclerViewHist.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.VISIBLE
        binding.serverErrorInclude.updateSearchServerError.setOnClickListener {
            viewModel.searchDebounce(binding.inputEditText.text.toString())
            showView()
        }
    }

    private fun noResults() {
        binding.noResultsSearchInclude.root.visibility = View.VISIBLE
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.textViewYourSearch.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.recyclerViewHist.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.GONE
    }

    private fun noTextOrFocusView() {
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.recyclerViewHist.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.textViewYourSearch.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
    }

}