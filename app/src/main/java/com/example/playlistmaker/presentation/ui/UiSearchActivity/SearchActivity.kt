package com.example.playlistmaker.presentation.ui.UiSearchActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TrackResponseDomain
import com.example.playlistmaker.presentation.ui.UiAudioPlayerActivity.AudioPlayerActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private var text = ""
    private var tracks = ArrayList<Track>()
    private var errorView: View? = null
    private var noResultsView: View? = null
    private var textViewYourSearch: View? = null
    private var searchProgressBar: View? = null
    private var trackItem: View? = null
    private var clearButton: Button? = null
    private var upd: Button? = null
    private var clearTrackHistoryBtn: Button? = null
    private var buttonBackInSettings: Button? = null
    private var rvTrack: RecyclerView? = null
    private var rvTrackHist: RecyclerView? = null
    lateinit var trackAdapter: TrackAdapter
    private lateinit var adapterForHistoryTracks: TrackAdapter
    private lateinit var sharedPr: SharedPreferences
    private val serverCode200 = 200
    private val serverCode400 = 400
    private lateinit var clickListener: RecyclerViewEvent
    private lateinit var trackInteractor: TrackInteractor
    lateinit var history: TrackHistoryInteractor
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var consume: TrackInteractor.Consumer<TrackResponseDomain>
    private val searchRunnable = Runnable { search(inputEditText.text.toString()) }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findBiId()

        sharedPr = getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)

        history = Creator.getTrackHistory(sharedPr)

        trackInteractor = Creator.provideTrackInteractor()

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
        consume = object : TrackInteractor.Consumer<TrackResponseDomain> {
            @SuppressLint("NotifyDataSetChanged")
            override fun consume(foundTracks: TrackResponseDomain) {
                handler.post {
                    if (inputEditText.text.isNotEmpty()) {
                        tracks.clear()
                        tracks.addAll(foundTracks.result)
                        trackAdapter.addTracks(tracks)
                        textViewYourSearch?.visibility = View.GONE
                        clearTrackHistoryBtn?.visibility = View.GONE
                        searchProgressBar?.visibility = View.GONE
                    }
                    if (tracks.isEmpty() && foundTracks.result.isEmpty() && foundTracks.resulCode == serverCode200) {
                        noResults()
                    } else if (foundTracks.resulCode == serverCode400) {
                        searchProgressBar?.visibility = View.GONE
                        serverError()
                    }
                }
            }
        }
        adapterForHistoryTracks = TrackAdapter(history.getItems(), clickListener)

        rvTrackHist?.adapter = adapterForHistoryTracks
        rvTrackHist?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackAdapter = TrackAdapter(tracks, clickListener)
        rvTrack?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTrack?.adapter = trackAdapter

        buttonBackInSettings?.setOnClickListener {
            finish()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputEditText.setOnClickListener {
                    it.hideKeyboard()
                }
                searchDebounce()
            }
            false
        }
        rvTrackHist?.visibility = View.GONE
        textViewYourSearch?.visibility = View.GONE
        clearTrackHistoryBtn?.visibility = View.GONE
        textViewYourSearch?.visibility = View.INVISIBLE
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            history.getItems()
            rvTrackHist?.visibility = View.VISIBLE
            adapterForHistoryTracks.updateList(history.getItems())
            trackAdapter.updateList(tracks)
            if (hasFocus && tracks.isEmpty() && history.getItems().isNotEmpty()) {
                clearTrackHistoryBtn?.visibility = View.VISIBLE
                textViewYourSearch?.visibility = View.VISIBLE
            } else if ((tracks.isEmpty() && history.getItems().isEmpty())) {
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
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("WrongConstant")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text = p0.toString()
                clearButton!!.visibility = clearButtonVisibility(p0)
                if (inputEditText.text.isNotEmpty()) {
                    searchDebounce()
                }
                if (inputEditText.hasFocus() && inputEditText.text.isEmpty()) {
                    tracks.clear()
                    trackAdapter.updateList(tracks)
                    history.getItems()
                    adapterForHistoryTracks.updateList(history.getItems())
                    rvTrackHist?.visibility = View.VISIBLE
                    if (history.getItems().isNotEmpty()) {
                        clearTrackHistoryBtn?.visibility = View.VISIBLE
                        textViewYourSearch?.visibility = View.VISIBLE
                    } else {
                        tracks.clear()
                        trackAdapter.updateList(tracks)
                        clearTrackHistoryBtn?.visibility = View.INVISIBLE
                        textViewYourSearch?.visibility = View.INVISIBLE
                        //здесь Invisible,а не gone, потому что gone отрабатывает не корректно
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        clearButton!!.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            trackAdapter.updateList(tracks)
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

    private fun search(text: String) {
        trackInteractor.searchTrack(text, consume)
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
        inputEditText.setText(text)
    }

    fun serverError() {
        noResultsView!!.visibility = View.GONE
        errorView!!.visibility = View.VISIBLE
        upd?.setOnClickListener {
            search(inputEditText.text.toString())
        }
    }

    fun noResults() {
        textViewYourSearch?.visibility = View.INVISIBLE
        noResultsView!!.visibility = View.VISIBLE
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun findBiId() {
        clearButton = findViewById(R.id.clear_icon_search)
        noResultsView = findViewById(R.id.no_results_search_include)
        errorView = findViewById(R.id.server_error_include)
        inputEditText = findViewById(R.id.input_edit_text)
        trackItem = findViewById(R.id.track_layout)
        clearTrackHistoryBtn = findViewById(R.id.clear_track_history)
        buttonBackInSettings = findViewById(R.id.back_button_search)
        rvTrack = findViewById(R.id.recyclerView)
        rvTrackHist = findViewById(R.id.recyclerViewHist)
        textViewYourSearch = findViewById(R.id.text_view_your_search)
        upd = findViewById(R.id.update_search_server_error)
        searchProgressBar = findViewById(R.id.progress_bar_search_activity)
    }

    private fun searchDebounce() {
        tracks.clear()
        trackAdapter.updateList(tracks)
        rvTrackHist?.visibility = View.GONE
        clearTrackHistoryBtn?.visibility = View.GONE
        textViewYourSearch?.visibility = View.INVISIBLE
        searchProgressBar?.visibility = View.VISIBLE
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
