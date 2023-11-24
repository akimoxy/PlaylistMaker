package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search_button_main)

        buttonSearch.setOnClickListener {
            val buttonSearchIntent = Intent(this, SearchActivity::class.java)
            startActivity(buttonSearchIntent)
        }


        val buttonMediateka = findViewById<Button>(R.id.mediateka_button)
        buttonMediateka.setOnClickListener {
            val buttonMediatekaIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(buttonMediatekaIntent)
        }

        val buttonSettings = findViewById<Button>(R.id.settings_button)
        buttonSettings.setOnClickListener {
            val buttonSettingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(buttonSettingsIntent)
        }


    }
}
