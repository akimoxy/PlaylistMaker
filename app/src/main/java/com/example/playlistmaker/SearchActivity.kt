package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private var text = ""
    private var tracks = ArrayList<Track>()
    private var errorView: View? = null
    private var noResultsView: View? = null
    private var textViewYourSearch: View? = null
    private var trackItem: View? = null
    private var clearButton: Button? = null
    private var upd: Button? = null
    private var clearTrackHistoryBtn: Button? = null
    private var buttonBackInSettings: Button? = null
    private var rvTrack: RecyclerView? = null
    private var rvTrackHist: RecyclerView? = null
    lateinit var trackAdapter: TrackAdapter
    private lateinit var adapterForHistoryTracks: TrackAdapter
    lateinit var history: SearchHistory
    private val iTunsBaseUrl = "https://itunes.apple.com"
    private lateinit var sharedPr: SharedPreferences
    private val serverCode200 = 200
    private lateinit var clickListener: RecyclerViewEvent
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunsBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findBiId()

        sharedPr = getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
        history = SearchHistory(sharedPr)
        clickListener = object : RecyclerViewEvent {
            override fun onItemClick(track: Track) {
                history.saveTrack(track)

            }

        }
        adapterForHistoryTracks = TrackAdapter(history.trackHistoryArray, clickListener)

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
                search()
            }
            false
        }
        textViewYourSearch?.visibility = View.GONE
        clearTrackHistoryBtn?.visibility = View.GONE
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            history.getItems()
            adapterForHistoryTracks.updateList(history.trackHistoryArray)
            if (hasFocus && tracks.isEmpty() && history.trackHistoryArray.isNotEmpty()) {
                clearTrackHistoryBtn?.visibility = View.VISIBLE
                textViewYourSearch?.visibility = View.VISIBLE
            } else if ((tracks.isEmpty() && history.trackHistoryArray.isEmpty())) {
                clearTrackHistoryBtn?.visibility = View.GONE
                textViewYourSearch?.visibility = View.GONE
            }
        }
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (inputEditText.hasFocus() && inputEditText.text.isEmpty()) {
                    history.getItems()
                    adapterForHistoryTracks.updateList(history.trackHistoryArray)
                    if (history.trackHistoryArray.isNotEmpty()) {
                        clearTrackHistoryBtn?.visibility = View.VISIBLE
                        textViewYourSearch?.visibility = View.VISIBLE
                    } else {
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
        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        rvTrackHist?.adapter = adapterForHistoryTracks
        clearTrackHistoryBtn?.setOnClickListener {
            history.clearTrackHistory()
            history.getItems()
            adapterForHistoryTracks.updateList(history.trackHistoryArray)
            clearTrackHistoryBtn?.visibility = View.GONE
            textViewYourSearch?.visibility = View.GONE
        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) { // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()
                clearButton!!.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun search() {
        iTunesService.findTrack(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    tracks.clear()
                    tracks.addAll(response.body()?.results!!)
                    trackAdapter.addTracks(tracks)
                    textViewYourSearch?.visibility = View.GONE
                    clearTrackHistoryBtn?.visibility = View.GONE
                    if (tracks.isEmpty() || response.code() == serverCode200) {
                        noResults()
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    serverError()
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    companion object {
        const val KEY = "someKey"
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
            search()
        }
    }

    fun noResults() {
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
    }
}

