package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
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
    private lateinit var clearButton: Button
    private var tracks = ArrayList<Track>()
    private lateinit var errorView: View
    private lateinit var noResultsView: View
    lateinit var arrayOfTrack: TrackAdapter
    private val iTunsBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunsBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val buttonBackInSettings = findViewById<Button>(R.id.back_button_search)
        buttonBackInSettings.setOnClickListener {
            finish()
        }
        noResultsView = findViewById(R.id.no_results_search_include)
        errorView = findViewById(R.id.server_error_include)
        inputEditText = findViewById(R.id.input_edit_text)
        arrayOfTrack = TrackAdapter(tracks)
        val rvTrack = findViewById<RecyclerView>(R.id.recyclerView)
        rvTrack.adapter = arrayOfTrack
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputEditText.setOnClickListener {
                    it.hideKeyboard()
                }
                search()
            }
            false
        }
        rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        clearButton = findViewById(R.id.clear_icon_search)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            it.hideKeyboard()
            errorView.visibility = View.GONE
            noResultsView.visibility = View.GONE
        }
        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
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
                    arrayOfTrack.notifyDataSetChanged()
                    if (tracks.isEmpty() || response.code() == 200) {
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
        noResultsView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        val upd = findViewById<Button>(R.id.update_search_server_error)
        upd.setOnClickListener {
            search()
        }
    }

    fun noResults() {
        noResultsView.visibility = View.VISIBLE
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

