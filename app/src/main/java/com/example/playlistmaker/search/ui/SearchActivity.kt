package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.AudioPlayerActivity
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.TRACK
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

const val KEY = "someKey"
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
    private val viewModel  by viewModel <SearchViewModel>()
    private var arrayList: ArrayList<Track> = arrayListOf()
    private var arrayListHistory: ArrayList<Track> = arrayListOf()

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findBiId()
        allViewGone()


        clickListener = object : RecyclerViewEvent {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemClick(track: Track) {
                viewModel.saveTrackToHistory(track)
                val buttonSearchIntent =
                    Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                val json = Json.encodeToString(track)
                buttonSearchIntent.putExtra(TRACK, json)
                startActivity(buttonSearchIntent)
            }
        }

        adapterForHistoryTracks =
            TrackAdapter(viewModel.getHistoryItems(), clickListener)

        rvTrackHist?.adapter = adapterForHistoryTracks
        rvTrackHist?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackAdapter = TrackAdapter(arrayList, clickListener)
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
                showView()
                arrayList.clear()
                trackAdapter.addTracks(arrayList)
                viewModel.searchDebounce(binding.inputEditText.text.toString())
                trackAdapter.updateList(arrayList)
            }
            false
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            rvTrackHist?.visibility = View.VISIBLE
            adapterForHistoryTracks.updateList(viewModel.getHistoryItems())
            trackAdapter.updateList(arrayList)
            if (hasFocus && arrayList.isEmpty() && viewModel.getHistoryItems()
                    .isNotEmpty()
            ) {
                showHistoryView()
            } else if ((arrayList.isEmpty() && viewModel.getHistoryItems()
                    .isEmpty())
            ) {
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
                    rvTrackHist?.visibility = View.VISIBLE
                }
                if (viewModel.getHistoryItems().isNotEmpty()) {
                    showHistoryView()
                } else {
                    arrayList.clear()
                    trackAdapter.updateList(arrayList)
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
            arrayList.clear()
            trackAdapter.updateList(arrayList)
            binding.recyclerView.visibility = View.GONE
            rvTrackHist
            it.hideKeyboard()
            errorView!!.visibility = View.GONE
            noResultsView!!.visibility = View.GONE

        }
        rvTrackHist?.adapter = adapterForHistoryTracks
        clearTrackHistoryBtn?.setOnClickListener {
            viewModel.clearTrackHistory()
            viewModel.getHistoryItems()
            adapterForHistoryTracks.updateList(viewModel.getHistoryItems())
            clearTrackHistoryBtn?.visibility = View.GONE
            textViewYourSearch?.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    companion object {

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
        rvTrackHist!!.visibility = View.GONE
        clearTrackHistoryBtn?.visibility = View.GONE
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
        viewModel.activityStateLiveData().observe(this) {
            when (it) {
                is SearchActivityState.SearchTracks -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    arrayList = it.history as ArrayList<Track>
                    trackAdapter.updateList(arrayList)
                    textViewYourSearch?.visibility = View.GONE
                    clearTrackHistoryBtn?.visibility = View.GONE
                    searchProgressBar?.visibility = View.GONE
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
