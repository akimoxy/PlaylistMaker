package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.AudioPlayerActivity
import com.example.playlistmaker.search.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class SearchActivity : AppCompatActivity() {
    private var text = ""
    private var errorView: View? = null
    private var noResultsView: View? = null
    private var textViewYourSearch: View? = null
    private var searchProgressBar: View? = null
    private var trackItem: View? = null
    private var upd: Button? = null
    private var clearTrackHistoryBtn: Button? = null
    private var rvTrackHist: RecyclerView? = null
    lateinit var trackAdapter: TrackAdapter
    private lateinit var adapterForHistoryTracks: TrackAdapter
    lateinit var binding: ActivitySearchBinding
    private lateinit var clickListener: RecyclerViewEvent
    private lateinit var trackInteractor: TrackInteractor
    lateinit var history: TrackHistoryInteractor
    lateinit var viewModel: SearchViewModel

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findBiId()
        allViewGone()
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        history = viewModel.getTrackHistoryInteractor()
        trackInteractor = viewModel.getTrackInteractor()

        clickListener = object : RecyclerViewEvent {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemClick(track: Track) {
                history.saveTrack(track)
                val buttonSearchIntent =
                    Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                val json = Json.encodeToString(track)
                buttonSearchIntent.putExtra(TRACK, json)
                startActivity(buttonSearchIntent)
            }
        }

        adapterForHistoryTracks = TrackAdapter(history.getItems(), clickListener)

        rvTrackHist?.adapter = adapterForHistoryTracks
        rvTrackHist?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackAdapter = TrackAdapter(viewModel.getTracks(), clickListener)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter

        binding.backButtonSearch.setOnClickListener {
            finish()
        }
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.inputEditText.setOnClickListener {
                    it.hideKeyboard()
                }
                viewModel.getTracks().clear()
                trackAdapter.addTracks(viewModel.getTracks())
                viewModel.searchDebounce(binding.inputEditText.text.toString())
                trackAdapter.updateList(viewModel.getTracks())
                Log.d("треки", viewModel.getTracks().toString())
                showView()
            }
            false
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            history.getItems()
            rvTrackHist?.visibility = View.VISIBLE
            adapterForHistoryTracks.updateList(history.getItems())
            trackAdapter.updateList(viewModel.getTracks())
            if (hasFocus && viewModel.getTracks().isEmpty() && history.getItems().isNotEmpty()) {
                showHistoryView()
            } else if ((viewModel.getTracks().isEmpty() && history.getItems().isEmpty())) {
                textViewYourSearch?.visibility = View.INVISIBLE
                clearTrackHistoryBtn?.visibility = View.GONE
                textViewYourSearch?.visibility = View.GONE
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
                    viewModel.getTracks().clear()
                    viewModel.searchDebounce(binding.inputEditText.text.toString())
                    trackAdapter.updateList(viewModel.getTracks())
                }
                if (binding.inputEditText.hasFocus() && binding.inputEditText.text.isEmpty()) {
                    viewModel.getTracks().clear()
                    trackAdapter.updateList(viewModel.getTracks())
                    history.getItems()
                    adapterForHistoryTracks.updateList(history.getItems())
                    rvTrackHist?.visibility = View.VISIBLE
                }
                if (history.getItems().isNotEmpty()) {
                    showHistoryView()
                } else {
                    viewModel.getTracks().clear()
                    trackAdapter.updateList(viewModel.getTracks())
                    clearTrackHistoryBtn?.visibility = View.INVISIBLE
                    textViewYourSearch?.visibility = View.INVISIBLE
                    //здесь Invisible,а не gone, потому что gone отрабатывает не корректно
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.clearIconSearch.setOnClickListener {
            binding.inputEditText.setText("")
            viewModel.getTracks().clear()
            trackAdapter.updateList(viewModel.getTracks())
            binding.recyclerView.visibility = View.GONE
            rvTrackHist
            it.hideKeyboard()
            errorView!!.visibility = View.GONE
            noResultsView!!.visibility = View.GONE

        }
        rvTrackHist?.adapter = adapterForHistoryTracks
        clearTrackHistoryBtn?.setOnClickListener {
            history.clearTrackHistory()
            history.getItems()
            adapterForHistoryTracks.updateList(history.getItems())
            clearTrackHistoryBtn?.visibility = View.GONE
            textViewYourSearch?.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    companion object {
        const val KEY = "someKey"
        const val TRACK = "track"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val TRACK_HISTORY = "track_history_preferences"
    }

    @SuppressLint("SetTextI18n")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(KEY)!!
        binding.inputEditText.setText(text)
    }

    private fun serverError() {
        searchProgressBar?.visibility = View.GONE
        noResultsView!!.visibility = View.GONE
        errorView!!.visibility = View.VISIBLE
        upd?.setOnClickListener {
            viewModel.searchDebounce(binding.inputEditText.text.toString())
        }
    }

    private fun allViewGone() {
        searchProgressBar?.visibility = View.GONE
        rvTrackHist?.visibility = View.GONE
        textViewYourSearch?.visibility = View.GONE
        clearTrackHistoryBtn?.visibility = View.GONE
        textViewYourSearch?.visibility = View.INVISIBLE
    }

    private fun loading() {
        searchProgressBar?.visibility = View.GONE
        textViewYourSearch?.visibility = View.INVISIBLE
        noResultsView!!.visibility = View.GONE
        searchProgressBar?.visibility = View.VISIBLE
    }

    private fun showHistoryView() {
        clearTrackHistoryBtn?.visibility = View.VISIBLE
        textViewYourSearch?.visibility = View.VISIBLE
    }

    private fun noResults() {
        searchProgressBar?.visibility = View.INVISIBLE
        textViewYourSearch?.visibility = View.INVISIBLE
        noResultsView!!.visibility = View.VISIBLE
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showView() {
        viewModel.activityStateLiveData().observe(this@SearchActivity) { SearchActivityState ->
            when (SearchActivityState!!) {
                com.example.playlistmaker.search.domain.models.SearchActivityState.SEARCH_TRACKS -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    trackAdapter.updateList(viewModel.getTracks())
                    textViewYourSearch?.visibility = View.GONE
                    clearTrackHistoryBtn?.visibility = View.GONE
                    searchProgressBar?.visibility = View.GONE
                }

                com.example.playlistmaker.search.domain.models.SearchActivityState.SEARCH_HISTORY -> {
                    showHistoryView()
                }

                com.example.playlistmaker.search.domain.models.SearchActivityState.LOADING -> {
                    loading()
                }

                com.example.playlistmaker.search.domain.models.SearchActivityState.NO_RESULTS -> {
                    noResults()
                }

                com.example.playlistmaker.search.domain.models.SearchActivityState.SERVER_ERROR -> {
                    serverError()
                }
            }
        }
    }

    private fun findBiId() {
        noResultsView = findViewById(R.id.no_results_search_include)
        errorView = findViewById(R.id.server_error_include)
        trackItem = findViewById(R.id.track_layout)
        clearTrackHistoryBtn = findViewById(R.id.clear_track_history)
        rvTrackHist = findViewById(R.id.recyclerViewHist)
        textViewYourSearch = findViewById(R.id.text_view_your_search)
        upd = findViewById(R.id.update_search_server_error)
        searchProgressBar = findViewById(R.id.progress_bar_search_activity)
    }
}

