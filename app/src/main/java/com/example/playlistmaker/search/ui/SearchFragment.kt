package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.AudioPlayerActivity
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.TRACK
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY = "someKey"

class SearchFragment : Fragment() {
    private var text = ""
    lateinit var trackAdapter: TrackAdapter
    private lateinit var adapterForHistoryTracks: TrackAdapter
    private lateinit var clickListener: RecyclerViewEvent
    private val viewModel by viewModel<SearchViewModel>()
    private var arrayList: ArrayList<Track> = arrayListOf()
    private var arrayListHistory: ArrayList<Track> = arrayListOf()
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

        allViewGone()

        clickListener = object : RecyclerViewEvent {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemClick(track: Track) {
                if(viewModel.clickDebounce()){
                viewModel.saveTrackToHistory(track)
                val buttonSearchIntent =
                    Intent(requireContext(), AudioPlayerActivity::class.java)
                val json = Json.encodeToString(track)
                buttonSearchIntent.putExtra(TRACK, json)
                startActivity(buttonSearchIntent)
            }  }
        }

        adapterForHistoryTracks =
            TrackAdapter(viewModel.getHistoryItems(), clickListener)
        binding.recyclerViewHist.adapter = adapterForHistoryTracks
        binding.recyclerViewHist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackAdapter = TrackAdapter(arrayList, clickListener)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter


        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.inputEditText.hideKeyboard()
                showView()
                arrayList.clear()
                trackAdapter.addTracks(arrayList)
                viewModel.searchDebounce(binding.inputEditText.text.toString())
                trackAdapter.updateList(arrayList)
            }
            false
        }


        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.inputEditText.showKeyboard()
            binding.recyclerViewHist.visibility = View.VISIBLE
            adapterForHistoryTracks.updateList(viewModel.getHistoryItems())
            trackAdapter.updateList(arrayList)
            if (hasFocus && arrayList.isEmpty() && viewModel.getHistoryItems().isNotEmpty()
            ) {
                showHistoryView()
            } else if ((arrayList.isEmpty() && viewModel.getHistoryItems()
                    .isEmpty())
            ) {
                binding.textViewYourSearch.visibility = View.INVISIBLE
                binding.clearTrackHistory.visibility = View.GONE
                binding.textViewYourSearch.visibility = View.GONE
            }
        }

        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("WrongConstant")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text = p0.toString()
                binding.clearIconSearch.visibility = clearButtonVisibility(p0)
                if (binding.inputEditText.text.isNotEmpty()) {
                    arrayList.clear()
                    viewModel.searchDebounce(binding.inputEditText.text.toString())
                    trackAdapter.updateList(arrayList)
                }
                if (binding.inputEditText.hasFocus() && binding.inputEditText.text.isEmpty()) {
                    arrayList.clear()
                    trackAdapter.updateList(arrayList)
                    viewModel.getHistoryItems()
                    adapterForHistoryTracks.updateList(
                        viewModel.getHistoryItems()
                    )
                    binding.recyclerViewHist.visibility = View.VISIBLE
                }
                if (viewModel.getHistoryItems().isNotEmpty()) {
                    showHistoryView()
                } else {
                    arrayList.clear()
                    trackAdapter.updateList(arrayList)
                    binding.clearTrackHistory.visibility = View.GONE
                    binding.textViewYourSearch.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                adapterForHistoryTracks.updateList(viewModel.getHistoryItems())
                trackAdapter.updateList(arrayList)
                if (binding.inputEditText.hasFocus() && arrayList.isEmpty() && viewModel.getHistoryItems()
                        .isNotEmpty()
                ) {
                    showHistoryView()
                } else {
                    binding.clearTrackHistory.visibility = View.GONE
                    binding.textViewYourSearch.visibility = View.GONE
                }
            }
        })
        binding.clearIconSearch.setOnClickListener {
            binding.inputEditText.setText("")
            arrayList.clear()
            trackAdapter.updateList(arrayList)
            binding.recyclerView.visibility = View.GONE
            binding.progressBarSearchActivity.visibility = View.GONE
            binding.serverErrorInclude.root.visibility = View.GONE
            binding.noResultsSearchInclude.root.visibility = View.GONE
        }
        binding.recyclerViewHist.adapter = adapterForHistoryTracks
        binding.clearTrackHistory.setOnClickListener {
            viewModel.clearTrackHistory()
            viewModel.getHistoryItems()
            adapterForHistoryTracks.updateList(viewModel.getHistoryItems())
            binding.clearTrackHistory.visibility = View.GONE
            binding.textViewYourSearch.visibility = View.GONE
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

    private fun serverError() {
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
        binding.serverErrorInclude.root.visibility = View.VISIBLE
        binding.serverErrorInclude.updateSearchServerError.setOnClickListener {
            viewModel.searchDebounce(binding.inputEditText.text.toString())
        }
    }

    private fun allViewGone() {
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.recyclerViewHist.visibility = View.GONE
        binding.textViewYourSearch.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.textViewYourSearch.visibility = View.GONE
    }

    private fun loading() {

        binding.textViewYourSearch.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.GONE
        binding.recyclerViewHist.visibility = View.GONE
        binding.clearTrackHistory.visibility = View.GONE
        binding.progressBarSearchActivity.visibility = View.VISIBLE
        binding.serverErrorInclude.root.visibility = View.GONE
    }

    private fun showHistoryView() {
        binding.clearTrackHistory.visibility = View.VISIBLE
        binding.textViewYourSearch.visibility = View.VISIBLE
    }

    private fun noResults() {
        binding.progressBarSearchActivity.visibility = View.GONE
        binding.textViewYourSearch.visibility = View.GONE
        binding.noResultsSearchInclude.root.visibility = View.VISIBLE
    }

    private fun View.hideKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun View.showKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }

    private fun showView() {
        viewModel.activityStateLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is SearchActivityState.SearchTracks -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    arrayList = it.history as ArrayList<Track>
                    trackAdapter.updateList(arrayList)
                    binding.textViewYourSearch.visibility = View.GONE
                    binding.clearTrackHistory.visibility = View.GONE
                    binding.progressBarSearchActivity.visibility = View.GONE
                }

                is SearchActivityState.SearchHistory -> {
                    arrayListHistory = it.history
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

                else -> {}
            }
        }
    }
}