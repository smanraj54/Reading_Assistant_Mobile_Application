package com.example.readingassistant.fragments

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.example.readingassistant.R
import com.example.readingassistant.model.SpeedControl
import com.example.readingassistant.databinding.FragmentMediaPlayerBinding
import java.io.File

class MediaPlayerFragment : Fragment() {

    private var _binding: FragmentMediaPlayerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private val seekBarHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBar: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var audio:Uri
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        pauseButton.isVisible = false

        val fastForwardButton: ImageButton = view.findViewById(R.id.fastForwardButton)
        val rewindButton: ImageButton = view.findViewById(R.id.rewindButton)
        val increaseButton: Button = view.findViewById(R.id.increaseButton)
        val decreaseButton: Button = view.findViewById(R.id.decreaseButton)
        val seekBar: SeekBar = view.findViewById(R.id.seekBar)
        val speedControl: SpeedControl = SpeedControl(DoubleArray(7){0.5 +(it*0.25)})

        setFragmentResultListener("mediaPlayerDocument") {requestKey, bundle ->
            binding.mediaPlayerDocumentTitle.text = bundle.getString("title")
            binding.mediaPlayerDocumentText.text = bundle.getString("text")
            var audioPath = bundle.getString("audioPath")
            audio = Uri.fromFile(File(audioPath))
            if (audio != null) {
                setupMediaPlayer(audio, seekBar)
                setupPlayButton(seekBar)
           }
        }

        setupPauseButton()
        setupSpeedButtons(speedControl, increaseButton, decreaseButton)
        setupFastForward(fastForwardButton)
        setupRewindButton(rewindButton)
        setupSeekBar(seekBar)
    }

    private fun setupMediaPlayer(audio: Uri, seekBar: SeekBar) {
        mediaPlayer = MediaPlayer.create(activity?.applicationContext, audio)
        seekBar.max = mediaPlayer.duration

        updateSeekBar = object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                seekBarHandler.postDelayed(this, 1000)
            }
        }
        seekBarHandler.post(updateSeekBar)
    }

    override fun onDestroyView() {
        println("view destroyed")
        seekBarHandler.removeCallbacks(updateSeekBar)
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        pause()
        super.onPause()
    }

    private fun setupPlayButton(seekBar: SeekBar) {
        //set up play button listener
        playButton.setOnClickListener {
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
            if (mediaPlayer.isPlaying) {
                println(DoubleArray(8) { 0.5 + (it * 0.25) })
                speedControl.increaseSpeed()
                params.speed = speedControl.getCurrentSpeed().toFloat()
                mediaPlayer.playbackParams = params
                updateSpeedButtons(speedControl, increaseButton, decreaseButton)
            }
        }

        //set up decrease speed button listener
        decreaseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                speedControl.decreaseSpeed()
                params.speed = speedControl.getCurrentSpeed().toFloat()
                mediaPlayer.playbackParams = params
                updateSpeedButtons(speedControl, increaseButton, decreaseButton)
            }
        }
    }

    private fun updateSpeedButtons(speedControl: SpeedControl, increaseButton:Button, decreaseButton:Button) {
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
    }

    private fun setupRewindButton(rewindButton: ImageButton) {
        //setup rewind button listener
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
    }

    private fun setupSeekBar(seekBar: SeekBar) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    mediaPlayer.seekTo(p1)
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

    private fun pause(){
        mediaPlayer.pause()
        playButton.isVisible = true
        pauseButton.isVisible = false
    }

    private fun play(){
        mediaPlayer.start()
        playButton.isVisible = false
        pauseButton.isVisible = true
    }
}