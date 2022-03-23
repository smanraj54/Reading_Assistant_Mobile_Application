package com.example.readingassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import java.util.*

import android.content.Intent
import java.io.File


class TTSExampleActivity : AppCompatActivity() {

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttsexample)
        var button: Button = findViewById(R.id.speakButton)

        tts = TextToSpeech(applicationContext, object: TextToSpeech.OnInitListener{
            override fun onInit(p0: Int) {
                println("attn")
                println(p0)
                if (p0 == TextToSpeech.SUCCESS) {
                    tts.language = Locale.CANADA
                }
            }
        })

        button.setOnClickListener {

            val textBox:EditText = findViewById(R.id.textBox)
            val titlebox:EditText = findViewById(R.id.titleBox)
            val text = textBox.text.toString()
            val title = titlebox.text.toString()
            val audio = tts.synthesizeToFile(text,null, File(cacheDir, "audio.mp3"),"audio")
println("audio int")
            println(audio)
            val intent = Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra("audioFilePath",audio)
                putExtra("text",text)
                putExtra("title",title)
            }
            startActivity(intent)
        }
    }
}