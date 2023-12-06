package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBackInSettings = findViewById<Button>(R.id.back_button)
        buttonBackInSettings.setOnClickListener {
            finish()
        }
        AppCompatDelegate.getDefaultNightMode()

        val switchDarkTheme = findViewById<Switch>(R.id.night_theme_switch_settings)
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        val linkToAndroidCourseString = getString(R.string.link_to_android_course)
        val buttonShareSettings = findViewById<Button>(R.id.share_button_settings)
        buttonShareSettings.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                linkToAndroidCourseString,
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        val recipientEmailAdress = getString(R.string.recipient_email)
        val subjectEmail = getString(R.string.subject_email)
        val mailText = getString(R.string.mail_text)
        val buttonWriteToSupport = findViewById<Button>(R.id.write_to_support_button)

        buttonWriteToSupport.setOnClickListener {
            val intent = Intent().apply {
                intent.action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, recipientEmailAdress)
                putExtra(Intent.EXTRA_SUBJECT, subjectEmail)
                putExtra(Intent.EXTRA_TEXT, mailText)
            }
            startActivity(intent)
        }
        val buttonTermOfUse = findViewById<Button>(R.id.terms_of_use_button)
        buttonTermOfUse.setOnClickListener {
            val url = getString(R.string.url_term_of_use)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}



