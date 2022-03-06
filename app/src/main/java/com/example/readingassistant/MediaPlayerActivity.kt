package com.example.readingassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.view.isVisible

class MediaPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        val playButton: ImageButton = findViewById(R.id.playButton)
        val pauseButton: ImageButton = findViewById(R.id.pauseButton)
        val fastForwardButton: ImageButton = findViewById(R.id.fastForwardButton)
        val rewindButton: ImageButton = findViewById(R.id.rewindButton)

        pauseButton.isVisible = false

        playButton.setOnClickListener {
            println("pressed play")

            playButton.isVisible = false
            pauseButton.isVisible = true
        }

        pauseButton.setOnClickListener {
            println("pressed pause")

            playButton.isVisible = true
            pauseButton.isVisible = false
        }

        fastForwardButton.setOnClickListener {
            println("pressed fastforward")
        }

        rewindButton.setOnClickListener {
            println("pressed rewind")
        }
    }

}
