package com.example.readingassistant

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible

class MediaPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)


        val playButton: ImageButton = findViewById(R.id.playButton)
        val pauseButton: ImageButton = findViewById(R.id.pauseButton)
        val fastForwardButton: ImageButton = findViewById(R.id.fastForwardButton)
        val rewindButton: ImageButton = findViewById(R.id.rewindButton)
        val seekBar: SeekBar = findViewById(R.id.seekBar)


        var mediaPlayer: MediaPlayer? = null
        var testAudio = R.raw.lofi_study_music;

        pauseButton.isVisible = false

        //https://www.youtube.com/watch?v=a3yLc9J0hGE&ab_channel=CodePalace
        playButton.setOnClickListener {
            println("pressed play")

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this,testAudio) //change to accept file
                seekBar.max = mediaPlayer!!.duration


                val handler: Handler = Handler() //fix
                val runnable = object : Runnable {
                    override fun run() {
                        seekBar.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(this,1000)
                    }
                }
                handler.post(runnable)
            }



            mediaPlayer?.start()

            playButton.isVisible = false
            pauseButton.isVisible = true
        }

        pauseButton.setOnClickListener {
            println("pressed pause")

            mediaPlayer?.pause()

            playButton.isVisible = true
            pauseButton.isVisible = false
        }

        fastForwardButton.setOnClickListener {
            println("pressed fastforward")
        }

        rewindButton.setOnClickListener {
            println("pressed rewind")
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    mediaPlayer?.seekTo(p1)
                }

                if (p1 == seekBar.max) {
                    if (!p2) {
                        seekBar.progress = 0
                    }
                    playButton.isVisible = true
                    pauseButton.isVisible = false
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

}
