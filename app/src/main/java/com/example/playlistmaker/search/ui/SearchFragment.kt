package com.example.playlistmaker.search.ui

import TRACK
import android.content.Context
import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var text = ""
    lateinit var trackAdapter: TrackAdapter
    private lateinit var adapterForHistoryTracks: TrackAdapter
    private val viewModel by viewModel<SearchViewModel>()
    private var listOfTracks: List<Track> = listOf()
    private var listHistory: List<Track> = listOf()
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
        viewModel.updateState(SearchActivityState.NoTextOrFocusState)
        viewModel.getState().observe(viewLifecycleOwner) { showView(it) }
        inputEditTextListener()
        initAdapterAndRecycler()



        binding.clearIconSearch.setOnClickListener {
            binding.inputEditText.text.clear()
            listOfTracks = emptyList()
            trackAdapter.updateList(listOfTracks)
            viewModel.setHistory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    private fun showView(it: SearchActivityState) {
        when (it) {
            is SearchActivityState.SearchTracks -> {
                searchTracks(it.searchTracks as ArrayList<Track>)
            }

            is SearchActivityState.SearchHistory -> {
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

    private fun clickListenerFun(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.saveTrackToHistory(track)
            val json = Json.encodeToString(track)
            var bundle = bundleOf(TRACK to json)
            findNavController().navigate(
                R.id.action_searchFragment_to_audioPlayerFragment,
                bundle
            )
        }
    }


    private fun searchTracks(it: ArrayList<Track>) {
        trackAdapter.updateList(it)
        viewVisibility(rV = true)
    }

    private fun showHistoryView() {
        listHistory = emptyList()
        adapterForHistoryTracks.updateList(viewModel.getHistoryItems())
        binding.clearTrackHistory.setOnClickListener {
            listHistory = emptyList()
            viewModel.clearTrackHistory()
            adapterForHistoryTracks.updateList(listHistory)
            viewModel.updateState(SearchActivityState.NoTextOrFocusState)
        }
        viewVisibility(
            rVHist = true,
            tVYourSearch = true,
            clearTrackHist = true
        )
    }

    private fun loading() {
        viewVisibility(progressBar = true)
    }

    private fun serverError() {
        viewVisibility(serverError = true)
        binding.serverErrorInclude.updateSearchServerError.setOnClickListener {
            viewModel.searchDebounce(binding.inputEditText.text.toString())
        }
    }

    private fun noResults() {
        viewVisibility(noResult = true)
    }

    private fun noTextOrFocusView() {
        viewVisibility()
    }

    private fun viewVisibility(
        progressBar: Boolean = false,
        rVHist: Boolean = false,
        rV: Boolean = false,
        tVYourSearch: Boolean = false,
        clearTrackHist: Boolean = false,
        serverError: Boolean = false,
        noResult: Boolean = false
    ) {
        with(binding) {
            progressBarSearchActivity.isVisible = progressBar
            if (progressBar) {
                recyclerView.smoothScrollToPosition(trackAdapter.itemCount)
            }
            recyclerViewHist.isVisible = rVHist
            recyclerView.isVisible = rV
            textViewYourSearch.isVisible = tVYourSearch
            clearTrackHistory.isVisible = clearTrackHist
            serverErrorInclude.root.isVisible = serverError
            noResultsSearchInclude.root.isVisible = noResult
        }
    }

    private fun inputEditTextListener() {
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.clearIconSearch.isVisible = p0.isNullOrEmpty().not()
                if (!p0.isNullOrEmpty()) {
                    viewModel.searchDebounce(p0.toString())
                } else if (p0.isNullOrEmpty() && viewModel.getHistoryItems().isEmpty()) {
                    listOfTracks = emptyList()
                    trackAdapter.updateList(listOfTracks)
                    viewModel.updateState(SearchActivityState.NoTextOrFocusState)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.inputEditText.hasFocus() && viewModel.getHistoryItems().isEmpty()) {
                    viewModel.updateState(SearchActivityState.NoTextOrFocusState)
                }
            }
        })
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.inputEditText.showKeyboard()
                viewModel.setHistory()
            }
        }
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.inputEditText.hideKeyboard()
            }
            false
        }
    }

    private fun initAdapterAndRecycler() {
        adapterForHistoryTracks = TrackAdapter(viewModel.getHistoryItems(), ::clickListenerFun)
        binding.recyclerViewHist.adapter = adapterForHistoryTracks
        binding.recyclerViewHist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        trackAdapter = TrackAdapter(listOfTracks, ::clickListenerFun)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter
    }

}