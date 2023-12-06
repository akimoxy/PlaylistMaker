package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private var text = ""
    // private lateinit var rvTrack5: RecyclerView
    //private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val buttonBackInSettings = findViewById<Button>(R.id.back_button_search)
        buttonBackInSettings.setOnClickListener {
            finish()
        }
        inputEditText = findViewById(R.id.input_edit_text)
        val clearButton = findViewById<Button>(R.id.clear_icon_search)
        fun View.hideKeyboard() {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(windowToken, 0)
        }
        clearButton.setOnClickListener {
            inputEditText.setText("")
            it.hideKeyboard()
        }
        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
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

        val trackName1 = getString(R.string.trackName1)
        val trackName2 = getString(R.string.trackName2)
        val trackName3 = getString(R.string.trackName3)
        val trackName4 = getString(R.string.trackName4)
        val trackName5 = getString(R.string.trackName5)

        val artistName1 = getString(R.string.artistName1)
        val artistName2 = getString(R.string.artistName2)
        val artistName3 = getString(R.string.artistName3)
        val artistName4 = getString(R.string.artistName4)
        val artistName5 = getString(R.string.artistName5)

        val trackTime1 = getString(R.string.trackTime1)
        val trackTime2 = getString(R.string.trackTime2)
        val trackTime3 = getString(R.string.trackTime3)
        val trackTime4 = getString(R.string.trackTime4)
        val trackTime5 = getString(R.string.trackTime5)

        val artworkUrl1 = getString(R.string.artworkUrl1)
        val artworkUrl2 = getString(R.string.artworkUrl2)
        val artworkUrl3 = getString(R.string.artworkUrl3)
        val artworkUrl4 = getString(R.string.artworkUrl4)
        val artworkUrl5 = getString(R.string.artworkUrl5)

        //      val artistNameList =
        //          arrayOf(artistName1, artistName2, artistName3, artistName4, artistName5)
        //     val trackNameList = arrayOf(trackName1, trackName2, trackName3, trackName4, trackName5)
        //      val trackTimeList = arrayOf(trackTime1, trackTime2, trackTime3, trackTime4, trackTime5)
        //    val imageUrlList = arrayOf(artworkUrl1, artworkUrl2, artworkUrl3, artworkUrl4, artworkUrl5)

        val track1 = Track(
            trackName1, artistName1, trackTime1, artworkUrl1
        )
        val track2 = Track(
            trackName2, artistName2, trackTime2, artworkUrl2
        )
        val track3 = Track(
            trackName3, artistName3, trackTime3, artworkUrl3
        )
        val track4 = Track(
            trackName4, artistName4, trackTime4, artworkUrl4
        )
        val track5 = Track(
            trackName5, artistName5, trackTime5, artworkUrl5
        )
        val arrayOfTrack = TrackAdapter(arrayListOf(track1, track2, track3, track4, track5))
        //    initial()
        val rvTrack = findViewById<RecyclerView>(R.id.recyclerView)
        rvTrack.adapter = arrayOfTrack
//binding= ActivityMainBinding.inflate(layoutInflater)
        //     setContentView(binding.root)
        //initial()
    }
    //end of create()

    //  fun initial() {
    //      rvTrack5=binding.rv
    // }

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

}







