package com.example.readingassistant.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.example.readingassistant.databinding.FragmentTtsBinding
import java.io.File
import java.util.*

class TTSFragment : Fragment() {

    private var _binding: FragmentTtsBinding? = null
    private lateinit var tts: TextToSpeech

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.speakButton.isEnabled = false

        //for testing purposes, replace with actual text source
        var title = "title"
        var text = ""
        for (i in 0..50) {
            text += "sample text "
        }
        //end

        val path = activity?.filesDir?.absolutePath+"/audio.mp3"

        tts = TextToSpeech(activity, object: TextToSpeech.OnInitListener{
            override fun onInit(p0: Int) {
                if (p0 == TextToSpeech.SUCCESS) {
                    tts.language = Locale.CANADA
                    tts.synthesizeToFile(text,null, File(path),"audio.mp3")
                }
            }
        })

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
            }

            override fun onDone(utteranceId: String) {
                activity?.runOnUiThread {
                    binding.speakButton.isEnabled = true
                }
            }

            override fun onError(utteranceId: String) {
                Log.e("TTS error",utteranceId)
            }
        })

        binding.speakButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("text",text)
            bundle.putString("title",title)
            bundle.putString("audioPath",path)

            setFragmentResult("mediaPlayerDocument",bundle)
            findNavController().navigate(R.id.action_TTSFragment_to_MediaPlayerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}