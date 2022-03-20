package com.example.readingassistant

import android.media.MediaPlayer
import android.media.PlaybackParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.view.isVisible

class MediaPlayerActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    lateinit var playButton: ImageButton
    lateinit var pauseButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        val testAudio: Int = R.raw.lofi_study_music ///change to get file from intent
        /*
        val arguments = intent.extras
        if (arguments != null) {
            val text = arguments.getString("text")
            val audio = arguments.getString("audio")
            val documentTitle = arguments.getString("title")
        }*/

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.isVisible = false

        val fastForwardButton: ImageButton = findViewById(R.id.fastForwardButton)
        val rewindButton: ImageButton = findViewById(R.id.rewindButton)
        val increaseButton: Button = findViewById(R.id.increaseButton)
        val decreaseButton: Button = findViewById(R.id.decreaseButton)
        val seekBar: SeekBar = findViewById(R.id.seekBar)
        val speedControl:SpeedControl = SpeedControl(DoubleArray(7){0.5 +(it*0.25)})

        setupPlayButton(testAudio, seekBar)
        setupPauseButton()
        setupSpeedButtons(speedControl, increaseButton, decreaseButton)
        setupFastForward(fastForwardButton)
        setupRewindButton(rewindButton)
        setupSeekBar(seekBar)
    }

    private fun setupPlayButton(testAudio: Int, seekBar: SeekBar) {
        //set up play button listener
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
            play()
        }
    }

    private fun setupPauseButton() {
        pauseButton.setOnClickListener {
            pause()
        }
    }

    private fun setupSpeedButtons(speedControl: SpeedControl, increaseButton: Button, decreaseButton: Button) {
        updateSpeedButtons(speedControl, increaseButton, decreaseButton)
        val params: PlaybackParams = PlaybackParams().setSpeed(1F)

        //set up increase speed button listener
        increaseButton.setOnClickListener {
            if (mediaPlayer != null) {
                println(DoubleArray(8) { 0.5 + (it * 0.25) })
                speedControl.increaseSpeed()
                params.speed = speedControl.getCurrentSpeed().toFloat()
                mediaPlayer!!.playbackParams = params
                updateSpeedButtons(speedControl, increaseButton, decreaseButton)
            }
        }

        //set up decrease speed button listener
        decreaseButton.setOnClickListener {
            if (mediaPlayer != null) {
                speedControl.decreaseSpeed()
                params.speed = speedControl.getCurrentSpeed().toFloat()
                mediaPlayer!!.playbackParams = params
                updateSpeedButtons(speedControl, increaseButton, decreaseButton)
            }
        }
    }


    private fun updateSpeedButtons(speedControl:SpeedControl, increaseButton:Button, decreaseButton:Button) {
        if (speedControl.getMaxSpeed() == speedControl.getCurrentSpeed()) {
            increaseButton.setText("max")
        } else {
            increaseButton.setText("x${speedControl.getHigherSpeed()}")
        }

        if (speedControl.getMinSpeed() == speedControl.getCurrentSpeed()) {
            decreaseButton.setText("min")
        } else {
            decreaseButton.setText("x${speedControl.getLowerSpeed()}")
        }
    }

    private fun setupFastForward(fastForwardButton: ImageButton) {
        //setup fast forward button listener
        var fastForwarding: Boolean
        fastForwardButton.setOnTouchListener { _, motionEvent ->
            if (mediaPlayer != null) {
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
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                fastForwarding = false
            }
            true
        }
    }

    private fun setupRewindButton(rewindButton: ImageButton) {
        //setup rewind button listener
        var rewinding: Boolean
        rewindButton.setOnTouchListener { _, motionEvent ->
            if (mediaPlayer != null) {
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
            }
            true
        }
    }

    private fun setupSeekBar(seekBar: SeekBar) {
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

    private fun pause(){
        mediaPlayer?.pause()
        playButton.isVisible = true
        pauseButton.isVisible = false
    }

    private fun play(){
        mediaPlayer?.start()
        playButton.isVisible = false
        pauseButton.isVisible = true
    }
}
