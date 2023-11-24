package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val buttonBackInSettings = findViewById<Button>(R.id.back_button_search)
        buttonBackInSettings.setOnClickListener {
            finish()
        }
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val clearButton = findViewById<Button>(R.id.clear_icon_search)

        clearButton.setOnClickListener {
            inputEditText.setText("")
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
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val text = inputEditText.text.toString()
        super.onSaveInstanceState(outState)
        outState.putString(KEY, text)
    }

    companion object {
        const val KEY = "someKey"
    }

    @SuppressLint("SetTextI18n")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val text = savedInstanceState.getString(KEY)
        inputEditText.setText(text)
    }
}







