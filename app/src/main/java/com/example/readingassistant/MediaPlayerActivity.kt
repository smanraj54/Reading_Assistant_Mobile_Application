package com.example.readingassistant

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.view.isVisible

class MediaPlayerActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    lateinit var playButton: ImageButton
    lateinit var pauseButton: ImageButton

    @SuppressLint("ClickableViewAccessibility") //fix this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        val fastForwardButton: ImageButton = findViewById(R.id.fastForwardButton)
        val rewindButton: ImageButton = findViewById(R.id.rewindButton)
        val seekBar: SeekBar = findViewById(R.id.seekBar)

        var testAudio = R.raw.lofi_study_music ///change


        pauseButton.isVisible = false


        playButton.setOnClickListener {
            if (mediaPlayer == null) {

               mediaPlayer = MediaPlayer.create(this, testAudio) //change to accept file
               seekBar.max = mediaPlayer!!.duration

                val handler: Handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        seekBar.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(this, 1000)
                    }
                }
                handler.post(runnable)
            }
            mediaPlayer!!.start()

            playButton.isVisible = false
            pauseButton.isVisible = true
        }

        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
            playButton.isVisible = true
            pauseButton.isVisible = false
        }

        var fastForwarding: Boolean
        fastForwardButton.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                fastForwarding = true
                val handler: Handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        if (fastForwarding) {
                            if (mediaPlayer!!.currentPosition + 2000 >= mediaPlayer!!.duration) {
                                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
                            } else {
                                mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.plus(2000))
                                handler.postDelayed(this, 500)
                            }
                        }
                    }
                }
                handler.post(runnable)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                fastForwarding = false
            }
            true
        }

        var rewinding: Boolean
        rewindButton.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                rewinding = true
                val handler: Handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        if (rewinding) {
                            mediaPlayer?.seekTo(mediaPlayer!!.currentPosition.minus(2000))
                            handler.postDelayed(this, 500)
                        }
                    }
                }
                handler.post(runnable)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                rewinding = false
            }
            true
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

    override fun onPause() {
        pause()
        super.onPause()
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onDestroy()
    }

    fun pause(){
        mediaPlayer?.pause()
        playButton.isVisible = true
        pauseButton.isVisible = false
    }

    fun play(){
        mediaPlayer?.start()
        playButton.isVisible = false
        pauseButton.isVisible = true
    }
}
