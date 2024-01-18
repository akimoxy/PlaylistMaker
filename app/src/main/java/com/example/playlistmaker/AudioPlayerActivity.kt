package com.example.playlistmaker

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

       val backBtnAudioPlayer=findViewById<Button>(R.id.back_button_audio_player)
        backBtnAudioPlayer?.setOnClickListener {
            finish()
        }

    //    intent.let {
    //        var track=  intent.extras?.getParcelable(TRACK)!! as Track
    //    }
   //     if (Build.VERSION.SDK_INT >= 33) { // TIRAMISU
   //         data = intent.getParcelableExtra (String name, Class<T> clazz)
   //     }else{
   //         data = intent.getParcelableExtra("")
   //     }





    }

}